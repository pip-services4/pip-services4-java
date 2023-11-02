package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule negate another rule.
 * When embedded rule returns no errors, than this rule return an error.
 * When embedded rule return errors, than the rule returns no errors.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new NotRule(
 *          new ValueComparisonRule("EQ", 1)
 *      ));
 *
 * schema.validate(1);          // Result: error
 * schema.validate(5);          // Result: no error
 * }
 * </pre>
 *
 * @see IValidationRule
 */
public class NotRule implements IValidationRule {
    private final IValidationRule _rule;

    /**
     * Creates a new validation rule and sets its values
     *
     * @param rule a rule to be negated.
     */
    public NotRule(IValidationRule rule) {
        _rule = rule;
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
        if (_rule == null)
            return;

        String name = path != null ? path : "value";
        List<ValidationResult> localResults = new ArrayList<>();
        _rule.validate(path, schema, value, localResults);
        if (!localResults.isEmpty())
            return;

        results.add(new ValidationResult(path, ValidationResultType.Error, "NOT_FAILED",
                "Negative check for " + name + " failed", null, null));
    }
}
