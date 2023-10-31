package org.pipservices4.commons.convert;

/**
 * Converts arbitrary values into float.
 * Converts using extended conversion rules:
 * <ul>
 * <li>Strings are converted to float values
 * <li>DateTime: total number of milliseconds since unix epoсh
 * <li>Boolean: 1 for true and 0 for false
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * float value1 = FloatConverter.toNullableFloat("ABC"); // Result: null
 * float value2 = FloatConverter.toNullableFloat("123.456"); // Result: 123.456
 * float value3 = FloatConverter.toNullableFloat(true); // Result: 1
 * float value4 = FloatConverter.toNullableFloat(new Date()); // Result: current milliseconds
 * }
 * </pre>
 */
public class FloatConverter {

    /**
     * Converts value into float or returns null when conversion is not possible.
     *
     * @param value the value to convert.
     * @return float value or null when conversion is not supported.
     * @see DoubleConverter#toNullableDouble(Object)
     */
    public static Float toNullableFloat(Object value) {
        Double result = DoubleConverter.toNullableDouble(value);
        return result != null ? (float) ((double) result) : null;
    }

    /**
     * Converts value into float or returns 0 when conversion is not possible.
     *
     * @param value the value to convert.
     * @return float value or 0 when conversion is not supported.
     * @see DoubleConverter#toDouble(Object)
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     */
    public static float toFloat(Object value) {
        return toFloatWithDefault(value, 0);
    }

    /**
     * Converts value into float or returns default when conversion is not possible.
     *
     * @param value        the value to convert.
     * @param defaultValue the default value.
     * @return float value or default value when conversion is not supported.
     * @see DoubleConverter#toDoubleWithDefault(Object, double)
     * @see FloatConverter#toNullableFloat(Object)
     */
    public static float toFloatWithDefault(Object value, float defaultValue) {
        Float result = toNullableFloat(value);
        return result != null ? result : defaultValue;
    }

}
