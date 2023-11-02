package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.convert.TypeConverter;
import org.pipservices4.commons.reflect.ObjectReader;
import org.pipservices4.commons.reflect.TypeMatcher;

/**
 * Basic schema that validates values against a set of validation rules.
 * <p>
 * This schema is used as a basis for specific schemas to validate 
 * objects, project properties, arrays and maps.
 * 
 * @see ObjectSchema
 * @see PropertySchema
 * @see ArraySchema
 * @see MapSchema
 */
public class Schema {
	private boolean _required = false;
	private List<IValidationRule> _rules;

	public Schema() {
	}

	/**
	 * Creates a new instance of validation schema and sets its values.
	 * 
	 * @param required (optional) true to always require non-null values.
	 * @param rules    (optional) a list with validation rules.
	 * 
	 * @see IValidationRule
	 */
	public Schema(boolean required, List<IValidationRule> rules) {
		_required = required;
		_rules = rules;
	}

	/**
	 * Gets a flag that always requires non-null values. For null values it raises a
	 * validation error.
	 * 
	 * @return true to always require non-null values and false to allow null
	 *         values.
	 */
	public boolean isRequired() {
		return _required;
	}

	/**
	 * Sets a flag that always requires non-null values.
	 * 
	 * @param value true to always require non-null values and false to allow null
	 *              values.
	 */
	public void setRequired(boolean value) {
		_required = value;
	}

	/**
	 * Gets validation rules to check values against.
	 * 
	 * @return a list with validation rules.
	 */
	public List<IValidationRule> getRules() {
		return _rules;
	}

	/**
	 * Sets validation rules to check values against.
	 * 
	 * @param value a list with validation rules.
	 */
	public void setRules(List<IValidationRule> value) {
		_rules = value;
	}

	/**
	 * Makes validated values always required (non-null). For null values the schema
	 * will raise errors.
	 * 
	 * This method returns reference to this exception to implement Builder pattern
	 * to chain additional calls.
	 * 
	 * @return this validation schema
	 * 
	 * @see #makeOptional()
	 */
	public Schema makeRequired() {
		_required = true;
		return this;
	}

	/**
	 * Makes validated values optional. Validation for null values will be skipped.
	 * 
	 * This method returns reference to this exception to implement Builder pattern
	 * to chain additional calls.
	 * 
	 * @return this validation schema
	 * 
	 * @see #makeRequired()
	 */
	public Schema makeOptional() {
		_required = false;
		return this;
	}

	/**
	 * Adds validation rule to this schema.
	 * 
	 * This method returns reference to this exception to implement Builder pattern
	 * to chain additional calls.
	 * 
	 * @param rule a validation rule to be added.
	 * @return this validation schema.
	 */
	public Schema withRule(IValidationRule rule) {
		_rules = _rules != null ? _rules : new ArrayList<>();
		_rules.add(rule);
		return this;
	}

	/**
	 * Validates a given value against the schema and configured validation rules.
	 * 
	 * @param path    a dot notation path to the value.
	 * @param value   a value to be validated.
	 * @param results a list with validation results to add new results.
	 */
	protected void performValidation(String path, Object value, List<ValidationResult> results) {
		String name = path != null ? path : "value";

		if (value == null) {
			// Check for required values
			if (_required)
				results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_IS_NULL",
						name + " cannot be null", "NOT NULL", null));
		} else {
			value = ObjectReader.getValue(value);

			// Check validation rules
			if (_rules != null) {
				for (IValidationRule rule : _rules)
					rule.validate(path, this, value, results);
			}
		}
	}

	/**
	 * Validates a given value to match specified type. The type can be defined as a
	 * Schema, type, a type name or TypeCode When type is a Schema, it executes
	 * validation recursively against that Schema.
	 * 
	 * @param path    a dot notation path to the value.
	 * @param type    a type to match the value type
	 * @param value   a value to be validated.
	 * @param results a list with validation results to add new results.
	 * 
	 * @see #performValidation(String, Object, List)
	 */
	protected void performTypeValidation(String path, Object type, Object value, List<ValidationResult> results) {
		// If type it not defined then skip
		if (type == null)
			return;

		// Perform validation against schema
		if (type instanceof Schema) {
			Schema schema = (Schema) type;
			schema.performValidation(path, value, results);
			return;
		}

		// If value is null then skip
		value = ObjectReader.getValue(value);
		if (value == null)
			return;

		String name = path != null ? path : "value";
		Class<?> valueType = value.getClass();

		// Match types
		if (TypeMatcher.matchType(type, TypeConverter.toTypeCode(valueType), value))
			return;

		// Generate type mismatch error
		results.add(new ValidationResult(path, ValidationResultType.Error, "TYPE_MISMATCH",
				name + " type must be " + type + " but found " + valueType, type, valueType));
	}

	/**
	 * Validates the given value and results validation results.
	 * 
	 * @param value a value to be validated.
	 * @return a list with validation results.
	 * 
	 * @see ValidationResult
	 */
	public List<ValidationResult> validate(Object value) {
		List<ValidationResult> results = new ArrayList<>();
		performValidation("", value, results);
		return results;
	}

	/**
	 * Validates the given value and returns a ValidationException if errors were
	 * found.
	 * 
	 * @param traceId     (optional) transaction id to trace execution through call chain.
	 * @param value         a value to be validated.
	 * @param strict        true to treat warnings as errors.
	 * @throws ValidationException when errors occurred in validation
	 */
	public void validateAndThrowException(String traceId, Object value, boolean strict)
			throws ValidationException {
		List<ValidationResult> results = validate(value);
		ValidationException.throwExceptionIfNeeded(traceId, results, strict);
	}

	/**
	 * Validates the given value and throws a ValidationException if errors were
	 * found.
	 * 
	 * @param traceId     (optional) transaction id to trace execution through call chain.
	 * @param value         a value to be validated.
	 * @throws ValidationException when errors occured in validation
	 * 
	 * @see ValidationException#throwExceptionIfNeeded(String, List, boolean)
	 */
	public void validateAndThrowException(String traceId, Object value) throws ValidationException {
		validateAndThrowException(traceId, value, false);
	}
}
