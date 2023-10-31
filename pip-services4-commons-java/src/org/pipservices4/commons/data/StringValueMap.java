package org.pipservices4.commons.data;

import java.time.*;
import java.util.*;

import org.pipservices4.commons.convert.*;

/**
 * Cross-language implementation of a map (dictionary) where all keys and values are strings.
 * The stored values can be converted to different types using variety of accessor methods.
 * <p>
 * The string map is highly versatile. It can be converted into many formats, stored and
 * sent over the wire.
 * <p>
 * This class is widely used in Pip.Services as a basis for variety of classes, such as
 * <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/config/ConfigParams.html">ConfigParams</a>,
 * <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/ConnectionParams.html">ConnectionParams</a>,
 * <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/CredentialParams.html">CredentialParams</a> and others.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * StringValueMap value1 = StringValueMap.fromString("key1=1;key2=123.456;key3=2018-01-01");
 *
 * value1.getAsBoolean("key1");   // Result: true
 * value1.getAsInteger("key2");   // Result: 123
 * value1.getAsFloat("key2");     // Result: 123.456
 * value1.getAsDateTime("key3");  // Result: new Date(2018,0,1)
 * }
 * </pre>
 *
 * @see StringConverter
 * @see TypeConverter
 * @see BooleanConverter
 * @see IntegerConverter
 * @see LongConverter
 * @see DoubleConverter
 * @see FloatConverter
 * @see DateTimeConverter
 */
public class StringValueMap extends HashMap<String, String> implements ICloneable {
    private static final long serialVersionUID = -7743560121354524782L;

    public StringValueMap() {
    }

    /**
     * Creates a new instance of the map and assigns its value.
     *
     * @param map (optional) values to initialize this map.
     */
    public StringValueMap(Map<?, ?> map) {
        append(map);
    }

    /**
     * Gets a map element specified by its key.
     *
     * @param key a key of the element to get.
     * @return the value of the map element.
     */
    public String get(String key) {
        return super.get(key);
    }

    /**
     * Gets keys of all elements stored in this map.
     *
     * @return a list with all map keys.
     */
    public List<String> getKeys() {
        return new ArrayList<>(this.keySet());
    }

    /**
     * Puts a new value into map element specified by its key.
     *
     * @param key   a key of the element to put.
     * @param value a new value for map element.
     */
    public void put(String key, Object value) {
        this.put(key, StringConverter.toNullableString(value));
    }

    /**
     * Appends new elements to this map.
     *
     * @param map a map with elements to be added.
     */
    public void append(Map<?, ?> map) {
        if (map == null || map.size() == 0)
            return;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            put(StringConverter.toString(entry.getKey()), StringConverter.toNullableString(entry.getValue()));
        }
    }

    /**
     * Gets a number of elements stored in this map.
     *
     * @return the number of elements in this map.
     */
    public int length() {
        return this.size();
    }

    /**
     * Gets the value stored in this map element without any conversions
     *
     * @return the value of the map element.
     */
    public Object getAsObject() {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : this.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Sets a new value for this array element
     *
     * @param value the new object value.
     */
    public void setAsObject(Object value) {
        clear();
        Map<String, Object> values = MapConverter.toMap(value);
        append(values);
    }

    /**
     * Gets the value stored in map element without any conversions. When element
     * key is not defined it returns the entire map value.
     *
     * @param key (optional) a key of the element to get
     * @return the element value or value of the map when index is not defined.
     */
    public Object getAsObject(String key) {
        return get(key);
    }

    /**
     * Sets a new value to map element specified by its index. When the index is not
     * defined, it resets the entire map value. This method has double purpose
     * because method overrides are not supported in JavaScript.
     *
     * @param key   (optional) a key of the element to set
     * @param value a new element or map value.
     */
    public void setAsObject(String key, Object value) {
        put(key, StringConverter.toNullableString(value));
    }

    /**
     * Converts map element into a string or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return string value of the element or null if conversion is not supported.
     * @see StringConverter#toNullableString(Object)
     */
    public String getAsNullableString(String key) {
        Object value = getAsObject(key);
        return StringConverter.toNullableString(value);
    }

    /**
     * Converts map element into a string or returns "" if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return string value of the element or "" if conversion is not supported.
     * @see #getAsStringWithDefault(String, String)
     */
    public String getAsString(String key) {
        return getAsStringWithDefault(key, null);
    }

    /**
     * Converts map element into a string or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return string value of the element or default value if conversion is not
     * supported.
     * @see StringConverter#toStringWithDefault(Object, String)
     */
    public String getAsStringWithDefault(String key, String defaultValue) {
        Object value = getAsObject(key);
        return StringConverter.toStringWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into a boolean or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return boolean value of the element or null if conversion is not supported.
     * @see BooleanConverter#toNullableBoolean(Object)
     */
    public Boolean getAsNullableBoolean(String key) {
        Object value = getAsObject(key);
        return BooleanConverter.toNullableBoolean(value);
    }

    /**
     * Converts map element into a boolean or returns false if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return boolean value of the element or false if conversion is not supported.
     * @see #getAsBooleanWithDefault(String, boolean)
     */
    public boolean getAsBoolean(String key) {
        return getAsBooleanWithDefault(key, false);
    }

    /**
     * Converts map element into a boolean or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return boolean value of the element or default value if conversion is not
     * supported.
     * @see BooleanConverter#toBooleanWithDefault(Object, boolean)
     */
    public boolean getAsBooleanWithDefault(String key, boolean defaultValue) {
        Object value = getAsObject(key);
        return BooleanConverter.toBooleanWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into an integer or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return integer value of the element or null if conversion is not supported.
     * @see IntegerConverter#toNullableInteger(Object)
     */
    public Integer getAsNullableInteger(String key) {
        Object value = getAsObject(key);
        return IntegerConverter.toNullableInteger(value);
    }

    /**
     * Converts map element into an integer or returns 0 if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return integer value of the element or 0 if conversion is not supported.
     * @see #getAsIntegerWithDefault(String, int)
     */
    public int getAsInteger(String key) {
        return getAsIntegerWithDefault(key, 0);
    }

    /**
     * Converts map element into an integer or returns default value if conversion
     * is not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return integer value of the element or default value if conversion is not
     * supported.
     * @see IntegerConverter#toIntegerWithDefault(Object, int)
     */
    public int getAsIntegerWithDefault(String key, int defaultValue) {
        Object value = getAsObject(key);
        return IntegerConverter.toIntegerWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into a long or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return long value of the element or null if conversion is not supported.
     * @see LongConverter#toNullableLong(Object)
     */
    public Long getAsNullableLong(String key) {
        Object value = getAsObject(key);
        return LongConverter.toNullableLong(value);
    }

    /**
     * Converts map element into a long or returns 0 if conversion is not possible.
     *
     * @param key a key of element to get.
     * @return long value of the element or 0 if conversion is not supported.
     * @see #getAsLongWithDefault(String, long)
     */
    public long getAsLong(String key) {
        return getAsLongWithDefault(key, 0);
    }

    /**
     * Converts map element into a long or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return long value of the element or default value if conversion is not
     * supported.
     * @see LongConverter#toLongWithDefault(Object, long)
     */
    public long getAsLongWithDefault(String key, long defaultValue) {
        Object value = getAsObject(key);
        return LongConverter.toLongWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into a float or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return float value of the element or null if conversion is not supported.
     * @see FloatConverter#toNullableFloat(Object)
     */
    public Float getAsNullableFloat(String key) {
        Object value = getAsObject(key);
        return FloatConverter.toNullableFloat(value);
    }

    /**
     * Converts map element into a float or returns 0 if conversion is not possible.
     *
     * @param key a key of element to get.
     * @return float value of the element or 0 if conversion is not supported.
     * @see #getAsFloatWithDefault(String, float)
     */
    public float getAsFloat(String key) {
        return getAsFloatWithDefault(key, 0);
    }

    /**
     * Converts map element into a float or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return float value of the element or default value if conversion is not
     * supported.
     * @see FloatConverter#toFloatWithDefault(Object, float)
     */
    public float getAsFloatWithDefault(String key, float defaultValue) {
        Object value = getAsObject(key);
        return FloatConverter.toFloatWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into a double or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return double value of the element or null if conversion is not supported.
     * @see DoubleConverter#toNullableDouble(Object)
     */
    public Double getAsNullableDouble(String key) {
        Object value = getAsObject(key);
        return DoubleConverter.toNullableDouble(value);
    }

    /**
     * Converts map element into a double or returns 0 if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return double value of the element or 0 if conversion is not supported.
     * @see #getAsDoubleWithDefault(String, double)
     */
    public double getAsDouble(String key) {
        return getAsDoubleWithDefault(key, 0);
    }

    /**
     * Converts map element into a double or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return double value of the element or default value if conversion is not
     * supported.
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     */
    public double getAsDoubleWithDefault(String key, double defaultValue) {
        Object value = getAsObject(key);
        return DoubleConverter.toDoubleWithDefault(value, defaultValue);
    }

    /**
     * Converts map element into a Date or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return ZonedDateTime value of the element or null if conversion is not
     * supported.
     * @see DateTimeConverter#toNullableDateTime(Object)
     */
    public ZonedDateTime getAsNullableDateTime(String key) {
        Object value = getAsObject(key);
        return DateTimeConverter.toNullableDateTime(value);
    }

    /**
     * Converts map element into a Date or returns the current date if conversion is
     * not possible.
     *
     * @param key a key of element to get.
     * @return ZonedDateTime value of the element or the current date if conversion
     * is not supported.
     * @see StringValueMap#getAsDateTimeWithDefault
     */
    public ZonedDateTime getAsDateTime(String key) {
        return getAsDateTimeWithDefault(key, null);
    }

    /**
     * Converts map element into a Date or returns default value if conversion is
     * not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return ZonedDateTime value of the element or default value if conversion is
     * not supported.
     * @see DateTimeConverter#toDateTimeWithDefault(Object, ZonedDateTime)
     */
    public ZonedDateTime getAsDateTimeWithDefault(String key, ZonedDateTime defaultValue) {
        Object value = getAsObject(key);
        return DateTimeConverter.toDateTimeWithDefault(value, defaultValue);
    }

    public Duration getAsNullableDuration(String key) {
        Object value = getAsObject(key);
        return DurationConverter.toNullableDuration(value);
    }

    public Duration getAsDuration(String key) {
        return getAsDurationWithDefault(key, null);
    }

    public Duration getAsDurationWithDefault(String key, Duration defaultValue) {
        Object value = getAsObject(key);
        return DurationConverter.toDurationWithDefault(value, defaultValue);
    }

    public <T extends Enum<T>> T getAsNullableEnum(Class<T> type, String key) {
        Object value = getAsObject(key);
        return EnumConverter.toNullableEnum(type, value);
    }

    public <T extends Enum<T>> T getAsEnum(Class<T> type, String key) {
        return getAsEnumWithDefault(type, key, null);
    }

    public <T extends Enum<T>> T getAsEnumWithDefault(Class<T> type, String key, T defaultValue) {
        Object value = getAsObject(key);
        return EnumConverter.toEnumWithDefault(type, value, defaultValue);
    }

    /**
     * Converts map element into a value defined by specied typecode. If conversion
     * is not possible it returns null.
     *
     * @param type the Class type that defined the type of the result
     * @param key  a key of element to get.
     * @return element value defined by the typecode or null if conversion is not
     * supported.
     * @see TypeConverter#toNullableType(Class, Object)
     */
    public <T> T getAsNullableType(Class<T> type, String key) {
        Object value = getAsObject(key);
        return TypeConverter.toNullableType(type, value);
    }

    /**
     * Converts map element into a value defined by specied typecode. If conversion
     * is not possible it returns default value for the specified type.
     *
     * @param type the Class type that defined the type of the result
     * @param key  a key of element to get.
     * @return element value defined by the typecode or default if conversion is not
     * supported.
     * @see #getAsTypeWithDefault(Class, String, Object)
     */
    public <T> T getAsType(Class<T> type, String key) {
        return getAsTypeWithDefault(type, key, null);
    }

    /**
     * Converts map element into a value defined by specied typecode. If conversion
     * is not possible it returns default value.
     *
     * @param type         the Class type that defined the type of the result
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return element value defined by the typecode or default value if conversion
     * is not supported.
     * @see TypeConverter#toTypeWithDefault(Class, Object, Object)
     */
    public <T> T getAsTypeWithDefault(Class<T> type, String key, T defaultValue) {
        Object value = getAsObject(key);
        return TypeConverter.toTypeWithDefault(type, value, defaultValue);
    }

    /**
     * Converts map element into an AnyValue or returns an empty AnyValue if
     * conversion is not possible.
     *
     * @param key a key of element to get.
     * @return AnyValue value of the element or empty AnyValue if conversion is not
     * supported.
     * @see AnyValue
     * @see AnyValue#AnyValue(Object)
     */
    public AnyValue getAsValue(String key) {
        Object value = getAsObject(key);
        return new AnyValue(value);
    }

    /**
     * Converts map element into an AnyValueArray or returns null if conversion is
     * not possible.
     *
     * @param key a key of element to get.
     * @return AnyValueArray value of the element or null if conversion is not
     * supported.
     * @see AnyValueArray
     * @see AnyValueArray#fromValue(Object)
     */
    public AnyValueArray getAsNullableArray(String key) {
        Object value = getAsObject(key);
        return value != null ? AnyValueArray.fromValue(value) : null;
    }

    /**
     * Converts map element into an AnyValueArray or returns empty AnyValueArray if
     * conversion is not possible.
     *
     * @param key a key of element to get.
     * @return AnyValueArray value of the element or empty AnyValueArray if
     * conversion is not supported.
     * @see AnyValueArray
     * @see AnyValueArray#fromValue(Object)
     */
    public AnyValueArray getAsArray(String key) {
        Object value = getAsObject(key);
        return AnyValueArray.fromValue(value);
    }

    /**
     * Converts map element into an AnyValueArray or returns default value if
     * conversion is not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return AnyValueArray value of the element or default value if conversion is
     * not supported.
     * @see AnyValueArray
     * @see #getAsNullableArray(String)
     */
    public AnyValueArray getAsArrayWithDefault(String key, AnyValueArray defaultValue) {
        AnyValueArray result = getAsNullableArray(key);
        return result != null ? result : defaultValue;
    }

    /**
     * Converts map element into an AnyValueMap or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return AnyValueMap value of the element or null if conversion is not
     * supported.
     * @see AnyValueMap#fromValue(Object)
     */
    public AnyValueMap getAsNullableMap(String key) {
        Object value = getAsObject(key);
        return value != null ? AnyValueMap.fromValue(value) : null;
    }

    /**
     * Converts map element into an AnyValueMap or returns empty AnyValueMap if
     * conversion is not possible.
     *
     * @param key a key of element to get.
     * @return AnyValueMap value of the element or empty AnyValueMap if conversion
     * is not supported.
     * @see AnyValueMap#fromValue(Object)
     */
    public AnyValueMap getAsMap(String key) {
        Object value = getAsObject(key);
        return AnyValueMap.fromValue(value);
    }

    /**
     * Converts map element into an AnyValueMap or returns default value if
     * conversion is not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return AnyValueMap value of the element or default value if conversion is
     * not supported.
     * @see #getAsNullableMap(String)
     */
    public AnyValueMap getAsMapWithDefault(String key, AnyValueMap defaultValue) {
        AnyValueMap result = getAsNullableMap(key);
        return result != null ? result : defaultValue;
    }

    /**
     * Gets a string representation of the object. The result is a
     * semicolon-separated list of key-value pairs as
     * "key1=value1;key2=value2;key=value3"
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Todo: User encoder
        for (Map.Entry<String, String> entry : this.entrySet()) {
            if (builder.length() > 0)
                builder.append(';');

            if (entry.getValue() != null)
                builder.append(entry.getKey()).append('=').append(entry.getValue());
            else
                builder.append(entry.getKey());
        }

        return builder.toString();
    }

    /**
     * Creates a binary clone of this object.
     *
     * @return a clone of this object.
     */
    public StringValueMap clone() {
        return new StringValueMap(this);
    }

    /**
     * Creates a new StringValueMap from a list of key-value pairs called tuples.
     *
     * @param tuples a list of values where odd elements are keys and the following
     *               even elements are values
     * @return a newly created StringValueMap.
     * @see #fromTuplesArray(Object[])
     */
    public static StringValueMap fromTuples(Object... tuples) {
        return StringValueMap.fromTuplesArray(tuples);
    }

    /**
     * Creates a new StringValueMap from a list of key-value pairs called tuples.
     * The method is similar to [[fromTuples]] but tuples are passed as array
     * instead of parameters.
     *
     * @param tuples a list of values where odd elements are keys and the following
     *               even elements are values
     * @return a newly created StringValueMap.
     */
    public static StringValueMap fromTuplesArray(Object[] tuples) {
        StringValueMap result = new StringValueMap();
        if (tuples == null || tuples.length == 0)
            return result;

        for (int index = 0; index < tuples.length; index += 2) {
            if (index + 1 >= tuples.length)
                break;

            String name = StringConverter.toString(tuples[index]);
            String value = StringConverter.toNullableString(tuples[index + 1]);

            result.put(name, value);
        }

        return result;
    }

    /**
     * Parses semicolon-separated key-value pairs and returns them as a
     * StringValueMap.
     *
     * @param line semicolon-separated key-value list to initialize StringValueMap.
     * @return a newly created StringValueMap.
     */
    public static StringValueMap fromString(String line) {
        StringValueMap result = new StringValueMap();
        if (line == null || line.length() == 0)
            return result;

        // Todo: User tokenizer / decoder
        String[] tokens = line.split(";", -1);

        for (String token : tokens) {
            if (token.length() == 0)
                continue;
            int index = token.indexOf('=');
            String key = index > 0 ? token.substring(0, index).trim() : token.trim();
            String val = index > 0 ? token.substring(index + 1).trim() : null;
            result.put(key, val);
        }

        return result;
    }

    /**
     * Creates a new AnyValueMap by merging two or more maps. Maps defined later in
     * the list override values from previously defined maps.
     *
     * @param maps an array of maps to be merged
     * @return a newly created AnyValueMap.
     */
    public static StringValueMap fromMaps(Map<?, ?>... maps) {
        StringValueMap result = new StringValueMap();
        if (maps != null && maps.length > 0) {
            for (Map<?, ?> map : maps)
                result.append(map);
        }
        return result;
    }

}
