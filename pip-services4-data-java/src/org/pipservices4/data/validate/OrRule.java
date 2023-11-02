package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule to combine rules with OR logical operation.
 * When one of rules returns no errors, than this rule also returns no errors.
 * When all rules return errors, than the rule returns all errors.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new OrRule(
 *          new ValueComparisonRule("LT", 1),
 *          new ValueComparisonRule("GT", 10)
 *      ));
 *
 * schema.validate(0);          // Result: no error
 * schema.validate(5);          // Result: 5 must be less than 1 or 5 must be more than 10
 * schema.validate(20);         // Result: no error
 * }
 * </pre>
 *
 * @see IValidationRule
 */
public class OrRule implements IValidationRule {
    private final IValidationRule[] _rules;

    /**
     * Creates a new validation rule and sets its values.
     *
     * @param rules a list of rules to join with OR operator
     */
    public OrRule(IValidationRule... rules) {
        _rules = rules;
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
        if (_rules == null || _rules.length == 0)
            return;

        List<ValidationResult> localResults = new ArrayList<>();

        for (IValidationRule rule : _rules) {
            int resultsCount = localResults.size();
            rule.validate(path, schema, value, localResults);
            if (resultsCount == localResults.size())
                return;
        }

        results.addAll(localResults);
    }
}
