package org.pipservices4.data.random;

import java.util.*;

/**
 * Random generator for long values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * long value1 = RandomLong.nextLong(5, 10);     // Possible result: 7
 * long value2 = RandomLong.nextLong(10);        // Possible result: 3
 * long value3 = RandomLong.nextLong(10, 3);   // Possible result: 9
 * }
 * </pre>
 */
public class RandomLong {

    private static final java.util.Random _random = new java.util.Random();

    /**
     * Generates a long in the range [0, 'max'].
     *
     * @param maxValue (optional) maximum value of the long that will be generated.
     *                 Defaults to 'min' if omitted.
     * @return generated random long value.
     */
    public static long nextLong(long maxValue) {
        return _random.nextInt((int) maxValue);
    }

    /**
     * Generates a long in the range ['min', 'max']. If 'max' is omitted, then
     * the range will be set to [0, 'min'].
     *
     * @param minValue minimum value of the long that will be generated. If 'max' is
     *                 omitted, then 'max' is set to 'min' and 'min' is set to 0.
     * @param maxValue (optional) maximum value of the long that will be generated.
     *                 Defaults to 'min' if omitted.
     * @return generated random long value.
     */
    public static long nextLong(long minValue, long maxValue) {
        if (maxValue - minValue <= 0)
            return minValue;

        return minValue + _random.nextInt((int) (maxValue - minValue));
    }

    /**
     * Updates (drifts) a long value without specified range defined
     *
     * @param value a long value to drift.
     * @return updated random long value.
     */
    public static long updateLong(long value) {
        return updateLong(value, 0);
    }

    /**
     * Updates (drifts) a long value within specified range defined
     *
     * @param value a long value to drift.
     * @param range (optional) a range. Default: 10% of the value
     * @return updated random long value.
     */
    public static long updateLong(long value, long range) {
        range = range == 0 ? (long) (0.1 * value) : range;
        long minValue = value - range;
        long maxValue = value + range;
        return nextLong(minValue, maxValue);
    }


    /**
     * Generates a random sequence of longs starting from 0 like: [0,1,2,3...??]
     *
     * @param size size of sequence.
     * @return generated array of longs.
     */
    public static List<Long> sequence(long size) {
        return sequence(size, size);
    }

    /**
     * Generates a random sequence of longs starting from 0 like: [0,1,2,3...??]
     *
     * @param min minimum value of the long that will be generated. If 'max' is
     *            omitted, then 'max' is set to 'min' and 'min' is set to 0.
     * @param max (optional) maximum value of the long that will be generated.
     *            Defaults to 'min' if omitted.
     * @return generated array of longs.
     */
    public static List<Long> sequence(long min, long max) {
        long count = nextLong(min, max);

        List<Long> result = new ArrayList<>();
        for (long i = 0; i < count; i++)
            result.add(i);

        return result;
    }
}
