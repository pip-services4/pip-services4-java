package org.pipservices4.data.validate;

import java.lang.reflect.Array;
import java.util.*;

import org.pipservices4.commons.reflect.ObjectReader;

/**
 * Schema to validate arrays.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ArraySchema schema = new ArraySchema(TypeCode.String);
 *
 * schema.validate(new String[]{"A", "B", "C"});    // Result: no errors
 * schema.validate(new int[] {1, 2, 3});          // Result: element type mismatch
 * schema.validate("A");                // Result: type mismatch
 * }
 * </pre>
 */
public class ArraySchema extends Schema {
    private Object _valueType;

    /**
     * Creates a new instance of validation schema.
     */
    public ArraySchema() {
        super();
    }

    /**
     * Creates a new instance of validation schema and sets its values.
     *
     * @param valueType a type of array elements. Null means that elements may have
     *                  any type.
     */
    public ArraySchema(Object valueType) {
        super();
        _valueType = valueType;
    }

    /**
     * Creates a new instance of validation schema and sets its values.
     *
     * @param valueType a type of array elements. Null means that elements may have any type.
     * @param required  (optional) true to always require non-null values.
     * @param rules     (optional) a list with validation rules.
     */
    public ArraySchema(Object valueType, boolean required, List<IValidationRule> rules) {
        super(required, rules);

        this._valueType = valueType;
    }

    /**
     * Gets the type of array elements. Null means that elements may have any type.
     *
     * @return the type of array elements.
     */
    public Object getValueType() {
        return _valueType;
    }

    /**
     * Sets the type of array elements. Null means that elements may have any type.
     *
     * @param value a type of array elements.
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

        if (value instanceof List<?>) {
            List<Object> list = (List<Object>) value;
            int index = 0;
            for (Object element : list) {
                String elementPath = path == null || path.isEmpty() ? Integer.toString(index) : path + "." + index;
                performTypeValidation(elementPath, _valueType, element, results);
                index++;
            }
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++) {
                Object element = Array.get(value, index);
                String elementPath = path == null || path.isEmpty() ? Integer.toString(index) : path + "." + index;
                performTypeValidation(elementPath, _valueType, element, results);
                index++;
            }
        } else {
            results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_ISNOT_ARRAY",
                    name + " type must be List or Array", "List", value.getClass()));
        }
    }
}
