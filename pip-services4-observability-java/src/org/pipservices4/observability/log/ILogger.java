package org.pipservices4.observability.log;

import org.pipservices4.components.context.IContext;

/**
 * Interface for logger components that capture execution log messages.
 */
public interface ILogger {
	/**
	 * Gets the maximum log level. Messages with higher log level are filtered out.
	 * 
	 * @return the maximum log level.
	 */
	LogLevel getLevel();

	/**
	 * Set the maximum log level.
	 * 
	 * @param value a new maximum log level.
	 */
	void setLevel(LogLevel value);

	/**
	 * Logs a message at specified log level.
	 * 
	 * @param level         a log level.
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void log(LogLevel level, IContext context, Exception error, String message, Object... args);

	/**
	 * Logs fatal (unrecoverable) message that caused the process to crash.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void fatal(IContext context, String message, Object... args);

	/**
	 * Logs fatal (unrecoverable) message that caused the process to crash.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 */
	void fatal(IContext context, Exception error);

	/**
	 * Logs fatal (unrecoverable) message that caused the process to crash.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void fatal(IContext context, Exception error, String message, Object... args);

	/**
	 * Logs recoverable application error.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void error(IContext context, String message, Object... args);

	/**
	 * Logs recoverable application error.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 */
	void error(IContext context, Exception error);

	/**
	 * Logs recoverable application error.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void error(IContext context, Exception error, String message, Object... args);

	/**
	 * Logs a warning that may or may not have a negative impact.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void warn(IContext context, String message, Object... args);

	/**
	 * Logs an important information message
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void info(IContext context, String message, Object... args);

	/**
	 * Logs a high-level debug information for troubleshooting.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void debug(IContext context, String message, Object... args);

	/**
	 * Logs a low-level debug information for troubleshooting.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	void trace(IContext context, String message, Object... args);
}
