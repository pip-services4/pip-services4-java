package org.pipservices4.observability.log;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.context.*;

import java.util.Arrays;

/**
 * Abstract logger that captures and formats log messages.
 * Child classes take the captured messages and write them to their specific destinations.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * Parameters to pass to the configure() method for component configuration:
 * <ul>
 * <li>level:             maximum log level to capture
 * <li>source:            source (context) name
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:context-info:*:*:1.0     (optional) {@link ContextInfo} to detect the context id and specify counters source
 * </ul>
 *
 * @see ILogger
 */
public abstract class Logger implements ILogger, IReconfigurable, IReferenceable {
    private LogLevel _level = LogLevel.Info;
    protected String _source = null;

    /**
     * Creates a new instance of the logger.
     */
    protected Logger() {
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        this._level = LogLevelConverter.toLogLevel(
                config.getAsObject("level"),
                this._level
        );

        this._source = config.getAsStringWithDefault("source", this._source);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) {
        Object contextInfo = references.getOneOptional(new Descriptor("pip-services", "context-info", "*", "*", "1.0"));
        if (contextInfo instanceof ContextInfo && _source == null)
            _source = ((ContextInfo) contextInfo).getName();
    }

    /**
     * Composes an human-readable error description
     *
     * @param error an error to format.
     * @return a human-reable error description.
     */
    protected String composeError(Exception error) {
        StringBuilder builder = new StringBuilder();

        Throwable t = error;
        while (t != null) {
            if (!builder.isEmpty())
                builder.append(" Caused by error: ");

            builder.append(t.getMessage()).append(" StackTrace: ").append(Arrays.toString(t.getStackTrace()));

            t = t.getCause();
        }

        return builder.toString();
    }

//	/**
//	 * Composes an human-readable error description
//	 *
//	 * @param error an error to format.
//	 * @return a human-reable error description.
//	 */
//	protected String composeError(Exception error) {
//		StringBuilder builder = new StringBuilder();
//
//		while (error != null) {
//			if (builder.length() > 0)
//				builder.append(" Caused by error: ");
//
//			builder.append(error.getMessage()).append(" StackTrace: ").append(Arrays.toString(error.getStackTrace()));
//
//			try {
//				error = error.getClass().newInstance();///// ????
//			} catch (InstantiationException | IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return builder.toString();
//	}

    /**
     * Gets the source (context) name.
     *
     * @return the source (context) name.
     */
    public String getSource() {
        return this._source;
    }

    /**
     * Sets the source (context) name.
     *
     * @param value a new source (context) name.
     */
    public void setSource(String value) {
        _source = value;
    }

    /**
     * Gets the maximum log level. Messages with higher log level are filtered out.
     *
     * @return the maximum log level.
     */
    public LogLevel getLevel() {
        return _level;
    }

    /**
     * Set the maximum log level.
     *
     * @param value a new maximum log level.
     */
    public void setLevel(LogLevel value) {
        _level = value;
    }

    /**
     * Writes a log message to the logger destination.
     *
     * @param level         a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     */
    protected abstract void write(LogLevel level, IContext context, Exception error, String message);

    /**
     * Formats the log message and writes it to the logger destination.
     *
     * @param level         a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    protected void formatAndWrite(LogLevel level, IContext context, Exception error, String message,
                                  Object[] args) {
        message = message != null ? message : "";
        if (args != null && args.length > 0)
            message = String.format(message, args);

        write(level, context, error, message);
    }

    /**
     * Logs a message at specified log level.
     *
     * @param level         a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void log(LogLevel level, IContext context, Exception error, String message, Object... args) {
        formatAndWrite(level, context, error, message, args);
    }

    /**
     * Logs fatal (unrecoverable) message that caused the process to crash.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void fatal(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Fatal, context, null, message, args);
    }

    /**
     * Logs fatal (unrecoverable) message that caused the process to crash.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     */
    public void fatal(IContext context, Exception error) {
        formatAndWrite(LogLevel.Fatal, context, error, null, null);
    }

    /**
     * Logs fatal (unrecoverable) message that caused the process to crash.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void fatal(IContext context, Exception error, String message, Object... args) {
        formatAndWrite(LogLevel.Fatal, context, error, message, args);
    }

    /**
     * Logs recoverable application error.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void error(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Error, context, null, message, args);
    }

    /**
     * Logs recoverable application error.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     */
    public void error(IContext context, Exception error) {
        formatAndWrite(LogLevel.Error, context, error, null, null);
    }

    /**
     * Logs recoverable application error.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param error         an error object associated with this message.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void error(IContext context, Exception error, String message, Object... args) {
        formatAndWrite(LogLevel.Error, context, error, message, args);
    }

    /**
     * Logs a warning that may or may not have a negative impact.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void warn(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Warn, context, null, message, args);
    }

    /**
     * Logs an important information message
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void info(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Info, context, null, message, args);
    }

    /**
     * Logs a high-level debug information for troubleshooting.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void debug(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Debug, context, null, message, args);
    }

    /**
     * Logs a low-level debug information for troubleshooting.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a human-readable message to log.
     * @param args          arguments to parameterize the message.
     */
    public void trace(IContext context, String message, Object... args) {
        formatAndWrite(LogLevel.Trace, context, null, message, args);
    }
}
