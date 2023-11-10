package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Access errors caused by missing user identity (authentication error) or incorrect security permissions (authorization error).
 */
public class UnauthorizedException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 1728971490844757508L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public UnauthorizedException() {
        super(ErrorCategory.Unauthorized, null, null, null);
        this.setStatus(401);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @param code          (optional) a unique error code. Default: "UNKNOWN"
     * @param message       (optional) a human-readable description of the error.
     * @see ErrorCategory
     */
    public UnauthorizedException(String traceId, String code, String message) {
        super(ErrorCategory.Unauthorized, traceId, code, message);
        this.setStatus(401);
    }
}
