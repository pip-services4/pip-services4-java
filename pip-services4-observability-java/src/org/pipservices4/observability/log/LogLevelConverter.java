package org.pipservices4.observability.log;

/**
 * Helper class to convert log level values.
 * 
 * @see LogLevel
 */
public class LogLevelConverter {

	/**
	 * Converts numbers and strings to standard log level values.
	 * 
	 * @param value a value to be converted
	 * @return converted log level
	 */
	public static LogLevel toLogLevel(Object value) {
		return toLogLevel(value, null);
	}

	/**
	 * Converts numbers and strings to standard log level values.
	 * @param value a value to be converted
	 * @param defaultValue a default value if conversion is not possible
	 * @return converted log level
	 */
	public static LogLevel toLogLevel(Object value, LogLevel defaultValue) {
		if (defaultValue == null)
			defaultValue = LogLevel.Info;
		if (value == null)
			return LogLevel.Info;

		value = value.toString().toUpperCase();
		if ("0".equals(value) || "NOTHING".equals(value) || "NONE".equals(value))
			return LogLevel.None;
		else if ("1".equals(value) || "FATAL".equals(value))
			return LogLevel.Fatal;
		else if ("2".equals(value) || "ERROR".equals(value))
			return LogLevel.Error;
		else if ("3".equals(value) || "WARN".equals(value) || "WARNING".equals(value))
			return LogLevel.Warn;
		else if ("4".equals(value) || "INFO".equals(value))
			return LogLevel.Info;
		else if ("5".equals(value) || "DEBUG".equals(value))
			return LogLevel.Debug;
		else if ("6".equals(value) || "TRACE".equals(value))
			return LogLevel.Trace;
		else
			return defaultValue;
	}

	/**
	 * Converts log level to a string.
	 * 
	 * @param level a log level to convert
	 * @return log level name string.
	 * 
	 * @see LogLevel
	 */
	public static String toString(LogLevel level) {
		if (level == LogLevel.Fatal)
			return "FATAL";
		if (level == LogLevel.Error)
			return "ERROR";
		if (level == LogLevel.Warn)
			return "WARN";
		if (level == LogLevel.Info)
			return "INFO";
		if (level == LogLevel.Debug)
			return "DEBUG";
		if (level == LogLevel.Trace)
			return "TRACE";
		return "UNDEF";
	}

	/**
	 * Converts log level to a number.
	 * 
	 * @param level a log level to convert.
	 * @return log level number value.
	 */
	public static int toInteger(LogLevel level) {
		if (level == LogLevel.Fatal)
			return 1;
		if (level == LogLevel.Error)
			return 2;
		if (level == LogLevel.Warn)
			return 3;
		if (level == LogLevel.Info)
			return 4;
		if (level == LogLevel.Debug)
			return 5;
		if (level == LogLevel.Trace)
			return 6;
		return 0;
	}

}
