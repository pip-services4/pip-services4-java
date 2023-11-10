package org.pipservices4.observability.log;

import org.pipservices4.components.context.IContext;

/**
 * Dummy implementation of logger that doesn't do anything.
 * <p>
 * It can be used in testing or in situations when logger is required
 * but shall be disabled.
 * 
 * @see ILogger
 */
public class NullLogger implements ILogger {

	/**
	 * Creates a new instance of the logger.
	 */
	public NullLogger() {
	}

	/**
	 * Gets the maximum log level. Messages with higher log level are filtered out.
	 * 
	 * @return the maximum log level.
	 */
	public LogLevel getLevel() {
		return LogLevel.None;
	}

	/**
	 * Set the maximum log level.
	 * 
	 * @param value a new maximum log level.
	 */
	public void setLevel(LogLevel value) {
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
	}

	/**
	 * Logs fatal (unrecoverable) message that caused the process to crash.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void fatal(IContext context, String message, Object... args) {
	}

	/**
	 * Logs fatal (unrecoverable) message that caused the process to crash.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 */
	public void fatal(IContext context, Exception error) {
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
	}

	/**
	 * Logs recoverable application error.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void error(IContext context, String message, Object... args) {
	}

	/**
	 * Logs recoverable application error.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param error         an error object associated with this message.
	 */
	public void error(IContext context, Exception error) {
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
	}

	/**
	 * Logs a warning that may or may not have a negative impact.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void warn(IContext context, String message, Object... args) {
	}

	/**
	 * Logs an important information message
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void info(IContext context, String message, Object... args) {
	}

	/**
	 * Logs a high-level debug information for troubleshooting.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void debug(IContext context, String message, Object... args) {
	}

	/**
	 * Logs a low-level debug information for troubleshooting.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param message       a human-readable message to log.
	 * @param args          arguments to parameterize the message.
	 */
	public void trace(IContext context, String message, Object... args) {
	}
}
