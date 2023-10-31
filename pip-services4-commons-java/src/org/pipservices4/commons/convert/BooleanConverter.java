package org.pipservices4.commons.convert;

import java.time.*;

/**
 * Converts arbitrary values to boolean values.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Numbers: above 0, less more 0 are true; equal to 0 are false
 * <li>Strings: "true", "yes", "T", "Y", "1" are true, "false", "no", "F", "N" are false
 * <li>DateTime: above 0, less more 0 total milliseconds are true, equal to 0 are false
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * boolean value1 = BooleanConverter.toNullableBoolean(true); // true
 * boolean value2 = BooleanConverter.toNullableBoolean("yes"); // true
 * boolean value3 = BooleanConverter.toNullableBoolean(1); // true
 * boolean value4 = BooleanConverter.toNullableBoolean({}); // null
 * }
 * </pre>
 */
public class BooleanConverter {

    /**
     * Converts value into boolean or returns null when conversion is not possible.
     *
     * @param value the value to convert.
     * @return boolean value or null when conversion is not supported.
     */
    public static Boolean toNullableBoolean(Object value) {
        if (value == null)
            return null;
        if (value instanceof Boolean)
            return (boolean) value;
        if (value instanceof Duration)
            return ((Duration) value).toMillis() > 0;

        String strValue = value.toString().toLowerCase();
        if (strValue.equals("1") || strValue.equals("true") || strValue.equals("t") || strValue.equals("yes")
                || strValue.equals("y"))
            return true;

        if (strValue.equals("0") || strValue.equals("false") || strValue.equals("f") || strValue.equals("no")
                || strValue.equals("n"))
            return false;

        return null;
    }

    /**
     * Converts value into boolean or returns false when conversion is not possible.
     *
     * @param value the value to convert.
     * @return boolean value or false when conversion is not supported.
     * @see BooleanConverter#toBooleanWithDefault(Object, boolean)
     */
    public static boolean toBoolean(Object value) {
        return toBooleanWithDefault(value, false);
    }

    /**
     * Converts value into boolean or returns default value when conversion is not
     * possible
     *
     * @param value        the value to convert.
     * @param defaultValue the default value
     * @return boolean value or default when conversion is not supported.
     * @see BooleanConverter#toNullableBoolean(Object)
     */
    public static boolean toBooleanWithDefault(Object value, boolean defaultValue) {
        Boolean result = toNullableBoolean(value);
        return result != null ? result : defaultValue;
    }

}
