package org.pipservices4.data.validate;

import com.fasterxml.jackson.annotation.*;

/**
 * Result generated by schema validation
 */
public class ValidationResult {
    private String _path;
    private ValidationResultType _type;
    private String _code;
    private String _message;
    private Object _expected;
    private Object _actual;

    /**
     * Creates a new instance of validation result.
     */
    public ValidationResult() {
    }

    /**
     * Creates a new instance of validation result and sets its values.
     *
     * @param path     a dot notation path of the validated element.
     * @param type     a type of the validation result: Information, Warning, or
     *                 Error.
     * @param code     an error code.
     * @param message  a human readable message.
     * @param expected an value expected by schema validation.
     * @param actual   an actual value found by schema validation.
     * @see ValidationResultType
     */
    public ValidationResult(String path, ValidationResultType type, String code, String message, Object expected,
                            Object actual) {
        _path = path;
        _type = type;
        _code = code;
        _message = message;
        _expected = expected;
        _actual = actual;
    }

    /**
     * Gets dot notation path of the validated element.
     *
     * @return the path of the validated element.
     */
    @JsonProperty("path")
    public String getPath() {
        return _path;
    }

    public void setPath(String value) {
        _path = value;
    }

    /**
     * Gets the type of the validation result: Information, Warning, or Error.
     *
     * @return the type of the validation result.
     * @see ValidationResultType
     */
    @JsonProperty("type")
    public ValidationResultType getType() {
        return _type;
    }

    public void setType(ValidationResultType value) {
        _type = value;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    @JsonProperty("code")
    public String getCode() {
        return _code;
    }

    public void setCode(String value) {
        _code = value;
    }

    /**
     * Gets the human readable message.
     *
     * @return the result message.
     */
    @JsonProperty("message")
    public String getMessage() {
        return _message;
    }

    public void setMessage(String value) {
        _message = value;
    }

    /**
     * Gets the value expected by schema validation.
     *
     * @return the expected value.
     */
    @JsonProperty("expected")
    public Object getExpected() {
        return _expected;
    }

    public void setExpected(Object value) {
        _expected = value;
    }

    /**
     * Gets the actual value found by schema validation.
     *
     * @return the actual value.
     */
    @JsonProperty("actual")
    public Object getActual() {
        return _actual;
    }

    public void setActual(Object value) {
        _actual = value;
    }
}
