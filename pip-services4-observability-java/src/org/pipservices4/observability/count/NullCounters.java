package org.pipservices4.observability.count;

import java.time.ZonedDateTime;

/**
 * Dummy implementation of performance counters that doesn't do anything.
 * <p>
 * It can be used in testing or in situations when counters is required
 * but shall be disabled.
 *
 * @see ICounters
 */
public class NullCounters implements ICounters {

    /**
     * Creates a new instance of the counter.
     */
    public NullCounters() {
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
        return new CounterTiming();
    }

    /**
     * Calculates min/average/max statistics based on the current and previous
     * values.
     *
     * @param name  a counter name of Statistics type
     * @param value a value to update statistics
     */
    public void stats(String name, float value) {
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
    }

    /**
     * Records the current time as a timestamp.
     *
     * @param name a counter name of Timestamp type.
     */
    public void timestampNow(String name) {
    }

    /**
     * Records the given timestamp.
     *
     * @param name  a counter name of Timestamp type.
     * @param value a timestamp to record.
     */
    public void timestamp(String name, ZonedDateTime value) {
    }

    /**
     * Increments counter by 1.
     *
     * @param name a counter name of Increment type.
     */
    public void incrementOne(String name) {
    }

    /**
     * Increments counter by given value.
     *
     * @param name  a counter name of Increment type.
     * @param value a value to add to the counter.
     */
    public void increment(String name, int value) {
    }
}
