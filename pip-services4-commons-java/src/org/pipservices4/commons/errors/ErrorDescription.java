package org.pipservices4.commons.errors;

import org.pipservices4.commons.data.*;

import com.fasterxml.jackson.annotation.*;

/**
 * Serializeable error description. It is use to pass information about errors
 * between microservices implemented in different languages. On the receiving side
 * ErrorDescription is used to recreate exception object close to its original type
 * without missing additional details.
 *
 * @see ApplicationException
 * @see ApplicationExceptionFactory
 */
public class ErrorDescription {
    /**
     * Data type of the original error
     */
    private String _type;
    /**
     * Standard error category
     */
    private String _category;
    /**
     * HTTP status code associated with this error type
     */
    private int _status;
    /**
     * A unique error code
     */
    private String _code;
    /**
     * A human-readable error description (usually written in English)
     */
    private String _message;
    /**
     * A map with additional details that can be used to restore error description in other languages
     */
    private StringValueMap _details;
    /**
     * A unique transaction id to trace execution throug call chain
     */
    private String _traceId;
    /**
     * Original error wrapped by this exception
     */
    private String _cause;
    /**
     * Stack trace of the exception
     */
    private String _stackTrace;

    public ErrorDescription() {
    }

    @JsonProperty("type")
    public String getType() {
        return _type;
    }

    public void setType(String value) {
        _type = value;
    }

    @JsonProperty("category")
    public String getCategory() {
        return _category;
    }

    public void setCategory(String value) {
        _category = value;
    }

    @JsonProperty("status")
    public int getStatus() {
        return _status;
    }

    public void setStatus(int value) {
        _status = value;
    }

    @JsonProperty("code")
    public String getCode() {
        return _code;
    }

    public void setCode(String value) {
        _code = value;
    }

    @JsonProperty("message")
    public String getMessage() {
        return _message;
    }

    public void setMessage(String value) {
        _message = value;
    }

    @JsonProperty("details")
    public StringValueMap getDetails() {
        return _details;
    }

    public void setDetails(StringValueMap value) {
        _details = value;
    }

    @JsonProperty("trace_id")
    public String gettraceId() {
        return _traceId;
    }

    public void settraceId(String value) {
        _traceId = value;
    }

    @JsonProperty("cause")
    public String getCause() {
        return _cause;
    }

    public void setCause(String value) {
        _cause = value;
    }

    @JsonProperty("stack_trace")
    public String getStackTrace() {
        return _stackTrace;
    }

    public void setStackTrace(String value) {
        _stackTrace = value;
    }
}
