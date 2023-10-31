package org.pipservices4.commons.errors;

/**
 * Unknown or unexpected errors
 */
public class UnknownException extends ApplicationException {
    private static final long serialVersionUID = -8513540232023043856L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public UnknownException() {
        super(ErrorCategory.Unknown, null, null, null);
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
    public UnknownException(String correlationId, String code, String message) {
        super(ErrorCategory.Unknown, correlationId, code, message);
        this.setStatus(500);
    }
}
