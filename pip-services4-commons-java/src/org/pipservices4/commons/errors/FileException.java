package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors in read/write file operations
 */
public class FileException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 1330544660294516445L;

    /**
     * Creates an error instance and assigns its values.
     *
     * @see ErrorCategory
     */
    public FileException() {
        super(ErrorCategory.FileError, null, null, null);
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
    public FileException(String traceId, String code, String message) {
        super(ErrorCategory.FileError, traceId, code, message);
        this.setStatus(500);
    }
}
