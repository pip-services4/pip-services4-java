package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule to combine rules with AND logical operation.
 * When all rules returns no errors, than this rule also returns no errors.
 * When one of the rules return errors, than the rules returns all errors.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new AndRule(
 *          new ValueComparisonRule("GTE", 1),
 *          new ValueComparisonRule("LTE", 10)
 *      ));
 * 
 * schema.validate(0);          // Result: 0 must be greater or equal to 1
 * schema.validate(5);          // Result: no error
 * schema.validate(20);         // Result: 20 must be letter or equal 10
 * }
 * </pre>
 * @see IValidationRule
 */
public class AndRule implements IValidationRule {
	private final IValidationRule[] _rules;

	/**
	 * Creates a new validation rule and sets its values.
	 * 
	 * @param rules a list of rules to join with AND operator
	 */
	public AndRule(IValidationRule... rules) {
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
		if (_rules == null)
			return;

		for (IValidationRule rule : _rules)
			rule.validate(path, schema, value, results);
	}

}
