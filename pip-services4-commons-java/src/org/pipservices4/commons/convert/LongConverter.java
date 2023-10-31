package org.pipservices4.commons.convert;

import java.time.Duration;
import java.util.*;

/**
 * Converts arbitrary values into longs.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Strings are converted to floats, then to longs
 * <li>DateTime: total number of milliseconds since unix epoсh
 * <li>Boolean: 1 for true and 0 for false
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * long value1 = LongConverter.toNullableLong("ABC"); // Result: null
 * long value2 = LongConverter.toNullableLong("123.456"); // Result: 123
 * long value3 = LongConverter.toNullableLong(true); // Result: 1
 * long value4 = LongConverter.toNullableLong(new Date()); // Result: current milliseconds
 * }
 * </pre>
 */
public class LongConverter {

    /**
     * Converts value into long or returns null when conversion is not possible.
     *
     * @param value the value to convert.
     * @return long value or null when conversion is not supported.
     */
    public static Long toNullableLong(Object value) {
        if (value == null)
            return null;

        if (value instanceof Date)
            return ((Date) value).getTime();
        if (value instanceof Calendar)
            return ((Calendar) value).getTimeInMillis();
        if (value instanceof Duration)
            return ((Duration) value).toMillis();

        if (value instanceof Boolean)
            return (boolean) value ? 1L : 0L;

        if (value instanceof Integer)
            return (long) ((int) value);
        if (value instanceof Short)
            return (long) ((short) value);
        if (value instanceof Long)
            return (long) value;
        if (value instanceof Float)
            return (long) Math.round((float) value);
        if (value instanceof Double)
            return Math.round((double) value);

        if (value instanceof String)
            try {
                return Math.round(Double.parseDouble((String) value));
            } catch (NumberFormatException ex) {
                return null;
            }

        return null;
    }

    /**
     * Converts value into long or returns 0 when conversion is not possible.
     *
     * @param value the value to convert.
     * @return long value or 0 when conversion is not supported.
     * @see LongConverter#toLongWithDefault(Object, long)
     */
    public static long toLong(Object value) {
        return toLongWithDefault(value, 0);
    }

    /**
     * Converts value into integer or returns default when conversion is not
     * possible.
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return long value or default when conversion is not supported
     * @see LongConverter#toNullableLong(Object)
     */
    public static long toLongWithDefault(Object value, long defaultValue) {
        Long result = toNullableLong(value);
        return result != null ? result : defaultValue;
    }

}
