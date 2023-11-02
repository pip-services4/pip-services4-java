package org.pipservices4.components.exec;

import java.util.*;

import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.run.IClosable;

/**
 * Timer that is triggered in equal time intervals.
 * <p>
 * It has symmetric cross-language implementation
 * and is often used by Pip.Services toolkit to
 * perform periodic processing and cleanup in microservices.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyComponent {
 *   FixedRateTimer timer = new FixedRateTimer(() -> { this.cleanup }, 60000, 0);
 *   ...
 *   public void open(IContext context) {
 *     ...
 *     timer.start();
 *     ...
 *   }
 * 
 *   public void open(IContext context) {
 *     ...
 *     timer.stop();
 *     ...
 *   }
 * 
 *   private void cleanup() {
 *     ...
 *   }
 *   ...
 * }
 * }
 * </pre>
 * @see INotifiable
 */
public class FixedRateTimer implements IClosable {
	private INotifiable _task;
	private long _delay;
	private long _interval;
	private final Timer _timer = new Timer("pip-commons-timer", true);
	private boolean _started = false;
	private final Object _lock = new Object();

	/**
	 * Creates new instance of the timer.
	 */
	public FixedRateTimer() {
	}

	/**
	 * Creates new instance of the timer and sets its values.
	 * 
	 * @param task     (optional) a Notifiable object to call when timer is
	 *                 triggered.
	 * @param interval (optional) an interval to trigger timer in milliseconds.
	 * @param delay    (optional) a delay before the first triggering in
	 *                 milliseconds.
	 * 
	 * @see #setTask(INotifiable)
	 * @see #setInterval(long)
	 * @see #setDelay(long)
	 */
	public FixedRateTimer(INotifiable task, long interval, long delay) {
		_task = task;
		_interval = interval;
		_delay = delay;
	}

	/**
	 * Gets the INotifiable object that receives notifications from this timer.
	 * 
	 * @return the INotifiable object or null if it is not set.
	 */
	public INotifiable getTask() {
		return _task;
	}

	/**
	 * Sets a new INotifiable object to receive notifications from this timer.
	 * 
	 * @param task a INotifiable object to be triggered.
	 */
	public void setTask(INotifiable task) {
		_task = task;
	}

	/**
	 * Gets initial delay before the timer is triggered for the first time.
	 * 
	 * @return the delay in milliseconds.
	 */
	public long getDelay() {
		return _delay;
	}

	/**
	 * Sets initial delay before the timer is triggered for the first time.
	 * 
	 * @param delay a delay in milliseconds.
	 */
	public void setDelay(long delay) {
		_delay = delay;
	}

	/**
	 * Gets periodic timer triggering interval.
	 * 
	 * @return the interval in milliseconds
	 */
	public long getInterval() {
		return _interval;
	}

	/**
	 * Sets periodic timer triggering interval.
	 * 
	 * @param interval an interval in milliseconds.
	 */
	public void setInterval(long interval) {
		_interval = interval;
	}

	/**
	 * Checks if the timer is started.
	 * 
	 * @return true if the timer is started and false if it is stopped.
	 */
	public boolean isStarted() {
		return _started;
	}

	/**
	 * Starts the timer.
	 * <p>
	 * Initially the timer is triggered after delay. After that it is triggered
	 * after interval until it is stopped.
	 * 
	 * @see #stop()
	 */
	public void start() {
		synchronized (_lock) {
			// Stop previously set timer
			_timer.purge();

			// Set a new timer
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					try {
						if(_task != null)
							_task.notify(Context.fromTraceId("pip-commons-timer"), new Parameters());
					} catch (Exception ex) {
						// Ignore or better log!
					}
				}
			};
			_timer.scheduleAtFixedRate(task, _delay, _interval);

			// Set started flag
			_started = true;
		}
	}

	/**
	 * Stops the timer.
	 * 
	 * @see #start()
	 */
	public void stop() {
		synchronized (_lock) {
			// Stop the timer
			_timer.purge();

			// Unset started flag
			_started = false;
		}
	}

	/**
	 * Closes the timer.
	 * <p>
	 * This is required by ICloseable interface, but besides that it is identical to
	 * stop().
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * 
	 * @see #stop()
	 */
	public void close(IContext context) {
		stop();
	}
}
