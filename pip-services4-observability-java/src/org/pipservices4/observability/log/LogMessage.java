package org.pipservices4.observability.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pipservices4.commons.errors.ErrorDescription;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Data object to store captured log messages.
 * This object is used by {@link CachedLogger}.
 */
public class LogMessage {
	/** The time then message was generated */
	private ZonedDateTime _time;
	/** The source (context name) */
	private String _source;
	/** This log level */
	private LogLevel _level;
	/** The transaction id to trace execution through call chain. */
	private String _traceId;
	/**
	 * The description of the captured error
	 * @see ErrorDescription
	 */
	private ErrorDescription _error;
	/** The human-readable message */
	private String _message;
	
    public LogMessage() { }

    public LogMessage(LogLevel level, String source, String traceId, ErrorDescription error, String message) {
    	_time = ZonedDateTime.now(ZoneId.of("Z"));
        _level = level;
        _source = source;
        _traceId = traceId;
        _error = error;
        _message = message;
    }

    @JsonProperty("time")
    public ZonedDateTime getTime() { return _time; }
    public void setTime(ZonedDateTime value) { _time = value; }

    @JsonProperty("source")
    public String getSource() { return _source; }
    public void setSource(String value) { _source = value; }

    @JsonProperty("level")
    public LogLevel getLevel() { return _level; }
    public void setLevel(LogLevel value) { _level = value; }

    @JsonProperty("trace_id")
    public String getTraceId() { return _traceId; }
    public void setTraceId(String value) { _traceId = value; }

    @JsonProperty("error")
    public ErrorDescription getError() { return _error; }
    public void setError(ErrorDescription value) { _error = value; }
    
    @JsonProperty("message")
    public String getMessage() { return _message; }
    public void setMessage(String value) { _message = value; }
}
