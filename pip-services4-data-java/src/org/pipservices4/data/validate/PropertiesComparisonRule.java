package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.reflect.*;

/**
 * Validation rule that compares two object properties.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *
 * Schema schema = new ObjectSchema().withRule(new PropertiesComparisonRule("field1", "NE", "field2"));
 *
 * schema.validate(Map.of("field1", 1, "field2", 2)); // Result: no errors
 * schema.validate(Map.of("field1", 1, "field2", 1)); // Result: field1 shall not be equal to field2
 * schema.validate(Map.of()); // Result: no errors
 *
 * }
 * </pre>
 * @see IValidationRule
 */
public class PropertiesComparisonRule implements IValidationRule {
	private final String _property1;
	private final String _property2;
	private final String _operation;

	/**
	 * Creates a new validation rule and sets its arguments.
	 * 
	 * @param property1 a name of the first property to compare.
	 * @param operation a comparison operation.
	 * @param property2 a name of the second property to compare.
	 * 
	 * @see ObjectComparator#compare(Object, String, Object)
	 */
	public PropertiesComparisonRule(String property1, String operation, String property2) {
		_property1 = property1;
		_operation = operation;
		_property2 = property2;
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
		Object value1 = ObjectReader.getProperty(value, _property1);
		Object value2 = ObjectReader.getProperty(value, _property2);

		if (!ObjectComparator.compare(value1, _operation, value2)) {
			results.add(new ValidationResult(path, ValidationResultType.Error, "PROPERTIES_NOT_MATCH",
					name + " must have " + _property1 + " " + _operation + " " + _property2, value2, value1));
		}
	}
}
