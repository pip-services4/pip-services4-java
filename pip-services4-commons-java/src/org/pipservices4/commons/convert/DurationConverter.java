package org.pipservices4.commons.convert;

import java.time.*;

public class DurationConverter {

    public static Duration toNullableDuration(Object value) {
        // Shortcuts
        if (value == null) return null;
        if (value instanceof Duration) return (Duration) value;

        // Number fields
        if (value instanceof Integer) return Duration.ofMillis((int) value);
        if (value instanceof Short) return Duration.ofMillis((short) value);
        if (value instanceof Long) return Duration.ofMillis((long) value);
        if (value instanceof Float) return Duration.ofMillis((long) ((float) value));
        if (value instanceof Double) return Duration.ofMillis((long) ((double) value));

        Long millis = LongConverter.toNullableLong(value);
        return millis != null ? Duration.ofMillis(millis) : null;
    }

    public static Duration toDuration(Object value) {
        return toDurationWithDefault(value, null);
    }

    public static Duration toDurationWithDefault(Object value, Duration defaultValue) {
        Duration result = toNullableDuration(value);
        return result != null ? result : defaultValue;
    }

}
