package org.pipservices4.commons.convert;

import java.lang.reflect.Array;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Converts arbitrary values into strings.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Numbers: are converted with '.' as decimal point
 * <li>DateTime: using ISO format
 * <li>Boolean: "true" for true and "false" for false
 * <li>Arrays: as comma-separated list
 * <li>Other objects: using toString() method
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * String value1 = StringConverter.toString(123.456); // Result: "123.456"
 * String value2 = StringConverter.toString(true); // Result: "true"
 * String value3 = StringConverter.toString(ZonedDateTime.now()); // Result: "2018-01-01T00:00:00.00"
 * String value4 = StringConverter.toString(new int[]{1, 2, 3}); // Result: "1,2,3"
 * }
 * </pre>
 */
public class StringConverter {

    /**
     * Converts value into string or returns null when value is null.
     *
     * @param value the value to convert.
     * @return string value or null when value is null.
     */
    public static String toNullableString(Object value) {
        // Shortcuts
        if (value == null)
            return null;
        if (value instanceof String)
            return (String) value;

        // Legacy and new dates
        if (value instanceof Date)
            value = ZonedDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
        if (value instanceof Calendar) {
            value = ZonedDateTime.ofInstant(((Calendar) value).toInstant(),
                    ((Calendar) value).getTimeZone().toZoneId());
        }
        if (value instanceof Duration)
            value = ((Duration) value).toMillis();
        if (value instanceof Instant)
            value = ZonedDateTime.ofInstant((Instant) value, ZoneId.systemDefault());
        if (value instanceof LocalDateTime)
            value = ZonedDateTime.of((LocalDateTime) value, ZoneId.systemDefault());
        if (value instanceof LocalDate)
            value = ZonedDateTime.of((LocalDate) value, LocalTime.of(0, 0), ZoneId.systemDefault());
        if (value instanceof ZonedDateTime)
            return ((ZonedDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // Convert list
        if (value instanceof List<?>) {
            StringBuilder builder = new StringBuilder();
            for (Object element : (List<?>) value) {
                if (builder.length() > 0)
                    builder.append(",");
                builder.append(element);
            }
            return builder.toString();
        }

        // Convert array
        if (value.getClass().isArray()) {
            StringBuilder builder = new StringBuilder();
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++) {
                if (builder.length() > 0)
                    builder.append(",");
                builder.append(Array.get(value, index));
            }
            return builder.toString();
        }


        // Everything else
        return value.toString();
    }

    /**
     * Converts value into string or returns "" when value is null.
     *
     * @param value the value to convert.
     * @return string value or "" when value is null.
     * @see StringConverter#toStringWithDefault(Object, String)
     */
    public static String toString(Object value) {
        return toStringWithDefault(value, "");
    }

    /**
     * Converts value into string or returns default when value is null.
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return string value or default when value is null.
     * @see StringConverter#toNullableString(Object)
     */
    public static String toStringWithDefault(Object value, String defaultValue) {
        String result = toNullableString(value);
        return result != null ? result : defaultValue;
    }

}
