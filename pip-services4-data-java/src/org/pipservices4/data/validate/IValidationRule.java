package org.pipservices4.data.validate;

import java.util.*;

/**
 * Interface for validation rules.
 * <p>
 * Validation rule can validate one or multiple values
 * against complex rules like: value is in range,
 * one property is less than another property,
 * enforce enumerated values and more.
 * <p>
 * This interface allows to implement custom rules.
 */
public interface IValidationRule {
    /**
     * Validates a given value against this rule.
     *
     * @param path    a dot notation path to the value.
     * @param schema  a schema this rule is called from
     * @param value   a value to be validated.
     * @param results a list with validation results to add new results.
     */
    void validate(String path, Schema schema, Object value, List<ValidationResult> results);
}
