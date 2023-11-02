package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.reflect.*;

/**
 * Schema to validate user defined objects.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ObjectSchema schema = new ObjectSchema()
 *      .withOptionalProperty("id", TypeCode.String)
 *      .withRequiredProperty("name", TypeCode.String);
 *
 * schema.validate({ id: "1", name: "ABC" });       // Result: no errors
 * schema.validate({ name: "ABC" });                // Result: no errors
 * schema.validate({ id: 1, name: "ABC" });         // Result: id type mismatch
 * schema.validate({ id: 1, _name: "ABC" });        // Result: name is missing, unexpected _name
 * schema.validate("ABC");                          // Result: type mismatch
 * }
 * </pre>
 */
public class ObjectSchema extends Schema {
    private List<PropertySchema> _properties;
    private boolean _allowUndefined = false;

    /**
     * Creates a new validation schema and sets its values.
     */
    public ObjectSchema() {
    }

    /**
     * Gets validation schemas for object properties.
     *
     * @return the list of property validation schemas.
     * @see PropertySchema
     */
    public List<PropertySchema> getProperties() {
        return _properties;
    }

    /**
     * Sets validation schemas for object properties.
     *
     * @param value a list of property validation schemas.
     * @see PropertySchema
     */
    public void setProperties(List<PropertySchema> value) {
        _properties = value;
    }

    /**
     * Gets flag to allow undefined properties
     *
     * @return true to allow undefined properties and false to disallow.
     */
    public boolean isUndefinedAllowed() {
        return _allowUndefined;
    }

    /**
     * Sets flag to allow undefined properties
     *
     * @param value true to allow undefined properties and false to disallow.
     */
    public void setUndefinedAllowed(boolean value) {
        _allowUndefined = value;
    }

    /**
     * Sets flag to allow undefined properties
     * <p>
     * This method returns reference to this exception to implement Builder pattern
     * to chain additional calls.
     *
     * @param value true to allow undefined properties and false to disallow.
     * @return this validation schema.
     */
    public ObjectSchema allowUndefined(boolean value) {
        _allowUndefined = value;
        return this;
    }

    /**
     * Adds a validation schema for an object property.
     * <p>
     * This method returns reference to this exception to implement Builder pattern
     * to chain additional calls.
     *
     * @param schema a property validation schema to be added.
     * @return this validation schema.
     * @see PropertySchema
     */
    public ObjectSchema withProperty(PropertySchema schema) {
        _properties = _properties != null ? _properties : new ArrayList<>();
        _properties.add(schema);
        return this;
    }

    /**
     * Adds a validation schema for a required object property.
     *
     * @param name  a property name.
     * @param type  (optional) a property schema or type.
     * @param rules (optional) a list of property validation rules.
     * @return the validation schema
     */
    public ObjectSchema withRequiredProperty(String name, Object type, IValidationRule... rules) {
        _properties = _properties != null ? _properties : new ArrayList<>();
        PropertySchema schema = new PropertySchema(name, type);
        schema.setRules(Arrays.asList(rules));
        schema.makeRequired();
        return withProperty(schema);
    }

    /**
     * Adds a validation schema for an optional object property.
     *
     * @param name  a property name.
     * @param type  (optional) a property schema or type.
     * @param rules (optional) a list of property validation rules.
     * @return the validation schema
     */
    public ObjectSchema withOptionalProperty(String name, Object type, IValidationRule... rules) {
        _properties = _properties != null ? _properties : new ArrayList<>();
        PropertySchema schema = new PropertySchema(name, type);
        schema.setRules(Arrays.asList(rules));
        schema.makeOptional();
        return withProperty(schema);
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
        super.performValidation(path, value, results);

        if (value == null)
            return;

        String name = path != null ? path : "value";
        Map<String, Object> properties = ObjectReader.getProperties(value);

        // Process defined properties
        if (_properties != null) {
            for (PropertySchema propertySchema : _properties) {
                String processedName = null;

                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String propertyName = entry.getKey();
                    Object propertyValue = entry.getValue();
                    // Find properties case insensitive
                    if (propertyName.equalsIgnoreCase(propertySchema.getName())) {
                        propertySchema.performValidation(path, propertyValue, results);
                        processedName = propertyName;
                        break;
                    }
                }

                if (processedName == null)
                    propertySchema.performValidation(path, null, results);
                else
                    properties.remove(processedName);
            }
        }

        // Process unexpected properties
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propertyPath = path == null || path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();

            results.add(new ValidationResult(propertyPath, ValidationResultType.Warning, "UNEXPECTED_PROPERTY",
                    name + " contains unexpected property " + entry.getKey(), null, entry.getKey()));
        }
    }
}
