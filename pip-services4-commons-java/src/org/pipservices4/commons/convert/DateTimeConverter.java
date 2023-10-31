package org.pipservices4.commons.convert;

import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Converts arbitrary values into Date values.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Strings: converted using ISO time format
 * <li>Numbers: converted using milliseconds since unix epoch
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ZonedDateTime value1 = DateTimeConverter.toNullableDateTime("ABC"); // Result: null
 * ZonedDateTime value2 = DateTimeConverter.toNullableDateTime("2018-01-01T11:30:00.0"); // Result: ZonedDateTime(2018,0,1,11,30)
 * ZonedDateTime value3 = DateTimeConverter.toNullableDateTime(123); // Result: ZonedDateTime(123)
 * }
 * </pre>
 */
public class DateTimeConverter {
    private static final DateTimeFormatter simpleDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter simpleDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static ZonedDateTime millisToDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Converts value into Date or returns null when conversion is not possible.
     *
     * @param value the value to convert.
     * @return Date value or null when conversion is not supported.
     */
    public static ZonedDateTime toNullableDateTime(Object value) {
        if (value == null)
            return null;
        if (value instanceof ZonedDateTime)
            return (ZonedDateTime) value;

        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        }
        if (value instanceof Date)
            return ZonedDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());

        if (value instanceof LocalDate)
            return ZonedDateTime.of((LocalDate) value, LocalTime.of(0, 0), ZoneId.systemDefault());
        if (value instanceof LocalDateTime)
            return ZonedDateTime.of((LocalDateTime) value, ZoneId.systemDefault());

        if (value instanceof Integer)
            return millisToDateTime((int) value);
        if (value instanceof Short)
            return millisToDateTime((short) value);
        if (value instanceof Long)
            return millisToDateTime((long) value);
        if (value instanceof Float)
            return millisToDateTime((long) ((float) value));
        if (value instanceof Double)
            return millisToDateTime((long) ((double) value));
        if (value instanceof Duration)
            return millisToDateTime(((Duration) value).toMillis());

        if (value instanceof String) {
            try {
                return ZonedDateTime.parse((String) value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeParseException ignored) {
            }

            try {
                return ZonedDateTime.of(LocalDateTime.parse((String) value, simpleDateTimeFormatter),
                        ZoneId.systemDefault());
            } catch (DateTimeParseException ignored) {
            }

            try {
                return ZonedDateTime.of(LocalDate.parse((String) value, simpleDateFormatter), LocalTime.of(0, 0),
                        ZoneId.systemDefault());
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    /**
     * Converts value into Date or returns current date when conversion is not
     * possible.
     *
     * @param value the value to convert.
     * @return Date value or current date when conversion is not supported.
     * @see DateTimeConverter#toDateTimeWithDefault(Object, ZonedDateTime)
     */
    public static ZonedDateTime toDateTime(Object value) {
        return toDateTimeWithDefault(value, null);
    }

    /**
     * Converts value into Date or returns default when conversion is not possible.
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return Date value or default when conversion is not supported.
     * @see DateTimeConverter#toNullableDateTime(Object)
     */
    public static ZonedDateTime toDateTimeWithDefault(Object value, ZonedDateTime defaultValue) {
        ZonedDateTime result = toNullableDateTime(value);
        return result != null ? result : defaultValue;
    }

}
