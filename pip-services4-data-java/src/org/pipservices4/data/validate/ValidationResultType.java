package org.pipservices4.data.validate;

/**
 * Types of validation results generated by validation schemas.
 *
 * @see ValidationResult
 */
public enum ValidationResultType {
    /**
     * General information (not an error).
     */
    Information,
    /**
     * Warning about something suspicious. In strict mode is treated as error
     */
    Warning,
    /**
     * Validation error.
     */
    Error
}
