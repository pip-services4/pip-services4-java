package org.pipservices4.commons.convert;

import java.util.*;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

/**
 * Converts arbitrary values into map objects using extended conversion rules.
 * This class is similar to {@link MapConverter}, but is recursively converts all values
 * stored in objects and arrays.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Map<String, Object> value1 = RecursiveMapConverted.toNullableMap("ABC"); // Result: null
 * Map<String, Object> value2 = RecursiveMapConverted.toNullableMap({ key: 123 }); // Result: { key: 123 }
 * List<Object> result = new ArrayList<Object>();
 * result.add(1);
 * result.add(new int[]{2, 3});
 * Map<String, Object> value3 = RecursiveMapConverted.toNullableMap(result); // Result: { "0": 1, { "0": 2, "1": 3 } }
 * }
 * </pre>
 */
public class RecursiveMapConverter {
    private static final ObjectMapper _mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
    };

    private static List<Object> listToMap(Collection<Object> list) {
        List<Object> result = new ArrayList<>();
        for (Object item : list)
            result.add(valueToMap(item));
        return result;
    }

    private static List<Object> arrayToMap(Object[] array) {
        List<Object> result = new ArrayList<>();
        for (Object item : array)
            result.add(valueToMap(item));
        return result;
    }

    private static Map<String, Object> mapToMap(Map<Object, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            result.put(StringConverter.toString(entry.getKey()), valueToMap(entry.getValue()));
        }
        return result;
    }

    private static Map<String, Object> mapToMap2(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), valueToMap(entry.getValue()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object valueToMap(Object value) {
        if (value == null)
            return null;
        if (value instanceof Map<?, ?>)
            return value;

        Class<?> valueClass = value.getClass();
        if (valueClass.isPrimitive())
            return value;

        if (value instanceof Map<?, ?>)
            return mapToMap((Map<Object, Object>) value);

        if (valueClass.isArray())
            return arrayToMap((Object[]) value);

        if (value instanceof Collection<?>)
            return listToMap((Collection<Object>) value);

        try {
            Map<String, Object> map = _mapper.convertValue(value, typeRef);
            return mapToMap2(map);
        } catch (Exception ex) {
            return value;
        }
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

        Object result = valueToMap(value);
        if (result instanceof Map<?, ?>)
            return (Map<String, Object>) result;
        return null;
    }

    /**
     * Converts value into map object or returns empty map when conversion is not
     * possible
     *
     * @param value the value to convert.
     * @return map object or empty map when conversion is not supported.
     * @see RecursiveMapConverter#toNullableMap(Object)
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
     * @see RecursiveMapConverter#toNullableMap(Object)
     */
    public static Map<String, Object> toMapWithDefault(Object value, Map<String, Object> defaultValue) {
        Map<String, Object> result = toNullableMap(value);
        return result != null ? result : defaultValue;
    }
}
