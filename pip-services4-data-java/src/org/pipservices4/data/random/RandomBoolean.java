package org.pipservices4.data.random;

/**
 * Random generator for boolean values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * boolean value1 = RandomBoolean.nextBoolean();    // Possible result: true
 * boolean value2 = RandomBoolean.chance(1,3);      // Possible result: false
 * }
 * </pre>
 */
public class RandomBoolean {
    private static final java.util.Random _random = new java.util.Random();

    /**
     * Calculates "chance" out of "max chances". Example: 1 chance out of 3 chances
     * (or 33.3%)
     *
     * @param chances    a chance proportional to maxChances.
     * @param maxChances a maximum number of chances
     * @return random boolean value.
     */
    public static boolean chance(float chances, float maxChances) {
        chances = chances >= 0 ? chances : 0;
        maxChances = maxChances >= 0 ? maxChances : 0;
        if (chances == 0 && maxChances == 0)
            return false;

        maxChances = Math.max(maxChances, chances);
        double start = (maxChances - chances) / 2;
        double end = start + chances;
        double hit = _random.nextDouble() * maxChances;
        return hit >= start && hit <= end;
    }

    /**
     * Generates a random boolean value.
     *
     * @return a random boolean.
     */
    public static boolean nextBoolean() {
        return _random.nextInt(100) < 50;
    }

}
