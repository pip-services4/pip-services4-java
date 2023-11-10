package org.pipservices4.commons.errors;

import java.io.Serial;

/**
 * Errors related to mistakes in the microservice's user-defined configurations.
 */
public class ConfigException extends ApplicationException {
    @Serial
    private static final long serialVersionUID = 3832437788895163030L;

    /**
     * Creates an error instance with misconfiguration error category and assigns its values.
     *
     * @see ErrorCategory
     */
    public ConfigException() {
        super(ErrorCategory.Misconfiguration, null, null, null);
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
    public ConfigException(String traceId, String code, String message) {
        super(ErrorCategory.Misconfiguration, traceId, code, message);
        this.setStatus(500);
    }
}
