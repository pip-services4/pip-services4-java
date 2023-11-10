package org.pipservices4.observability.log;

import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates all loggers from component references under a single component.
 * <p>
 * It allows to log messages and conveniently send them to multiple destinations.
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) {@link ILogger} components to pass log messages
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyComponent implements IConfigurable, IReferenceable {
 *     CompositeLogger _logger = new CompositeLogger();
 *
 *     public void configure(ConfigParams config) {
 *        this._logger.configure(config);
 *        ...
 *     }
 *
 *     public void setReferences(IReferences references) {
 *         this._logger.setReferences(references);
 *         ...
 *     }
 *
 *     public void myMethod(IContext context) {
 *         this._logger.debug(context, "Called method mycomponent.mymethod");
 *         ...
 *     }
 * }
 * }
 * </pre>
 *
 * @see ILogger
 */
public class CompositeLogger extends Logger {
    private final List<ILogger> _loggers = new ArrayList<>();

    /**
     * Creates a new instance of the logger.
     */
    public CompositeLogger() {
    }

    /**
     * Creates a new instance of the logger.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no references found
     */
    public CompositeLogger(IReferences references) throws ReferenceException {
        if (references != null)
            setReferences(references);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {

        List<Object> loggers = references.getOptional(new Descriptor(null, "logger", null, null, null));
        for (Object logger : loggers) {
            if (logger instanceof ILogger && logger != this)
                _loggers.add((ILogger) logger);
        }
    }

    /**
     * Writes a log message to the logger destination(s).
     *
     * @param level         a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     */
    @Override
    protected void write(LogLevel level, IContext context, Exception error, String message) {
        for (ILogger logger : _loggers)
            logger.log(level, context, error, message);
    }

}
