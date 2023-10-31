package org.pipservices4.commons.data;

import java.io.IOException;
import java.time.*;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pipservices4.commons.convert.*;

/**
 * Cross-language implementation of dynamic object array what can hold values of any type.
 * The stored values can be converted to different types using variety of accessor methods.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * AnyValueArray value1 = new AnyValueArray(new Object[]{1, "123.456", "2018-01-01"});
 *
 * value1.getAsBoolean(0);   // Result: true
 * value1.getAsInteger(1);   // Result: 123
 * value1.getAsFloat(1);     // Result: 123.456
 * value1.getAsDateTime(2);  // Result: new Date(2018,0,1)
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
 * @see ICloneable
 */
public class AnyValueArray extends ArrayList<Object> implements ICloneable {
    private static final long serialVersionUID = 4856478344826232231L;

    public AnyValueArray() {
    }

    /**
     * Creates a new instance of the array and assigns its value.
     *
     * @param values (optional) values to initialize this array.
     */
    public AnyValueArray(Object[] values) {
        append(values);
    }

    /**
     * Creates a new instance of the array and assigns its value.
     *
     * @param values (optional) values to initialize this array.
     */
    public AnyValueArray(Iterable<?> values) {
        append(values);
    }

    /**
     * Gets the value stored in this array element without any conversions
     *
     * @return the value of the array element.
     */
    public Object getAsObject() {
        return new ArrayList<>(this);
    }

    /**
     * Puts a new value into array element specified by its index.
     *
     * @param index an index of the element to put.
     * @param value a new value for array element.
     */
    public void put(int index, Object value) {
        this.set(index, value);
    }

//	/**
//	 * Sets a new value for this array element
//	 *
//	 * @param value the new object value.
//	 */
//	public void setAsObject(Object value) {
//		clear();
//		List<Object> elements = ArrayConverter.toArray(value);
//		append(elements);
//	}

    /**
     * Appends new elements to this array.
     *
     * @param elements a list of elements to be added.
     */
    public void append(Iterable<?> elements) {
        if (elements != null) {
            for (Object item : elements)
                add(item);
        }
    }

    /**
     * Appends new elements to this array.
     *
     * @param elements a list of elements to be added.
     */
    public void append(Object[] elements) {
        if (elements != null) {
            this.addAll(Arrays.asList(elements));
        }
    }

    /**
     * Gets an array element specified by its index.
     *
     * @param index an index of the element to get.
     * @return the value of the array element.
     */
    public Object getAsObject(int index) {
        return get(index);
    }

    /**
     * Gets an array element specified by its index.
     *
     * @param objectType type of returned object
     * @param index      an index of the element to get.
     * @return the value of the array element.
     */
    public <T> T getAsObject(Class<T> objectType, int index) {
        String strValue = "";
        var value = get(index);
        try {
            if (!(value instanceof String))
                strValue = JsonConverter.toJson(value);
            return JsonConverter.fromJson(objectType, strValue);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets an array element specified by its index.
     *
     * @param objectType generic type of returned object
     * @param index      an index of the element to get.
     * @return the value of the array element.
     */
    public <T> T getAsObject(TypeReference<T> objectType, int index) {
        String strValue = "";
        var value = get(index);
        try {
            if (!(value instanceof String))
                strValue = JsonConverter.toJson(value);
            return JsonConverter.fromJson(objectType, strValue);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets a new value into array element specified by its index.
     *
     * @param index an index of the element to put.
     * @param value a new value for array element.
     */
    public void setAsObject(int index, Object value) {
        if (value == null) {
            value = index; // originally was not present
            clear();
            List<Object> elements = ArrayConverter.toArray(value);
            append(elements);
        } else {
            set(index, value);
        }
    }

//    @Override
//    public void add(Object value) {
//    	add(new AnyValue(value));
//    }

    /**
     * Converts array element into a string or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return string value of the element or null if conversion is not supported.
     * @see StringConverter#toNullableString(Object)
     */
    public String getAsNullableString(int index) {
        Object value = getAsObject(index);
        return StringConverter.toNullableString(value);
    }

    /**
     * Converts array element into a string or returns "" if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return string value ot the element or "" if conversion is not supported.
     * @see #getAsStringWithDefault(int, String)
     */
    public String getAsString(int index) {
        return getAsStringWithDefault(index, null);
    }

    /**
     * Converts array element into a string or returns default value if conversion
     * is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return string value ot the element or default value if conversion is not
     * supported.
     * @see StringConverter#toStringWithDefault(Object, String)
     */
    public String getAsStringWithDefault(int index, String defaultValue) {
        Object value = getAsObject(index);
        return StringConverter.toStringWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into a boolean or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return boolean value of the element or null if conversion is not supported.
     * @see BooleanConverter#toNullableBoolean(Object)
     */
    public Boolean getAsNullableBoolean(int index) {
        Object value = getAsObject(index);
        return BooleanConverter.toNullableBoolean(value);
    }

    /**
     * Converts array element into a boolean or returns false if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return boolean value ot the element or false if conversion is not supported.
     * @see #getAsBooleanWithDefault(int, boolean)
     */
    public Boolean getAsBoolean(int index) {
        return getAsBooleanWithDefault(index, false);
    }

    /**
     * Converts array element into a boolean or returns default value if conversion
     * is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return boolean value ot the element or default value if conversion is not
     * supported.
     * @see BooleanConverter#toBooleanWithDefault(Object, boolean)
     */
    public boolean getAsBooleanWithDefault(int index, boolean defaultValue) {
        Object value = getAsObject(index);
        return BooleanConverter.toBooleanWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into an integer or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return integer value of the element or null if conversion is not supported.
     * @see IntegerConverter#toNullableInteger(Object)
     */
    public Integer getAsNullableInteger(int index) {
        Object value = getAsObject(index);
        return IntegerConverter.toNullableInteger(value);
    }

    /**
     * Converts array element into an integer or returns 0 if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return integer value ot the element or 0 if conversion is not supported.
     * @see #getAsIntegerWithDefault(int, int)
     */
    public int getAsInteger(int index) {
        return getAsIntegerWithDefault(index, 0);
    }

    /**
     * Converts array element into an integer or returns default value if conversion
     * is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return integer value ot the element or default value if conversion is not
     * supported.
     * @see IntegerConverter#toIntegerWithDefault(Object, int)
     */
    public int getAsIntegerWithDefault(int index, int defaultValue) {
        Object value = getAsObject(index);
        return IntegerConverter.toIntegerWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into a long or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return long value of the element or null if conversion is not supported.
     * @see LongConverter#toNullableLong(Object)
     */
    public Long getAsNullableLong(int index) {
        Object value = getAsObject(index);
        return LongConverter.toNullableLong(value);
    }

    /**
     * Converts array element into a long or returns 0 if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return long value ot the element or 0 if conversion is not supported.
     * @see #getAsLongWithDefault(int, long)
     */
    public long getAsLong(int index) {
        return getAsLongWithDefault(index, 0);
    }

    /**
     * Converts array element into a long or returns default value if conversion is
     * not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return long value ot the element or default value if conversion is not
     * supported.
     * @see LongConverter#toLongWithDefault(Object, long)
     */
    public long getAsLongWithDefault(int index, long defaultValue) {
        Object value = getAsObject(index);
        return LongConverter.toLongWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into a float or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return float value of the element or null if conversion is not supported.
     * @see FloatConverter#toNullableFloat(Object)
     */
    public Float getAsNullableFloat(int index) {
        Object value = getAsObject(index);
        return FloatConverter.toNullableFloat(value);
    }

    /**
     * Converts array element into a float or returns 0 if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return float value ot the element or 0 if conversion is not supported.
     * @see #getAsFloatWithDefault(int, float)
     */
    public float getAsFloat(int index) {
        return getAsFloatWithDefault(index, 0);
    }

    /**
     * Converts array element into a float or returns default value if conversion is
     * not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return float value ot the element or default value if conversion is not
     * supported.
     * @see FloatConverter#toFloatWithDefault(Object, float)
     */
    public float getAsFloatWithDefault(int index, float defaultValue) {
        Object value = getAsObject(index);
        return FloatConverter.toFloatWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into a double or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return double value of the element or null if conversion is not supported.
     * @see DoubleConverter#toNullableDouble(Object)
     */
    public Double getAsNullableDouble(int index) {
        Object value = getAsObject(index);
        return DoubleConverter.toNullableDouble(value);
    }

    /**
     * Converts array element into a double or returns 0 if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return double value ot the element or 0 if conversion is not supported.
     * @see #getAsDoubleWithDefault(int, double)
     */
    public double getAsDouble(int index) {
        return getAsDoubleWithDefault(index, 0);
    }

    /**
     * Converts array element into a double or returns default value if conversion
     * is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return double value ot the element or default value if conversion is not
     * supported.
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     */
    public double getAsDoubleWithDefault(int index, double defaultValue) {
        Object value = getAsObject(index);
        return DoubleConverter.toDoubleWithDefault(value, defaultValue);
    }

    /**
     * Converts array element into a Date or returns null if conversion is not
     * possible.
     *
     * @param index an index of element to get.
     * @return ZonedDateTime value of the element or null if conversion is not supported.
     * @see DateTimeConverter#toNullableDateTime(Object)
     */
    public ZonedDateTime getAsNullableDateTime(int index) {
        Object value = getAsObject(index);
        return DateTimeConverter.toNullableDateTime(value);
    }

    /**
     * Converts array element into a Date or returns the current date if conversion
     * is not possible.
     *
     * @param index an index of element to get.
     * @return ZonedDateTime value ot the element or the current date if conversion is not
     * supported.
     * @see #getAsDateTimeWithDefault(int, ZonedDateTime)
     */
    public ZonedDateTime getAsDateTime(int index) {
        return getAsDateTimeWithDefault(index, null);
    }

    /**
     * Converts array element into a Date or returns default value if conversion is
     * not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return ZonedDateTime value ot the element or default value if conversion is not
     * supported.
     * @see DateTimeConverter#toDateTimeWithDefault(Object, ZonedDateTime)
     */
    public ZonedDateTime getAsDateTimeWithDefault(int index, ZonedDateTime defaultValue) {
        Object value = getAsObject(index);
        return DateTimeConverter.toDateTimeWithDefault(value, defaultValue);
    }

    public Duration getAsNullableDuration(int index) {
        Object value = getAsObject(index);
        return DurationConverter.toNullableDuration(value);
    }

    public Duration getAsDuration(int index) {
        return getAsDurationWithDefault(index, null);
    }

    public Duration getAsDurationWithDefault(int index, Duration defaultValue) {
        Object value = getAsObject(index);
        return DurationConverter.toDurationWithDefault(value, defaultValue);
    }

    public <T extends Enum<T>> T getAsNullableEnum(Class<T> type, int index) {
        Object value = getAsObject(index);
        return EnumConverter.toNullableEnum(type, value);
    }

    public <T extends Enum<T>> T getAsEnum(Class<T> type, int index) {
        return getAsEnumWithDefault(type, index, null);
    }

    public <T extends Enum<T>> T getAsEnumWithDefault(Class<T> type, int index, T defaultValue) {
        Object value = getAsObject(index);
        return EnumConverter.toEnumWithDefault(type, value, defaultValue);
    }

    /**
     * Converts array element into a value defined by specied typecode. If
     * conversion is not possible it returns null.
     *
     * @param type  the Class type that defined the type of the result
     * @param index an index of element to get.
     * @return element value defined by the typecode or null if conversion is not
     * supported.
     * @see TypeConverter#toNullableType(Class, Object)
     */
    public <T> T getAsNullableType(Class<T> type, int index) {
        Object value = getAsObject(index);
        return TypeConverter.toNullableType(type, value);
    }

    /**
     * Converts array element into a value defined by specied typecode. If
     * conversion is not possible it returns default value for the specified type.
     *
     * @param type  the Class type that defined the type of the result
     * @param index an index of element to get.
     * @return element value defined by the typecode or default if conversion is not
     * supported.
     * @see #getAsTypeWithDefault(Class, int, Object)
     */
    public <T> T getAsType(Class<T> type, int index) {
        return getAsTypeWithDefault(type, index, null);
    }

    /**
     * Converts array element into a value defined by specied typecode. If
     * conversion is not possible it returns default value.
     *
     * @param type         the Class type that defined the type of the result
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return element value defined by the typecode or default value if conversion
     * is not supported.
     * @see TypeConverter#toTypeWithDefault(Class, Object, Object)
     */
    public <T> T getAsTypeWithDefault(Class<T> type, int index, T defaultValue) {
        Object value = getAsObject(index);
        return TypeConverter.toTypeWithDefault(type, value, defaultValue);
    }

    /**
     * Converts array element into an AnyValue or returns an empty AnyValue if
     * conversion is not possible.
     *
     * @param index an index of element to get.
     * @return AnyValue value of the element or empty AnyValue if conversion is not
     * supported.
     * @see AnyValue
     * @see AnyValue#AnyValue(Object)
     */
    public AnyValue getAsValue(int index) {
        Object value = getAsObject(index);
        return new AnyValue(value);
    }

    /**
     * Converts array element into an AnyValueArray or returns null if conversion is
     * not possible.
     *
     * @param index an index of element to get.
     * @return AnyValueArray value of the element or null if conversion is not
     * supported.
     * @see #fromValue(Object)
     */
    public AnyValueArray getAsNullableArray(int index) {
        Object value = getAsObject(index);
        return value != null ? AnyValueArray.fromValue(value) : null;
    }

    /**
     * Converts array element into an AnyValueArray or returns empty AnyValueArray
     * if conversion is not possible.
     *
     * @param index an index of element to get.
     * @return AnyValueArray value of the element or empty AnyValueArray if
     * conversion is not supported.
     * @see #fromValue(Object)
     */
    public AnyValueArray getAsArray(int index) {
        Object value = getAsObject(index);
        return AnyValueArray.fromValue(value);
    }

    /**
     * Converts array element into an AnyValueArray or returns default value if
     * conversion is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return AnyValueArray value of the element or default value if conversion is
     * not supported.
     * @see #getAsNullableArray(int)
     */
    public AnyValueArray getAsArrayWithDefault(int index, AnyValueArray defaultValue) {
        AnyValueArray result = getAsNullableArray(index);
        return result != null ? result : defaultValue;
    }

    /**
     * Converts array element into an AnyValueMap or returns null if conversion is
     * not possible.
     *
     * @param index an index of element to get.
     * @return AnyValueMap value of the element or null if conversion is not
     * supported.
     * @see AnyValueMap
     * @see AnyValueMap#fromValue(Object)
     */
    public AnyValueMap getAsNullableMap(int index) {
        Object value = getAsObject(index);
        return value != null ? AnyValueMap.fromValue(value) : null;
    }

    /**
     * Converts array element into an AnyValueMap or returns empty AnyValueMap if
     * conversion is not possible.
     *
     * @param index an index of element to get.
     * @return AnyValueMap value of the element or empty AnyValueMap if conversion
     * is not supported.
     * @see AnyValueMap
     * @see AnyValueMap#fromValue(Object)
     */
    public AnyValueMap getAsMap(int index) {
        Object value = getAsObject(index);
        return AnyValueMap.fromValue(value);
    }

    /**
     * Converts array element into an AnyValueMap or returns default value if
     * conversion is not possible.
     *
     * @param index        an index of element to get.
     * @param defaultValue the default value
     * @return AnyValueMap value of the element or default value if conversion is
     * not supported.
     * @see #getAsNullableMap(int)
     */
    public AnyValueMap getAsMapWithDefault(int index, AnyValueMap defaultValue) {
        AnyValueMap result = getAsNullableMap(index);
        return result != null ? AnyValueMap.fromValue(result) : defaultValue;
    }

    /**
     * Checks if this array contains a value. The check uses direct comparison
     * between elements and the specified value.
     *
     * @param value a value to be checked
     * @return true if this array contains the value or false otherwise.
     */
    public boolean contains(Object value) {
        for (Object element : this) {
            if (value == null && element == null)
                return true;
            if (value == null || element == null)
                continue;
            if (value.equals(element))
                return true;
        }

        return false;
    }

    /**
     * Checks if this array contains a value. The check before comparison converts
     * elements and the value to type specified by type code.
     *
     * @param type  a Class type that defines a type to convert values before
     *              comparison
     * @param value a value to be checked
     * @return true if this array contains the value or false otherwise.
     * @see TypeConverter#toType(Class, Object)
     * @see TypeConverter#toNullableType(Class, Object)
     */
    public <T> boolean containsAsType(Class<T> type, Object value) {
        T typedValue = TypeConverter.toType(type, value);

        for (Object thisValue : this) {
            T thisTypedValue = TypeConverter.toNullableType(type, thisValue);

            if (typedValue == null && thisTypedValue == null)
                return true;
            if (typedValue == null || thisTypedValue == null)
                continue;
            if (typedValue.equals(thisTypedValue))
                return true;
        }

        return false;
    }

    /**
     * Creates a binary clone of this object.
     *
     * @return a clone of this object.
     */
    public Object clone() {
        return new AnyValueArray(this);
    }

    /**
     * Gets a string representation of the object. The result is a comma-separated
     * list of string representations of individual elements as
     * "value1,value2,value3"
     *
     * @return a string representation of the object.
     * @see StringConverter#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < size(); index++) {
            if (index > 0)
                builder.append(',');
            builder.append(getAsStringWithDefault(index, ""));
        }
        return builder.toString();
    }

    /**
     * Creates a new AnyValueArray from a list of values
     *
     * @param values a list of values to initialize the created AnyValueArray
     * @return a newly created AnyValueArray.
     */
    public static AnyValueArray fromValues(Object... values) {
        return new AnyValueArray(values);
    }

    /**
     * Converts specified value into AnyValueArray.
     *
     * @param value value to be converted
     * @return a newly created AnyValueArray.
     * @see ArrayConverter#toNullableArray(Object)
     */
    public static AnyValueArray fromValue(Object value) {
        List<Object> values = ArrayConverter.toNullableArray(value);
        return new AnyValueArray(values);
    }

    /**
     * Splits specified string into elements using a separator and assigns the
     * elements to a newly created AnyValueArray.
     *
     * @param values           a string value to be split and assigned to
     *                         AnyValueArray
     * @param separator        a separator to split the string
     * @param removeDuplicates (optional) true to remove duplicated elements
     * @return a newly created AnyValueArray.
     */
    public static AnyValueArray fromString(String values, String separator, boolean removeDuplicates) {
        AnyValueArray result = new AnyValueArray();

        if (values == null || values.length() == 0)
            return result;

        String[] items = values.split(separator, -1);
        for (String item : items) {
            if ((item != null && item.length() > 0) || !removeDuplicates)
                result.add(item != null ? new AnyValue(item) : null);
        }

        return result;
    }

    /**
     * Splits specified string into elements using a separator and assigns the
     * elements to a newly created AnyValueArray.
     *
     * @param values    a string value to be split and assigned to AnyValueArray
     * @param separator a separator to split the string
     * @return a newly created AnyValueArray.
     */
    public static AnyValueArray fromString(String values, String separator) {
        return AnyValueArray.fromString(values, separator, false);
    }
}
