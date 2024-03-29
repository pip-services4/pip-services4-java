package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors related to operations called in wrong component state.
 * For instance, business calls when component is not ready
 */
public class InvalidStateException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 8713306897733892945L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public InvalidStateException() {
        super(ErrorCategory.InvalidState, null, null, null);
        this.setStatus(500);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public InvalidStateException(String traceId, String code, String message) {
        super(ErrorCategory.InvalidState, traceId, code, message);
        this.setStatus(500);
    }
}
