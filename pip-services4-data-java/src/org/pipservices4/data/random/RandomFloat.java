package org.pipservices4.data.random;

/**
 * Random generator for float values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * float value1 = RandomFloat.nextFloat(5, 10);     // Possible result: 7.3
 * float value2 = RandomFloat.nextFloat(10);        // Possible result: 3.7
 * float value3 = RandomFloat.updateFloat(10, 3);   // Possible result: 9.2
 * }
 * </pre>
 */
public class RandomFloat {
    private static final java.util.Random _random = new java.util.Random();

    /**
     * Generates a float in the range [0, 'max'].
     *
     * @param max (optional) maximum value of the float that will be generated.
     * @return generated random float value.
     */
    public static float nextFloat(int max) {
        return (float) _random.nextDouble() * max;
    }

    /**
     * Generates a float in the range ['min', 'max']. If 'max' is omitted, then the
     * range will be set to [0, 'min'].
     *
     * @param min minimum value of the float that will be generated. If 'max' is
     *            omitted, then 'max' is set to 'min' and 'min' is set to 0.
     * @param max (optional) maximum value of the float that will be generated.
     *            Defaults to 'min' if omitted.
     * @return generated random float value.
     */
    public static float nextFloat(float min, float max) {
        return (float) (min + _random.nextDouble() * (max - min));
    }

    /**
     * Updates (drifts) a float value without specified range defined
     *
     * @param value a float value to drift.
     * @return updated random float value.
     */
    public static float updateFloat(float value) {
        return updateFloat(value, 0);
    }

    /**
     * Updates (drifts) a float value within specified range defined
     *
     * @param value a float value to drift.
     * @param range (optional) a range. Default: 10% of the value
     * @return updated random float value.
     */
    public static float updateFloat(float value, float range) {
        range = range == 0 ? (float) (0.1 * value) : range;
        float min = value - range;
        float max = value + range;
        return nextFloat(min, max);
    }

}
