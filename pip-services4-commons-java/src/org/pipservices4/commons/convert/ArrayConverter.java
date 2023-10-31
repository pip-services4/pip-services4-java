package org.pipservices4.commons.convert;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Converts arbitrary values into array objects.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * List<Object> value1 = ArrayConverter.toArray(1);        // Result: [1]
 * List<Object> value2 = ArrayConverter.listToArray("1,2,3"); // Result: ["1", "2", "3"]
 * }
 * </pre>
 */
public class ArrayConverter {

    /**
     * Converts value into array object. Single values are converted into arrays
     * with a single element.
     *
     * @param value the value to convert.
     * @return array object or null when value is null.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> toNullableArray(Object value) {
        // Return null when nothing found
        if (value == null) {
            return null;
        }
        // Convert list
        else if (value instanceof List<?>) {
            return (List<Object>) value;
        }
        // Convert array
        else if (value.getClass().isArray()) {
            List<Object> array = new ArrayList<>();
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++)
                array.add(Array.get(value, index));
            return array;
        }
        // Convert maps by taking all values and ignoring keys
        else if (value instanceof Map<?, ?>) {
            List<Object> array = new ArrayList<>();
            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Map.Entry<Object, Object> entry : map.entrySet())
                array.add(entry.getValue());
            return array;
        }
        // Convert single values
        else {
            List<Object> array = new ArrayList<>();
            array.add(value);
            return array;
        }
    }

    /**
     * Converts value into array object with empty array as default. Single values
     * are converted into arrays with single element.
     *
     * @param value the value to convert.
     * @return array object or empty array when value is null.
     * @see ArrayConverter#toNullableArray(Object)
     */
    public static List<Object> toArray(Object value) {
        List<Object> result = toNullableArray(value);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Converts value into array object with empty array as default. Single values
     * are converted into arrays with single element.
     *
     * @param value        the value to convert.
     * @param defaultValue default array object.
     * @return array object or empty array when value is null.
     * @see ArrayConverter#toNullableArray(Object)
     */
    public static List<Object> toArrayWithDefault(Object value, List<Object> defaultValue) {
        List<Object> result = toNullableArray(value);
        return result != null ? result : defaultValue;
    }

    /**
     * Converts value into array object with empty array as default. Strings with
     * comma-delimited values are split into array of strings.
     *
     * @param value the list to convert.
     * @return array object or empty array when value is null
     * @see ArrayConverter#toArray(Object)
     */
    public static List<Object> listToArray(Object value) {
        if (value == null)
            return new ArrayList<>();
        if (value instanceof String)
            value = ((String) value).split(",");
        return ArrayConverter.toArray(value);
    }

}
