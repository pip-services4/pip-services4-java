package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule that compares value to a constant.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new ValueComparisonRule("EQ", 1));
 *
 * schema.validate(1);          // Result: no errors
 * schema.validate(2);          // Result: 2 is not equal to 1
 * }
 * </pre>
 *
 * @see IValidationRule
 */
public class ValueComparisonRule implements IValidationRule {
    private final String _operation;
    private final Object _value;

    /**
     * Creates a new validation rule and sets its values.
     *
     * @param operation a comparison operation.
     * @param value     a constant value to compare to
     */
    public ValueComparisonRule(String operation, Object value) {
        _operation = operation;
        _value = value;
    }

    /**
     * Validates a given value against this rule.
     *
     * @param path    a dot notation path to the value.
     * @param schema  a schema this rule is called from
     * @param value   a value to be validated.
     * @param results a list with validation results to add new results.
     */
    public void validate(String path, Schema schema, Object value, List<ValidationResult> results) {
        String name = path != null ? path : "value";

        if (!ObjectComparator.compare(value, _operation, _value)) {
            results.add(new ValidationResult(path, ValidationResultType.Error, "BAD_VALUE",
                    name + " must " + _operation + " " + _value + " but found " + value, _operation + " " + _value,
                    value));
        }
    }
}
