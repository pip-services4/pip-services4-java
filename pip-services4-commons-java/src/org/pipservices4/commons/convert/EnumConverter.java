package org.pipservices4.commons.convert;

public class EnumConverter {

    public static <T extends Enum<T>> T toNullableEnum(Class<T> type, Object value) {
        Integer intValue = IntegerConverter.toNullableInteger(value);
        String strValue = StringConverter.toNullableString(value);

        if (intValue == null && strValue == null)
            return null;

        for (T e : type.getEnumConstants()) {
            if (intValue != null && e.ordinal() == intValue)
                return e;
            if (e.name().equalsIgnoreCase(strValue))
                return e;
        }

        return null;
    }

    public static <T extends Enum<T>> T toEnum(Class<T> type, Object value) {
        T[] elements = type.getEnumConstants();
        T defaultElement = elements != null && elements.length > 0 ? elements[0] : null;
        return toEnumWithDefault(type, value, defaultElement);
    }

    public static <T extends Enum<T>> T toEnumWithDefault(Class<T> type, Object value, T defaultValue) {
        T result = toNullableEnum(type, value);
        return result != null ? result : defaultValue;
    }

}
