package org.pipservices4.data.random;

/**
 * Random generator for double values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * double value1 = RandomDouble.nextDouble(5, 10);     // Possible result: 7.3
 * double value2 = RandomDouble.nextDouble(10);        // Possible result: 3.7
 * double value3 = RandomDouble.updateDouble(10, 3);   // Possible result: 9.2
 * }
 * </pre>
 */
public class RandomDouble {
    private static final java.util.Random _random = new java.util.Random();

    /**
     * Generates a random double value in the range to "max".
     *
     * @param max max range value
     * @return a random double value.
     */
    public static double nextDouble(double max) {
        return _random.nextDouble() * max;
    }

    /**
     * Generates a random double value in the range ["min", "max"].
     *
     * @param min (optional) minimum range value
     * @param max max range value
     * @return a random double value.
     */
    public static double nextDouble(double min, double max) {
        return min + _random.nextDouble() * (max - min);
    }

    /**
     * Updates (drifts) a double value without specified range defined
     *
     * @param value a double value to drift.
     * @return updated random double value.
     */
    public static double updateDouble(double value) {
        return updateDouble(value, 0);
    }

    /**
     * Updates (drifts) a double value within specified range defined
     *
     * @param value a double value to drift.
     * @param range (optional) a range. Default: 10% of the value
     * @return updated random double value.
     */
    public static double updateDouble(double value, double range) {
        range = range == 0 ? 0.1 * value : range;
        double min = value - range;
        double max = value + range;
        return nextDouble(min, max);
    }
}
