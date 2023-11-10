package org.pipservices4.observability.trace;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.log.LogLevel;
import org.pipservices4.observability.log.LogLevelConverter;
import org.pipservices4.components.context.IContext;

/**
 * Tracer that dumps recorded traces to logger.
 *
 * ### Configuration parameters ###
 *
 * <ul>
 *     <li> - options:
 *     <ul>
 *         <li> - log_level:         log level to record traces (default: debug)
 *     </ul>
 * </ul>
 *
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) {@link org.pipservices4.observability.log.ILogger} components to dump the captured counters
 * <li>*:context-info:*:*:1.0         (optional) {@link org.pipservices4.components.context.ContextInfo} to detect the context id and specify counters source
 * </ul>
 *
 * @see ITracer
 * @see org.pipservices4.observability.count.CachedCounters
 * @see CompositeLogger
 *
 * ### Example ###
 *
 * {@code
 * LogTracer tracer = new LogTracer();
 * tracer.setReferences(References.fromTuples(
 *     new Descriptor("pip-services", "logger", "console", "default", "1.0"), new ConsoleLogger()
 * ));
 *
 * TraceTiming timing = trcer.beginTrace("123", "mycomponent", "mymethod");
 * try {
 *     ...
 *     timing.endTrace();
 * } catch(err) {
 *     timing.endFailure(err);
 * }
 * }
 */
public class LogTracer implements IConfigurable, IReferenceable, ITracer {
    private final CompositeLogger _logger = new CompositeLogger();
    private LogLevel _logLevel = LogLevel.Debug;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._logLevel = LogLevelConverter.toLogLevel(
                config.getAsObject("options.log_level"),
                this._logLevel
        );
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException
     * @throws ConfigException
     */
    @Override
    public void setReferences(IReferences references) {
        this._logger.setReferences(references);
    }

    private void logTrace(IContext context, String component, String operation, Exception error, long duration) {
        StringBuilder builder = new StringBuilder();

        if (error != null) {
            builder.append("Failed to execute ");
        } else {
            builder.append("Executed ");
        }

        builder.append(component);

        if (operation != null && !operation.isEmpty()) {
            builder.append(".");
            builder.append(operation);
        }

        if (duration > 0) {
            builder.append(" in ").append(duration).append(" msec");
        }

        if (error != null) {
            this._logger.error(context, String.valueOf(error), builder);
        } else {
            this._logger.log(this._logLevel, context, null, String.valueOf(builder));
        }
    }

    /**
     * Records an operation trace with its name and duration
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param duration      execution duration in milliseconds.
     */
    @Override
    public void trace(IContext context, String component, String operation, Long duration) {
        this.logTrace(context, component, operation, null, duration);
    }

    /**
     * Records an operation failure with its name, duration and error
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param error         an error object associated with this trace.
     * @param duration      execution duration in milliseconds.
     */
    public void failure(IContext context, String component, String operation, Exception error, long duration) {
        this.logTrace(context, component, operation, error, duration);
    }

    /**
     * Begings recording an operation trace
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @return a trace timing object.
     */
    public TraceTiming beginTrace(IContext context, String component, String operation) {
        return new TraceTiming(context, component, operation, this);
    }

}
