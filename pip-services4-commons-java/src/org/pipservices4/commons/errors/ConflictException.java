package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors raised by conflicts between object versions that were
 * posted by the user and those that are stored on the server.
 */
public class ConflictException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -3421059253211761993L;

    /**
     * Creates an error instance with conflict error category and assigns its values.
     *
     * @see ErrorCategory
     */
    public ConflictException() {
        super(ErrorCategory.Conflict, null, null, null);
        this.setStatus(409);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public ConflictException(String traceId, String code, String message) {
        super(ErrorCategory.Conflict, traceId, code, message);
        this.setStatus(409);
        this.setTraceId(traceId);
    }
}
