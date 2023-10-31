package org.pipservices4.commons.convert;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

/**
 * Converts arbitrary values into map objects.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Objects: property names as keys, property values as values
 * <li>Arrays: element indexes as keys, elements as values
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Map<String, Object> value1 = MapConverted.toNullableMap("ABC"); // Result: null
 * Map<String, Object> value2 = MapConverted.toNullableMap({ key: 123 }); // Result: { key: 123 }
 * Map<String, Object> value3 = MapConverted.toNullableMap(new int[]{1, 2, 3}); // Result: { "0": 1, "1": 2, "2": 3 }
 * }
 * </pre>
 */
public class MapConverter {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
    };

    private static Map<String, Object> listToMap(Collection<Object> list) {
        Map<String, Object> result = new HashMap<>();
        int index = 0;
        for (Object item : list) {
            result.put(Integer.toString(index), item);
            index++;
        }
        return result;
    }

    private static Map<String, Object> arrayToMap(Object[] array) {
        Map<String, Object> result = new HashMap<>();
        int index = 0;
        for (Object item : array) {
            result.put(Integer.toString(index), item);
            index++;
        }
        return result;
    }

    private static Map<String, Object> mapToMap(Map<Object, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            result.put(StringConverter.toString(entry.getKey()), entry.getValue());
        }
        return result;
    }

    /**
     * Converts value into map object or returns null when conversion is not
     * possible.
     *
     * @param value the value to convert.
     * @return map object or null when conversion is not supported.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toNullableMap(Object value) {
        if (value == null)
            return null;

        Class<?> valueClass = value.getClass();
        if (valueClass.isPrimitive())
            return null;

        if (value instanceof Map<?, ?>)
            return mapToMap((Map<Object, Object>) value);

        if (valueClass.isArray())
            return arrayToMap((Object[]) value);

        if (value instanceof Collection<?>)
            return listToMap((Collection<Object>) value);

        try {
            return mapper.convertValue(value, typeRef);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Converts value into map object or returns empty map when conversion is not
     * possible
     *
     * @param value the value to convert.
     * @return map object or empty map when conversion is not supported.
     * @see MapConverter#toNullableMap(Object)
     */
    public static Map<String, Object> toMap(Object value) {
        Map<String, Object> result = toNullableMap(value);
        return result != null ? result : new HashMap<>();
    }

    /**
     * Converts value into map object or returns default when conversion is not
     * possible
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return map object or empty map when conversion is not supported.
     * @see MapConverter#toNullableMap(Object)
     */
    public static Map<String, Object> toMapWithDefault(Object value, Map<String, Object> defaultValue) {
        Map<String, Object> result = toNullableMap(value);
        return result != null ? result : defaultValue;
    }
}
