package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors returned by remote services or network during call attempts
 */
public class InvocationException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 7516215539095097503L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public InvocationException() {
        super(ErrorCategory.FailedInvocation, null, null, null);
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
    public InvocationException(String traceId, String code, String message) {
        super(ErrorCategory.FailedInvocation, traceId, code, message);
        this.setStatus(500);
    }
}
