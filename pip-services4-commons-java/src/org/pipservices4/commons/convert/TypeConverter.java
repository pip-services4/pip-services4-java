package org.pipservices4.commons.convert;

import java.time.*;
import java.util.*;

/**
 * Converts arbitrary values into objects specific by TypeCodes.
 * For each {@link TypeCode} this class calls corresponding converter which applies
 * extended conversion rules to convert the values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * int value1 = TypeConverter.toType(TypeCode.Integer, "123.456"); // Result: 123
 * ZonedDateTime value2 = TypeConverter.toType(TypeCode.DateTime, 123); // Result: ZonedDateTime(123)
 * boolean value3 = TypeConverter.toType(TypeCode.Boolean, "F"); // Result: false
 * }
 * </pre>
 *
 * @see TypeCode
 */
public class TypeConverter {
    private static final Class<?> _booleanType = Boolean.class;
    private static final Class<?> _integerType = Integer.class;
    private static final Class<?> _longType = Long.class;
    private static final Class<?> _stringType = String.class;
    private static final Class<?> _floatType = Float.class;
    private static final Class<?> _doubleType = Double.class;
    private static final Class<?> _dateTimeType = ZonedDateTime.class;
    private static final Class<?> _durationType = Duration.class;
    private static final Class<?> _enumType = Enum.class;
    private static final Class<?> _listType = List.class;
    private static final Class<?> _mapType = Map.class;

    /**
     * Gets TypeCode for specific type.
     *
     * @param type the Class type for the data type.
     * @return the TypeCode that corresponds to the passed object's type.
     */
    public static TypeCode toTypeCode(Class<?> type) {
        if (type == null)
            return TypeCode.Unknown;
        else if (type.isArray())
            return TypeCode.Array;
        else if (type.isEnum())
            return TypeCode.Enum;
        else if (type.isPrimitive()) {
            if (_booleanType.isAssignableFrom(type))
                return TypeCode.Boolean;
            if (_doubleType.isAssignableFrom(type))
                return TypeCode.Double;
            if (_floatType.isAssignableFrom(type))
                return TypeCode.Float;
            if (_longType.isAssignableFrom(type))
                return TypeCode.Long;
            if (_integerType.isAssignableFrom(type))
                return TypeCode.Integer;
        } else {
            if (_booleanType.isAssignableFrom(type))
                return TypeCode.Boolean;
            if (_doubleType.isAssignableFrom(type))
                return TypeCode.Double;
            if (_floatType.isAssignableFrom(type))
                return TypeCode.Float;
            if (_longType.isAssignableFrom(type))
                return TypeCode.Long;
            if (_integerType.isAssignableFrom(type))
                return TypeCode.Integer;
            if (_stringType.isAssignableFrom(type))
                return TypeCode.String;
            if (_dateTimeType.isAssignableFrom(type))
                return TypeCode.DateTime;
            if (_durationType.isAssignableFrom(type))
                return TypeCode.Duration;
            if (_mapType.isAssignableFrom(type))
                return TypeCode.Map;
            if (_listType.isAssignableFrom(type))
                return TypeCode.Array;
            if (_enumType.isAssignableFrom(type))
                return TypeCode.Enum;
        }

        return TypeCode.Object;
    }

    /**
     * Gets TypeCode for specific value.
     *
     * @param value value whose TypeCode is to be resolved.
     * @return the TypeCode that corresponds to the passed object's type.
     */
    public static TypeCode toTypeCode(Object value) {
        if (value == null)
            return TypeCode.Unknown;

        return toTypeCode(value instanceof Class<?> ? (Class<?>)value : value.getClass());
    }

    /**
     * Converts value into an object type specified by Type Code or returns null
     * when conversion is not possible.
     *
     * @param type  the Class type for the data type.
     * @param value the value to convert.
     * @return object value of type corresponding to TypeCode, or null when
     * conversion is not supported.
     * @see TypeConverter#toTypeCode(Class)
     */
    @SuppressWarnings("unchecked")
    public static <T> T toNullableType(Class<T> type, Object value) {
        TypeCode resultType = toTypeCode(type);

        if (value == null)
            return null;
        if (type.isInstance(value))
            return (T) value;

        // Convert to known types
        if (resultType == TypeCode.String)
            return type.cast(StringConverter.toNullableString(value));
        else if (resultType == TypeCode.Integer)
            return type.cast(IntegerConverter.toNullableInteger(value));
        else if (resultType == TypeCode.Long)
            return type.cast(LongConverter.toNullableLong(value));
        else if (resultType == TypeCode.Float)
            return type.cast(FloatConverter.toNullableFloat(value));
        else if (resultType == TypeCode.Double)
            return type.cast(DoubleConverter.toNullableDouble(value));
        else if (resultType == TypeCode.Duration)
            return type.cast(DurationConverter.toNullableDuration(value));
        else if (resultType == TypeCode.DateTime)
            return type.cast(DateTimeConverter.toNullableDateTime(value));
        else if (resultType == TypeCode.Array)
            return type.cast(ArrayConverter.toNullableArray(value));
        else if (resultType == TypeCode.Map)
            return type.cast(MapConverter.toNullableMap(value));

        // Convert to unknown type
        try {
            return type.cast(value);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Converts value into an object type specified by Type Code or returns type
     * default when conversion is not possible.
     *
     * @param type  the Class type for the data type into which 'value' is to be converted.
     * @param value the value to convert.
     * @return object value of type corresponding to TypeCode, or type default when
     * conversion is not supported.
     * @see TypeConverter#toNullableType(Class, Object)
     * @see TypeConverter#toTypeCode(Class)
     */
    public static <T> T toType(Class<T> type, Object value) {
        T result = toNullableType(type, value);
        if (result != null)
            return result;

        TypeCode resultType = toTypeCode(type);
        if (resultType == TypeCode.String)
            return null;
        else if (resultType == TypeCode.Integer)
            return type.cast((int) 0);
        else if (resultType == TypeCode.Long)
            return type.cast((long) 0);
        else if (resultType == TypeCode.Float)
            return type.cast((float) 0);
        else if (resultType == TypeCode.Double)
            return type.cast((double) 0);
        else
            return null;
    }

    /**
     * Converts value into an object type specified by Type Code or returns default
     * value when conversion is not possible.
     *
     * @param type         the Class type for the data type into which 'value' is to be converted.
     * @param value        the value to convert.
     * @param defaultValue the default value to return if conversion is not possible
     *                     (returns null).
     * @return object value of type corresponding to TypeCode, or default value when
     * conversion is not supported.
     * @see TypeConverter#toNullableType(Class, Object)
     * @see TypeConverter#toTypeCode(Class)
     */
    public static <T> T toTypeWithDefault(Class<T> type, Object value, T defaultValue) {
        T result = toNullableType(type, value);
        return result != null ? result : defaultValue;
    }

    /**
     * Converts a TypeCode into its string name.
     *
     * @param type the TypeCode to convert into a string.
     * @return the name of the TypeCode passed as a string value.
     */
    public static String toString(TypeCode type) {
        switch (type) {
            case String:
                return "string";
            case Boolean:
                return "boolean";
            case Integer:
                return "integer";
            case Long:
                return "long";
            case Float:
                return "float";
            case Double:
                return "double";
            case DateTime:
                return "datetime";
            case Duration:
                return "duration";
            case Object:
                return "object";
            case Enum:
                return "enum";
            case Array:
                return "array";
            case Map:
                return "map";
            default:
                return "unknown";
        }
    }
}
