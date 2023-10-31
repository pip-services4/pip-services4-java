package org.pipservices4.commons.errors;

/**
 * Errors returned by remote services or network during call attempts
 */
public class InvocationException extends ApplicationException {
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
     * @param correlationId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public InvocationException(String correlationId, String code, String message) {
        super(ErrorCategory.FailedInvocation, correlationId, code, message);
        this.setStatus(500);
    }
}
