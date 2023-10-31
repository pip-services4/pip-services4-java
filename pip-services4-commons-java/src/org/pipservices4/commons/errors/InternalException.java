package org.pipservices4.commons.errors;

/**
 * Errors caused by programming mistakes
 */
public class InternalException extends ApplicationException {
    private static final long serialVersionUID = 9121408616688009166L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public InternalException() {
        super(ErrorCategory.Internal, null, null, null);
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
    public InternalException(String correlationId, String code, String message) {
        super(ErrorCategory.Internal, correlationId, code, message);
        this.setStatus(500);
    }
}
