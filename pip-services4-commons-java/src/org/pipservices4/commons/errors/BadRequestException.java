package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors due to improper user requests.
 * <p>
 * For example: missing or incorrect parameters.
 */
public class BadRequestException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -6858254084911710376L;

    /**
     * Creates an error instance with bad request error category and assigns its values.
     *
     * @see ErrorCategory
     */
    public BadRequestException() {
        super(ErrorCategory.BadRequest, null, null, null);
        this.setStatus(400);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public BadRequestException(String traceId, String code, String message) {
        super(ErrorCategory.BadRequest, traceId, code, message);
        this.setStatus(400);
    }
}
