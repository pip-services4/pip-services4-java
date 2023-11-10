package org.pipservices4.observability.count;

/**
 * Interface for a callback to end measurement of execution elapsed time.
 * 
 * @see CounterTiming
 */
public interface ICounterTimingCallback {
	/**
	 * Ends measurement of execution elapsed time and updates specified counter.
	 * 
	 * @param name    a counter name
	 * @param elapsed execution elapsed time in milliseconds to update the counter.
	 * 
	 * @see CounterTiming#endTiming()
	 */
	void endTiming(String name, float elapsed);
}
