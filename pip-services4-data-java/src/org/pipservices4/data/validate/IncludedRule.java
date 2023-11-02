package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule to check that value is included into the list of constants.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new IncludedRule(1, 2, 3));
 * 
 * schema.validate(2);      // Result: no errors
 * schema.validate(10);     // Result: 10 must be one of 1, 2, 3
 * }
 * </pre>
 * @see IValidationRule
 */
public class IncludedRule implements IValidationRule {
	private final Object[] _values;

	/**
	 * Creates a new validation rule and sets its values.
	 * 
	 * @param values a list of constants that value must be included to
	 */
	public IncludedRule(Object... values) {
		_values = values;
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
		boolean found = false;

		for (Object thisValue : _values) {
			if (thisValue != null && thisValue.equals(value)) {
				found = true;
				break;
			}
		}

		if (!found) {
			results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_NOT_INCLUDED",
					name + " must be one of " + Arrays.toString(_values), _values, value));
		}
	}
}
