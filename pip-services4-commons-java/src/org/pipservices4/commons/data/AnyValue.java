package org.pipservices4.commons.data;

import java.io.*;
import java.time.*;

import com.fasterxml.jackson.core.type.TypeReference;
import org.pipservices4.commons.convert.*;

import com.fasterxml.jackson.annotation.*;

/**
 * Cross-language implementation of dynamic object what can hold value of any type.
 * The stored value can be converted to different types using variety of accessor methods.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * AnyValue value1 = new AnyValue("123.456");
 *
 * value1.getAsInteger();   // Result: 123
 * value1.getAsString();    // Result: "123.456"
 * value1.getAsFloat();     // Result: 123.456
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
public class AnyValue implements Serializable, Cloneable {
    private static final long serialVersionUID = 8543060319681670938L;

    /**
     * The value stored by this object.
     */
    private Object _value;

    public AnyValue() {
        _value = null;
    }

    /**
     * Creates a new instance of the object and assigns its value.
     *
     * @param value (optional) value to initialize this object.
     */
    public AnyValue(Object value) {
        if (value instanceof AnyValue)
            _value = ((AnyValue) value)._value;
        else
            _value = value;
    }

    public AnyValue(AnyValue value) {
        _value = value.getAsObject();
    }

    /**
     * Gets type code for the value stored in this object.
     *
     * @return type code of the object value.
     * @see TypeConverter#toTypeCode(Class)
     */
    @JsonIgnore
    public TypeCode getTypeCode() {
        return TypeConverter.toTypeCode(_value);
    }

    /**
     * Gets the value stored in this object without any conversions
     *
     * @return the object value.
     */
    @JsonProperty("value")
    public Object getAsObject() {
        return _value;
    }

    /**
     * Gets the value stored in this object without any conversions
     *
     * @param objectType type of returned object
     * @return the object value.
     */
    public <T> T getAsObject(Class<T> objectType) {
        String value = "";
        try {
            if (!(_value instanceof String))
                value = JsonConverter.toJson(_value);
            return JsonConverter.fromJson(objectType, value);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the value stored in this object without any conversions
     *
     * @param objectType type of returned object
     * @return the object value.
     */
    public <T> T getAsObject(TypeReference<T> objectType) {
        String value = "";
        try {
            if (!(_value instanceof String))
                value = JsonConverter.toJson(_value);
            return JsonConverter.fromJson(objectType, value);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Sets a new value for this object
     *
     * @param value the new object value.
     */
    public void setAsObject(Object value) {
        if (value instanceof AnyValue)
            _value = ((AnyValue) value)._value;
        else
            _value = value;
    }

    /**
     * Converts object value into a string or returns null if conversion is not
     * possible.
     *
     * @return string value or null if conversion is not supported.
     * @see StringConverter#toNullableString(Object)
     */
    @JsonIgnore
    public String getAsNullableString() {
        return StringConverter.toNullableString(_value);
    }

    /**
     * Converts object value into a string or returns "" if conversion is not
     * possible.
     *
     * @return string value or "" if conversion is not supported.
     * @see #getAsStringWithDefault(String)
     */
    @JsonIgnore
    public String getAsString() {
        return getAsStringWithDefault(null);
    }

    /**
     * Converts object value into a string or returns default value if conversion is
     * not possible.
     *
     * @param defaultValue the default value.
     * @return string value or default if conversion is not supported.
     * @see StringConverter#toStringWithDefault(Object, String)
     */
    @JsonIgnore
    public String getAsStringWithDefault(String defaultValue) {
        return StringConverter.toStringWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into a boolean or returns null if conversion is not
     * possible.
     *
     * @return boolean value or null if conversion is not supported.
     * @see BooleanConverter#toNullableBoolean(Object)
     */
    @JsonIgnore
    public Boolean getAsNullableBoolean() {
        return BooleanConverter.toNullableBoolean(_value);
    }

    /**
     * Converts object value into a boolean or returns false if conversion is not
     * possible.
     *
     * @return string value or false if conversion is not supported.
     * @see #getAsBooleanWithDefault(boolean)
     */
    @JsonIgnore
    public Boolean getAsBoolean() {
        return getAsBooleanWithDefault(false);
    }

    /**
     * Converts object value into a boolean or returns default value if conversion
     * is not possible.
     *
     * @param defaultValue the default value.
     * @return boolean value or default if conversion is not supported.
     * @see BooleanConverter#toBooleanWithDefault(Object, boolean)
     */
    @JsonIgnore
    public boolean getAsBooleanWithDefault(boolean defaultValue) {
        return BooleanConverter.toBooleanWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into an integer or returns null if conversion is not
     * possible.
     *
     * @return integer value or null if conversion is not supported.
     * @see IntegerConverter#toNullableInteger(Object)
     */
    @JsonIgnore
    public Integer getAsNullableInteger() {
        return IntegerConverter.toNullableInteger(_value);
    }

    /**
     * Converts object value into an integer or returns 0 if conversion is not
     * possible.
     *
     * @return integer value or 0 if conversion is not supported.
     * @see #getAsIntegerWithDefault(int)
     */
    @JsonIgnore
    public int getAsInteger() {
        return getAsIntegerWithDefault(0);
    }

    /**
     * Converts object value into a integer or returns default value if conversion
     * is not possible.
     *
     * @param defaultValue the default value.
     * @return integer value or default if conversion is not supported.
     * @see IntegerConverter#toIntegerWithDefault(Object, int)
     */
    @JsonIgnore
    public int getAsIntegerWithDefault(int defaultValue) {
        return IntegerConverter.toIntegerWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into a long or returns null if conversion is not
     * possible.
     *
     * @return long value or null if conversion is not supported.
     * @see LongConverter#toNullableLong(Object)
     */
    @JsonIgnore
    public Long getAsNullableLong() {
        return LongConverter.toNullableLong(_value);
    }

    /**
     * Converts object value into a long or returns 0 if conversion is not possible.
     *
     * @return string value or 0 if conversion is not supported.
     * @see #getAsLongWithDefault(long)
     */
    @JsonIgnore
    public long getAsLong() {
        return getAsLongWithDefault(0);
    }

    /**
     * Converts object value into a long or returns default value if conversion is
     * not possible.
     *
     * @param defaultValue the default value.
     * @return long value or default if conversion is not supported.
     * @see LongConverter#toLongWithDefault(Object, long)
     */
    @JsonIgnore
    public long getAsLongWithDefault(long defaultValue) {
        return LongConverter.toLongWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into a float or returns null if conversion is not
     * possible.
     *
     * @return float value or null if conversion is not supported.
     * @see FloatConverter#toNullableFloat(Object)
     */
    @JsonIgnore
    public Float getAsNullableFloat() {
        return FloatConverter.toNullableFloat(_value);
    }

    /**
     * Converts object value into a float or returns 0 if conversion is not
     * possible.
     *
     * @return float value or 0 if conversion is not supported.
     * @see #getAsFloatWithDefault(float)
     */
    @JsonIgnore
    public float getAsFloat() {
        return getAsFloatWithDefault(0);
    }

    /**
     * Converts object value into a float or returns default value if conversion is
     * not possible.
     *
     * @param defaultValue the default value.
     * @return float value or default if conversion is not supported.
     * @see FloatConverter#toFloatWithDefault(Object, float)
     */
    @JsonIgnore
    public float getAsFloatWithDefault(float defaultValue) {
        return FloatConverter.toFloatWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into a double or returns null if conversion is not
     * possible.
     *
     * @return double value or null if conversion is not supported.
     * @see DoubleConverter#toNullableDouble(Object)
     */
    @JsonIgnore
    public Double getAsNullableDouble() {
        return DoubleConverter.toNullableDouble(_value);
    }

    /**
     * Converts object value into a double or returns 0 if conversion is not
     * possible.
     *
     * @return double value or 0 if conversion is not supported.
     * @see #getAsDoubleWithDefault(double)
     */
    @JsonIgnore
    public double getAsDouble() {
        return getAsDoubleWithDefault(0);
    }

    /**
     * Converts object value into a double or returns default value if conversion is
     * not possible.
     *
     * @param defaultValue the default value.
     * @return double value or default if conversion is not supported.
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     */
    @JsonIgnore
    public double getAsDoubleWithDefault(double defaultValue) {
        return DoubleConverter.toDoubleWithDefault(_value, defaultValue);
    }

    /**
     * Converts object value into a Date or returns null if conversion is not
     * possible.
     *
     * @return ZonedDateTime value or null if conversion is not supported.
     * @see DateTimeConverter#toNullableDateTime(Object)
     */
    @JsonIgnore
    public ZonedDateTime getAsNullableDateTime() {
        return DateTimeConverter.toNullableDateTime(_value);
    }

    /**
     * Converts object value into a Date or returns current date if conversion is
     * not possible.
     *
     * @return ZonedDateTime value or current date if conversion is not supported.
     * @see #getAsDateTimeWithDefault(ZonedDateTime)
     */
    @JsonIgnore
    public ZonedDateTime getAsDateTime() {
        return getAsDateTimeWithDefault(null);
    }

    /**
     * Converts object value into a Date or returns default value if conversion is
     * not possible.
     *
     * @param defaultValue the default value.
     * @return ZonedDateTime value or default if conversion is not supported.
     * @see DateTimeConverter#toDateTimeWithDefault(Object, ZonedDateTime)
     */
    @JsonIgnore
    public ZonedDateTime getAsDateTimeWithDefault(ZonedDateTime defaultValue) {
        return DateTimeConverter.toDateTimeWithDefault(_value, defaultValue);
    }

    @JsonIgnore
    public Duration getAsNullableDuration() {
        return DurationConverter.toNullableDuration(_value);
    }

    @JsonIgnore
    public Duration getAsDuration() {
        return getAsDurationWithDefault(null);
    }

    @JsonIgnore
    public Duration getAsDurationWithDefault(Duration defaultValue) {
        return DurationConverter.toDurationWithDefault(_value, defaultValue);
    }

    @JsonIgnore
    public <T extends Enum<T>> T getAsNullableEnum(Class<T> type) {
        return EnumConverter.toNullableEnum(type, _value);
    }

    @JsonIgnore
    public <T extends Enum<T>> T getAsEnum(Class<T> type) {
        return getAsEnumWithDefault(type, null);
    }

    @JsonIgnore
    public <T extends Enum<T>> T getAsEnumWithDefault(Class<T> type, T defaultValue) {
        return EnumConverter.toEnumWithDefault(type, _value, defaultValue);
    }

    /**
     * Converts object value into a value defined by specied typecode. If conversion
     * is not possible it returns null.
     *
     * @param type the Class type that defined the type of the result
     * @return value defined by the typecode or null if conversion is not supported.
     * @see TypeConverter#toNullableType(Class, Object)
     */
    @JsonIgnore
    public <T> T getAsNullableType(Class<T> type) {
        return TypeConverter.toNullableType(type, _value);
    }

    /**
     * Converts object value into a value defined by specied typecode. If conversion
     * is not possible it returns default value for the specified type.
     *
     * @param type the Class type that defined the type of the result
     * @return value defined by the typecode or type default value if conversion is
     * not supported.
     * @see #getAsTypeWithDefault(Class, Object)
     */
    @JsonIgnore
    public <T> T getAsType(Class<T> type) {
        return getAsTypeWithDefault(type, null);
    }

    /**
     * Converts object value into a value defined by specied typecode. If conversion
     * is not possible it returns default value.
     *
     * @param type         the Class type that defined the type of the result
     * @param defaultValue the default value
     * @return value defined by the typecode or type default value if conversion is
     * not supported.
     * @see TypeConverter#toTypeWithDefault(Class, Object, Object)
     */
    @JsonIgnore
    public <T> T getAsTypeWithDefault(Class<T> type, T defaultValue) {
        return TypeConverter.toTypeWithDefault(type, _value, defaultValue);
    }

    /**
     * Converts object value into an AnyArray or returns empty AnyArray if
     * conversion is not possible.
     *
     * @return AnyArray value or empty AnyArray if conversion is not supported.
     * @see AnyValueArray#fromValue(Object)
     */
    @JsonIgnore
    public AnyValueArray getAsArray() {
        return AnyValueArray.fromValue(_value);
    }

    /**
     * Converts object value into AnyMap or returns empty AnyMap if conversion is
     * not possible.
     *
     * @return AnyMap value or empty AnyMap if conversion is not supported.
     * @see AnyValueMap#fromValue(Object)
     */
    @JsonIgnore
    public AnyValueMap getAsMap() {
        return AnyValueMap.fromValue(_value);
    }

    /**
     * Compares this object value to specified specified value. When direct
     * comparison gives negative results it tries to compare values as strings.
     *
     * @param obj the value to be compared with.
     * @return true when objects are equal and false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null && _value == null)
            return true;
        if (obj == null || _value == null)
            return false;

        if (obj instanceof AnyValue)
            obj = ((AnyValue) obj)._value;

        if (_value == null && obj == null)
            return true;
        if (_value == null || obj == null)
            return false;

//        String strThisValue = StringConverter.toString(_value);
//        String strValue = StringConverter.toString(obj);
//
//        if (strThisValue == null && strValue == null) return true;
//        if (strThisValue == null || strValue == null) return false;
//        return strThisValue.equals(strValue);
        return _value.equals(obj);
    }

    /**
     * Compares this object value to specified specified value. When direct
     * comparison gives negative results it converts values to type specified by
     * type code and compare them again.
     *
     * @param type the Class type that defined the type of the result
     * @param obj  the value to be compared with.
     * @return true when objects are equal and false otherwise.
     * @see TypeConverter#toType(Class, Object)
     */
    public <T> boolean equalsAsType(Class<T> type, Object obj) {
        if (obj == null && _value == null)
            return true;
        if (obj == null || _value == null)
            return false;

        if (obj instanceof AnyValue)
            obj = ((AnyValue) obj)._value;

        T typedThisValue = TypeConverter.toType(type, _value);
        T typedValue = TypeConverter.toType(type, obj);

        if (typedThisValue == null && typedValue == null)
            return true;
        if (typedThisValue == null || typedValue == null)
            return false;
        return typedThisValue.equals(typedValue);
    }

    /**
     * Creates a binary clone of this object.
     *
     * @return a clone of this object.
     */
    public Object clone() {
        return new AnyValue(_value);
    }

    /**
     * Gets a string representation of the object.
     *
     * @return a string representation of the object.
     * @see StringConverter#toString()
     */
    @Override
    public String toString() {
        return StringConverter.toString(_value);
    }

    /**
     * Gets an object hash code which can be used to optimize storing and searching.
     *
     * @return an object hash code.
     */
    @Override
    public int hashCode() {
        return _value != null ? _value.hashCode() : 0;
    }
}
