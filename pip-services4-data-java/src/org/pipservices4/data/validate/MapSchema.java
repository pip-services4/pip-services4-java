package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.reflect.ObjectReader;
import org.pipservices4.commons.convert.*;

/**
 * Schema to validate maps.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * MapSchema schema = new MapSchema(TypeCode.String, TypeCode.Integer);
 *
 * schema.validate({ "key1": "A", "key2": "B" });       // Result: no errors
 * schema.validate({ "key1": 1, "key2": 2 });           // Result: element type mismatch
 * schema.validate(new int[]{ 1, 2, 3 });                        // Result: type mismatch
 * }
 * </pre>
 */
public class MapSchema extends Schema {
    private Object _keyType;
    private Object _valueType;

    /**
     * Creates a new instance of validation schema.
     */
    public MapSchema() {
    }

    /**
     * Creates a new instance of validation schema and sets its values.
     *
     * @param keyType   a type of map keys. Null means that keys may have any type.
     * @param valueType a type of map values. Null means that values may have any
     *                  type.
     * @see IValidationRule
     * @see TypeCode
     */
    public MapSchema(Object keyType, Object valueType) {
        _keyType = keyType;
        _valueType = valueType;
    }

    /**
     * Creates a new instance of validation schema and sets its values.
     *
     * @param keyType   a type of map keys. Null means that keys may have any type.
     * @param valueType a type of map values. Null means that values may have any type.
     * @param required  (optional) true to always require non-null values.
     * @param rules     (optional) a list with validation rules.
     */
    public MapSchema(Object keyType, Object valueType, Boolean required, List<IValidationRule> rules) {
        super(required, rules);

        this._keyType = keyType;
        this._valueType = valueType;
    }

    /**
     * Gets the type of map keys. Null means that keys may have any type.
     *
     * @return the type of map keys.
     */
    public Object getKeyType() {
        return _keyType;
    }

    /**
     * Sets the type of map keys. Null means that keys may have any type.
     *
     * @param value a type of map keys.
     */
    public void setKeyType(Object value) {
        _keyType = value;
    }

    /**
     * Gets the type of map values. Null means that values may have any type.
     *
     * @return the type of map values.
     */
    public Object getValueType() {
        return _valueType;
    }

    /**
     * Sets the type of map values. Null means that values may have any type.
     *
     * @param value a type of map values.
     */
    public void setValueType(Object value) {
        _valueType = value;
    }

    /**
     * Validates a given value against the schema and configured validation rules.
     *
     * @param path    a dot notation path to the value.
     * @param value   a value to be validated.
     * @param results a list with validation results to add new results.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void performValidation(String path, Object value, List<ValidationResult> results) {
        String name = path != null ? path : "value";
        value = ObjectReader.getValue(value);

        super.performValidation(path, value, results);

        if (value == null)
            return;

        if (value instanceof Map<?, ?>) {
            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Object key : map.keySet()) {
                String elementPath = path == null || path.isEmpty() ? key.toString() : path + "." + key;

                performTypeValidation(elementPath, _keyType, key, results);
                performTypeValidation(elementPath, _valueType, map.get(key), results);
            }
        } else {
            results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_ISNOT_MAP",
                    name + " type must be Map", "Map", value.getClass()));
        }
    }
}
