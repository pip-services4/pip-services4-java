package org.pipservices4.commons.convert;

import java.time.Duration;
import java.util.*;

/**
 * Converts arbitrary values into double.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Strings are converted to double values
 * <li>DateTime: total number of milliseconds since unix epoсh
 * <li>Boolean: 1 for true and 0 for false
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * double value1 = DoubleConverter.toNullableDouble("ABC"); // Result: null
 * double value2 = DoubleConverter.toNullableDouble("123.456"); // Result: 123.456
 * double value3 = DoubleConverter.toNullableDouble(true); // Result: 1
 * double value4 = DoubleConverter.toNullableDouble(new Date()); // Result: current milliseconds
 * }
 * </pre>
 */
public class DoubleConverter {
    /**
     * Converts value into doubles or returns null when conversion is not possible.
     *
     * @param value the value to convert.
     * @return double value or null when conversion is not supported.
     */
    public static Double toNullableDouble(Object value) {
        if (value == null)
            return null;

        if (value instanceof Date)
            return (double) ((Date) value).getTime();
        if (value instanceof Calendar)
            return (double) ((Calendar) value).getTimeInMillis();
        if (value instanceof Duration)
            return (double) ((Duration) value).toMillis();

        if (value instanceof Boolean)
            return (boolean) value ? 1.0 : 0.0;

        if (value instanceof Integer)
            return (double) ((int) value);
        if (value instanceof Short)
            return (double) ((short) value);
        if (value instanceof Long)
            return (double) ((long) value);
        if (value instanceof Float)
            return (double) ((float) value);
        if (value instanceof Double)
            return (double) value;

        if (value instanceof String)
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException ex) {
                return null;
            }

        return null;
    }

    /**
     * Converts value into doubles or returns 0 when conversion is not possible.
     *
     * @param value the value to convert.
     * @return double value or 0 when conversion is not supported.
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     */
    public static double toDouble(Object value) {
        return toDoubleWithDefault(value, 0);
    }

    /**
     * Converts value into doubles or returns default value when conversion is not
     * possible.
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return double value or default when conversion is not supported.
     * @see DoubleConverter#toNullableDouble(Object)
     */
    public static double toDoubleWithDefault(Object value, double defaultValue) {
        Double result = toNullableDouble(value);
        return result != null ? result : defaultValue;
    }

}
