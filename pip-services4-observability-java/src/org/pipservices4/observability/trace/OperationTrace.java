package org.pipservices4.observability.trace;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pipservices4.commons.errors.ErrorDescription;

import java.time.ZonedDateTime;

/**
 * Data object to store captured operation traces.
 * This object is used by {@link CachedTracer}.
 */
public class OperationTrace {
    /**
     * The time when operation was executed
     */
    @JsonProperty("time")
    public ZonedDateTime time;
    /**
     * The source (context name)
     */
    @JsonProperty("source")
    public String source;
    /**
     * The name of component
     */
    @JsonProperty("component")
    public String component;
    /**
     * The name of the executed operation
     */
    @JsonProperty("operation")
    public String operation;
    /**
     * The transaction id to trace execution through call chain.
     */
    @JsonProperty("trace_id")
    public String traceId;
    /**
     * The duration of the operation in milliseconds
     */
    @JsonProperty("duration")
    public long duration;
    /**
     * The description of the captured error
     * <p>
     * {@link ErrorDescription}
     * {@link org.pipservices4.commons.errors.ApplicationException}
     */
    @JsonProperty("error")
    public ErrorDescription error;

    public OperationTrace() {
    }

    public OperationTrace(ZonedDateTime time, String source, String component, String operation, String traceId, long duration, ErrorDescription error) {
        this.time = time;
        this.source = source;
        this.component = component;
        this.operation = operation;
        this.traceId = traceId;
        this.duration = duration;
        this.error = error;
    }
}
