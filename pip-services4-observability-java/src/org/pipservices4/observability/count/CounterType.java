package org.pipservices4.observability.count;

/**
 * Types of counters that measure different types of metrics
 */
public class CounterType {
	/** Counters that measure execution time intervals */
	public final static int Interval = 0;
	/** Counters that keeps the latest measured value */
	public final static int LastValue = 1;
	/** Counters that measure min/average/max statistics */
	public final static int Statistics = 2;
	/** Counter that record timestamps */
	public final static int Timestamp = 3;
	/** Counter that increment counters */
	public final static int Increment = 4;
}
