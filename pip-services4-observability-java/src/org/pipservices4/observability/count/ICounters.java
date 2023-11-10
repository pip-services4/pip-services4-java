package org.pipservices4.observability.count;

import java.time.ZonedDateTime;

/**
 * Interface for performance counters that measure execution metrics.
 * <p>
 * The performance counters measure how code is performing:
 * how fast or slow, how many transactions performed, how many objects
 * are stored, what was the latest transaction time and so on.
 * <p>
 * They are critical to monitor and improve performance, scalability
 * and reliability of code in production. 
 */
public interface ICounters {
	/**
	 * Begins measurement of execution time interval. It returns CounterTiming object which
	 * has to be called at {@link CounterTiming#endTiming()} to end the measurement and
	 * update the counter.
	 * 
	 * @param name a counter name of Interval type.
	 * @return a CounterTiming callback object to end timing.
	 */
	CounterTiming beginTiming(String name);

	/**
	 * Calculates min/average/max statistics based on the current and previous
	 * values.
	 * 
	 * @param name  a counter name of Statistics type
	 * @param value a value to update statistics
	 */
	void stats(String name, float value);

	/**
	 * Records the last calculated measurement value.
	 * 
	 * Usually this method is used by metrics calculated externally.
	 * 
	 * @param name  a counter name of Last type.
	 * @param value a last value to record.
	 */
	void last(String name, float value);

	/**
	 * Records the current time as a timestamp.
	 * 
	 * @param name a counter name of Timestamp type.
	 */
	void timestampNow(String name);

	/**
	 * Records the given timestamp.
	 * 
	 * @param name  a counter name of Timestamp type.
	 * @param value a timestamp to record.
	 */
	void timestamp(String name, ZonedDateTime value);

	/**
	 * Increments counter by 1.
	 * 
	 * @param name a counter name of Increment type.
	 */
	void incrementOne(String name);

	/**
	 * Increments counter by given value.
	 * 
	 * @param name  a counter name of Increment type.
	 * @param value a value to add to the counter.
	 */
	void increment(String name, int value);
}
