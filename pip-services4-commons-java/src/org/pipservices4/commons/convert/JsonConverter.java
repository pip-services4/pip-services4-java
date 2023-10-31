package org.pipservices4.commons.convert;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

/**
 * Converts arbitrary values from and to JSON (JavaScript Object Notation) strings.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * T value1 = JsonConverter.fromJson("{\"key\":123}"); // Result: { key: 123 }
 * T value2 = JsonConverter.toMap({ key: 123}); // Result: "{\"key\":123}"
 * }
 * </pre>
 */
public class JsonConverter {
    private static final ObjectMapper _mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
    };

    static {
        _mapper.findAndRegisterModules();
        // _mapper.registerModule(new JavaTimeModule());
        _mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Converts JSON string into a value of type specified by Class type.
     *
     * @param type  the Class type for the data type into which 'value' is to be converted.
     * @param value the JSON string to convert.
     * @return converted object value or null when value is null.
     * @throws JsonMappingException when conversion fails for mapping reason.
     * @throws JsonParseException   when conversion fails for parse reason.
     * @throws IOException          when conversion fails for input/output stream reason.
     */
    public static <T> T fromJson(Class<T> type, String value)
            throws JsonMappingException, JsonParseException, IOException {
        if (value == null)
            return null;
        return _mapper.readValue(value, type);
    }

    /**
     * Converts JSON string into a value of generic type specified by Class type.
     *
     * @param type  the Class type for the data type into which 'value' is to be converted.
     * @param value the JSON string to convert.
     * @return converted object value or null when value is null.
     * @throws JsonMappingException when conversion fails for mapping reason.
     * @throws JsonParseException   when conversion fails for parse reason.
     * @throws IOException          when conversion fails for input/output stream reason.
     */
    public static <T> T fromJson(TypeReference<T> type, String value)
            throws JsonMappingException, JsonParseException, IOException {
        if (value == null)
            return null;
        return _mapper.readValue(value, type);
    }

    /**
     * Converts value into JSON string.
     *
     * @param value the value to convert.
     * @return JSON string or null when value is null.
     * @throws JsonProcessingException when conversion fails for any reason.
     */
    public static String toJson(Object value) throws JsonProcessingException {
        if (value == null)
            return null;
        return _mapper.writeValueAsString(value);
    }

    /**
     * Converts JSON string into map object or returns null when conversion is not
     * possible.
     *
     * @param value the JSON string to convert.
     * @return Map object value or null when conversion is not supported.
     * @see MapConverter#toNullableMap(Object)
     */
    public static Map<String, Object> toNullableMap(String value) {
        if (value == null)
            return null;

        try {
            Map<String, Object> map = _mapper.readValue(value, typeRef);
            return RecursiveMapConverter.toNullableMap(map);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Converts JSON string into map object or returns empty map when conversion is
     * not possible.
     *
     * @param value the JSON string to convert.
     * @return Map object value or empty object when conversion is not supported.
     * @see JsonConverter#toNullableMap(String)
     */
    public static Map<String, Object> toMap(String value) {
        Map<String, Object> result = toNullableMap(value);
        return result != null ? result : new HashMap<>();
    }

    /**
     * Converts JSON string into map object or returns default value when conversion
     * is not possible.
     *
     * @param value        the JSON string to convert.
     * @param defaultValue the default value.
     * @return Map object value or default when conversion is not supported.
     * @see JsonConverter#toNullableMap(String)
     */
    public static Map<String, Object> toMapWithDefault(String value, Map<String, Object> defaultValue) {
        Map<String, Object> result = toNullableMap(value);
        return result != null ? result : defaultValue;
    }

}
