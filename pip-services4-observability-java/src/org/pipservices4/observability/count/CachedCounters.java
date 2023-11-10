package org.pipservices4.observability.count;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.errors.InvocationException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of performance counters that measures and stores counters in memory.
 * Child classes implement saving of the counters into various destinations.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>options:
 * <ul>
 *   <li>interval:        interval in milliseconds to save current counters measurements (default: 5 mins)
 *   <li>reset_timeout:   timeout in milliseconds to reset the counters. 0 disables the reset (default: 0)
 * </ul>
 * </ul>
 */
public abstract class CachedCounters implements ICounters, IReconfigurable, ICounterTimingCallback {
    private final Map<String, Counter> _cache = new HashMap<>();
    private boolean _updated = false;
    private long _lastDumpTime = System.currentTimeMillis();
    protected long _lastResetTime = System.currentTimeMillis();
    private long _interval = 300000;
    private final Object _lock = new Object();
    protected long _resetTimeout = 0;

    /**
     * Creates a new CachedCounters object.
     */
    protected CachedCounters() {
    }

    /**
     * Saves the current counters measurements.
     *
     * @param counters current counters measurements to be saves.
     * @throws InvocationException when error occured.
     */
    protected abstract void save(List<Counter> counters) throws InvocationException;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        _interval = config.getAsLongWithDefault("interval", _interval);
        _interval = config.getAsLongWithDefault("options.interval", _interval);
        _resetTimeout = config.getAsLongWithDefault("reset_timeout", _resetTimeout);
        _resetTimeout = config.getAsLongWithDefault("options.reset_timeout", _resetTimeout);
    }

    /**
     * Gets the counters dump/save interval.
     * @return the interval in milliseconds.
     */
    public long getInterval() {
        return _interval;
    }

    /**
     * Gets the counters dump/save interval.
     * @param value the interval in milliseconds.
     */
    public void setInterval(long value) {
        _interval = value;
    }

    /**
     * Clears (resets) a counter specified by its name.
     *
     * @param name a counter name to clear.
     */
    public void clear(String name) {
        synchronized (_lock) {
            _cache.remove(name);
        }
    }

    /**
     * Clears (resets) all counters.
     */
    public void clearAll() {
        synchronized (_lock) {
            _cache.clear();
            _updated = false;
        }
    }

    /**
     * Dumps (saves) the current values of counters.
     *
     * @throws InvocationException when error occured.
     * @see #save(List)
     */
    public void dump() throws InvocationException {
        if (_updated) {
            List<Counter> counters = this.getAll();
            save(counters);

            synchronized (_lock) {
                _updated = false;
                _lastDumpTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Makes counter measurements as updated and dumps them when timeout expires.
     *
     * @see #dump()
     */
    protected void update() {
        _updated = true;
        if (System.currentTimeMillis() > _lastDumpTime + _interval) {
            try {
                dump();
            } catch (InvocationException ex) {
                // Todo: decide what to do
            }
        }
    }

    private void resetIfNeeded() {
        if (_resetTimeout == 0)
            return;

        if (System.currentTimeMillis() - _lastResetTime > _resetTimeout) {
            _cache.clear();
            _updated = false;
            _lastResetTime = System.currentTimeMillis();
        }
    }

    /**
     * Gets all captured counters.
     *
     * @return a list with counters.
     */
    public List<Counter> getAll() {
        synchronized (_lock) {
            resetIfNeeded();
            return new ArrayList<>(_cache.values());
        }
    }

    /**
     * Gets a counter specified by its name. It counter does not exist or its type
     * doesn't match the specified type it creates a new one.
     *
     * @param name a counter name to retrieve.
     * @param type a counter type.
     * @return an existing or newly created counter of the specified type.
     */
    public Counter get(String name, int type) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("Counter name was not set");

        synchronized (_lock) {
            resetIfNeeded();
            Counter counter = _cache.get(name);
            if (counter == null || counter.getType() != type) {
                counter = new Counter(name, type);
                _cache.put(name, counter);
            }

            return counter;
        }
    }

    private void calculateStats(Counter counter, float value) {
        if (counter == null)
            throw new NullPointerException("Counter cannot be null");

        counter.setLast(value);
        counter.setCount(counter.getCount() != null ? counter.getCount() + 1 : 1);
        counter.setMax(counter.getMax() != null ? Math.max(counter.getMax(), value) : value);
        counter.setMin(counter.getMin() != null ? Math.min(counter.getMin(), value) : value);
        counter.setAverage(counter.getAverage() != null && counter.getCount() > 1
                ? ((counter.getAverage() * (counter.getCount() - 1)) + value) / counter.getCount()
                : value);
    }

    /**
     * Begins measurement of execution time interval. It returns CounterTiming object which
     * has to be called at {@link CounterTiming#endTiming()} to end the measurement and
     * update the counter.
     *
     * @param name a counter name of Interval type.
     * @return a CounterTiming callback object to end timing.
     */
    public CounterTiming beginTiming(String name) {
        return new CounterTiming(name, this);
    }

    /**
     * Ends measurement of execution elapsed time and updates specified counter.
     *
     * @param name    a counter name
     * @param elapsed execution elapsed time in milliseconds to update the counter.
     * @see CounterTiming#endTiming()
     */
    public void endTiming(String name, float elapsed) {
        Counter counter = get(name, CounterType.Interval);
        calculateStats(counter, elapsed);
        update();
    }

    /**
     * Calculates min/average/max statistics based on the current and previous
     * values.
     *
     * @param name  a counter name of Statistics type
     * @param value a value to update statistics
     */
    public void stats(String name, float value) {
        Counter counter = get(name, CounterType.Statistics);
        calculateStats(counter, value);
        update();
    }

    /**
     * Records the last calculated measurement value.
     * <p>
     * Usually this method is used by metrics calculated externally.
     *
     * @param name  a counter name of Last type.
     * @param value a last value to record.
     */
    public void last(String name, float value) {
        Counter counter = get(name, CounterType.LastValue);
        counter.setLast(value);
        update();
    }

    /**
     * Records the current time as a timestamp.
     *
     * @param name a counter name of Timestamp type.
     */
    public void timestampNow(String name) {
        timestamp(name, ZonedDateTime.now());
    }

    /**
     * Records the given timestamp.
     *
     * @param name  a counter name of Timestamp type.
     * @param value a timestamp to record.
     */
    public void timestamp(String name, ZonedDateTime value) {
        Counter counter = get(name, CounterType.Timestamp);
        counter.setTime(value != null ? value : ZonedDateTime.now(ZoneId.of("Z")));
        update();
    }

    /**
     * Increments counter by 1.
     *
     * @param name a counter name of Increment type.
     */
    public void incrementOne(String name) {
        increment(name, 1);
    }

    /**
     * Increments counter by given value.
     *
     * @param name  a counter name of Increment type.
     * @param value a value to add to the counter.
     */
    public void increment(String name, int value) {
        Counter counter = get(name, CounterType.Increment);
        counter.setCount(counter.getCount() != null ? counter.getCount() + value : value);
        update();
    }
}
