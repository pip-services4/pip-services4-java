package org.pipservices4.commons.errors;

/**
 * Error caused by attempt to access missing object
 */
public class NotFoundException extends ApplicationException {
    private static final long serialVersionUID = -3296918665715724164L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public NotFoundException() {
        super(ErrorCategory.NotFound, null, null, null);
        this.setStatus(404);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public NotFoundException(String traceId, String code, String message) {
        super(ErrorCategory.NotFound, traceId, code, message);
        this.setStatus(404);
    }
}
