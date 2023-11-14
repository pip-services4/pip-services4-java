package org.pipservices4.container.test;


import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ApplicationExceptionFactory;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.data.random.RandomLong;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Random shutdown component that crashes the process
 * using various methods.
 * <p>
 * The component is usually used for testing, but brave developers
 * can try to use it in production to randomly crash microservices.
 * It follows the concept of "Chaos Monkey" popularized by Netflix.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li> - mode:          null - crash by NullPointer excepiton, zero - crash by dividing by zero, excetion = crash by unhandled exception, exit - exit the process
 * <li> - min_timeout:   minimum crash timeout in milliseconds (default: 5 mins)
 * <li> - max_timeout:   maximum crash timeout in milliseconds (default: 15 minutes)
 * </ul>
 * <p>
 * ### Example ###
 * {@code
 * Shutdown shutdown = new Shutdown();
 * shutdown.configure(ConfigParams.fromTuples(
 *     "mode", "exception"
 * ));
 * shutdown.shutdown();         // Result: Bang!!! the process crashes
 * }
 */
public class Shutdown implements IConfigurable, IOpenable {
    private boolean _started = false;
    private final Object _lock = new Object();
    private final Timer _timer = new Timer("shutdown-timer", true);
    private String _mode = "exception";
    private long _minTimeout = 300000;
    private long _maxTimeout = 900000;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._mode = config.getAsStringWithDefault("mode", this._mode);
        this._minTimeout = config.getAsLongWithDefault("min_timeout", this._minTimeout);
        this._maxTimeout = config.getAsLongWithDefault("max_timeout", this._maxTimeout);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _started;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) {
        synchronized (_lock) {
            if (_started)
                _timer.purge();

            long timeout = RandomLong.nextLong(this._minTimeout, this._maxTimeout);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        shutdown();
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            _timer.scheduleAtFixedRate(task, timeout, timeout);

            // Set started flag
            _started = true;
        }
    }

    /**
     * Crashes the process using the configured crash mode.
     */
    public void shutdown() throws ApplicationException {
        if (Objects.equals(this._mode, "null") || Objects.equals(this._mode, "nullpointer")) {
            throw new NullPointerException("nullpointer");
        } else if (Objects.equals(this._mode, "zero") || Objects.equals(this._mode, "dividebyzero")) {
            var crash = 0 / 100;
        } else if (Objects.equals(this._mode, "exit") || Objects.equals(this._mode, "processexit")) {
            System.exit(1);
        } else {
            ErrorDescription descr = new ErrorDescription();
            descr.setCategory("test");
            descr.setMessage("Crash test exception");
            throw ApplicationExceptionFactory.create(descr);
        }
    }

    /**
     * Closes the timer.
     *
     * This is required by ICloseable interface, but besides that it is identical to stop().
     *
     * @param context     (optional) a context to trace execution through call chain.
     */

    @Override
    public void close(IContext context) {
        synchronized (_lock) {
            // Stop the timer
            _timer.purge();

            // Unset started flag
            _started = false;
        }
    }
}
