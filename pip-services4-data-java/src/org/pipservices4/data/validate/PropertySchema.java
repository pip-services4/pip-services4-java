package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.convert.*;

/**
 * Schema to validate object properties
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ObjectSchema schema = new ObjectSchema().withProperty(new PropertySchema("id", TypeCode.String));
 *
 * schema.validate(Map.of("id", "1", "name" ,"ABC" ));	// Result: no errors
 * schema.validate(Map.of( "name", "ABC" ));                    // Result: no errors
 * schema.validate(Map.of( "id", 1, "name", "ABC" ));	// Result: id type mismatch
 * }
 * </pre>
 *
 * @see ObjectSchema
 */
public class PropertySchema extends Schema {
    private String _name;
    private Object _type;

    /**
     * Creates a new validation schema.
     */
    public PropertySchema() {
    }

    /**
     * Creates a new validation schema and sets its values.
     *
     * @param name (optional) a property name
     * @param type (optional) a property type
     * @see IValidationRule
     * @see TypeCode
     */
    public PropertySchema(String name, Object type) {
        _name = name;
        _type = type;
    }

    /**
     * Creates a new validation schema and sets its values.
     *
     * @param name     (optional) a property name
     * @param type     (optional) a property type
     * @param required (optional) true to always require non-null values.
     * @param rules    (optional) a list with validation rules.
     * @see IValidationRule
     * @see TypeCode
     */
    public PropertySchema(String name, Object type, Boolean required, List<IValidationRule> rules) {
        super(required, rules);

        this._name = name;
        this._type = type;
    }

    /**
     * Gets the property name.
     *
     * @return the property name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the property name.
     *
     * @param value a new property name.
     */
    public void setName(String value) {
        _name = value;
    }

    /**
     * Gets the property type.
     *
     * @return the property type.
     */
    public Object getType() {
        return _type;
    }

    /**
     * Sets a new property type. The type can be defined as type, type name or
     * [[TypeCode]]
     *
     * @param value a new property type.
     */
    public void setType(Object value) {
        _type = value;
    }

    /**
     * Validates a given value against the schema and configured validation rules.
     *
     * @param path    a dot notation path to the value.
     * @param value   a value to be validated.
     * @param results a list with validation results to add new results.
     */
    @Override
    protected void performValidation(String path, Object value, List<ValidationResult> results) {
        path = path == null || path.isEmpty() ? _name : path + "." + _name;

        super.performValidation(path, value, results);
        performTypeValidation(path, _type, value, results);
    }
}
