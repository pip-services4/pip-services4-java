package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors caused by calls to unsupported or not yet implemented functionality
 */
public class UnsupportedException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = -8650683748145033352L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public UnsupportedException() {
        super(ErrorCategory.Unsupported, null, null, null);
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
    public UnsupportedException(String traceId, String code, String message) {
        super(ErrorCategory.Unsupported, traceId, code, message);
        this.setStatus(500);
    }
}
