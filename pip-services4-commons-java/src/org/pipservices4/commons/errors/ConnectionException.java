package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors that occur during connections to remote services.
 * They can be related to misconfiguration, network issues, or the remote service itself.
 */
public class ConnectionException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 5757636441830366775L;

    /**
     * Creates an error instance with noresponse error category and assigns its values.
     *
     * @see ErrorCategory
     */
    public ConnectionException() {
        super(ErrorCategory.NoResponse, null, null, null);
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
    public ConnectionException(String traceId, String code, String message) {
        super(ErrorCategory.NoResponse, traceId, code, message);
        this.setStatus(500);
    }
}
