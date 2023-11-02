package org.pipservices4.data.validate;

import java.util.*;

/**
 * Validation rule to check that value is excluded from the list of constants.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new ExcludedRule(1, 2, 3));
 * 
 * schema.validate(2);      // Result: 2 must not be one of 1, 2, 3
 * schema.validate(10);     // Result: no errors
 * }
 * </pre>
 * @see IValidationRule
 */
public class ExcludedRule implements IValidationRule {
	private final Object[] _values;

	/**
	 * Creates a new validation rule and sets its values.
	 * 
	 * @param values a list of constants that value must be excluded from
	 */
	public ExcludedRule(Object... values) {
		_values = values;
	}

	/**
	 * Validates the given value. None of the values set in this ExcludedRule object
	 * must exist in the value that is given for validation to pass.
	 * 
	 * @param path    the dot notation path to the value that is to be validated.
	 * @param schema  (not used in this implementation).
	 * @param value   the value that is to be validated.
	 * @param results the results of the validation.
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

		if (found) {
			results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_INCLUDED",
					name + " must not be one of " + Arrays.toString(_values), _values, value));
		}
	}
}
