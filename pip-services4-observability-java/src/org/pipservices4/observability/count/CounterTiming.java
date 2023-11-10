package org.pipservices4.observability.count;

/**
 * Callback object returned by {@link ICounters#beginTiming(String)} to end timing
 * of execution block and update the associated counter.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * CounterTiming timing = counters.beginTiming("mymethod.exec_time");
 * try {
 *     ...
 * } finally {
 *   timing.endTiming();
 * }
 * }
 * </pre>
 */
public class CounterTiming {
	private long _start;
	private ICounterTimingCallback _callback;
	private String _counter;

	/**
	 * Creates a new instance of the timing callback object.
	 */
	public CounterTiming() {
	}

	/**
	 * Creates a new instance of the timing callback object.
	 * 
	 * @param counter  an associated counter name
	 * @param callback a callback that shall be called when endTiming is called.
	 */
	public CounterTiming(String counter, ICounterTimingCallback callback) {
		_counter = counter;
		_callback = callback;
		_start = System.currentTimeMillis();
	}

	/**
	 * Ends timing of an execution block, calculates elapsed time and updates the
	 * associated counter.
	 */
	public void endTiming() {
		if (_callback != null) {
			float elapsed = System.currentTimeMillis() - _start;
			_callback.endTiming(_counter, elapsed);
		}
	}
}
