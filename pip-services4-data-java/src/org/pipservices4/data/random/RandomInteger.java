package org.pipservices4.data.random;

import java.util.*;

/**
 * Random generator for integer values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * int value1 = RandomInteger.nextInteger(5, 10);     // Possible result: 7
 * int value2 = RandomInteger.nextInteger(10);        // Possible result: 3
 * int value3 = RandomInteger.updateInteger(10, 3);   // Possible result: 9
 * }
 * </pre>
 */
public class RandomInteger {
    private static final java.util.Random _random = new java.util.Random();

    /**
     * Generates a integer in the range [0, 'max'].
     *
     * @param max (optional) maximum value of the integer that will be generated.
     *            Defaults to 'min' if omitted.
     * @return generated random integer value.
     */
    public static int nextInteger(int max) {
        return _random.nextInt(max);
    }

    /**
     * Generates a integer in the range ['min', 'max']. If 'max' is omitted, then
     * the range will be set to [0, 'min'].
     *
     * @param min minimum value of the integer that will be generated. If 'max' is
     *            omitted, then 'max' is set to 'min' and 'min' is set to 0.
     * @param max (optional) maximum value of the integer that will be generated.
     *            Defaults to 'min' if omitted.
     * @return generated random integer value.
     */
    public static int nextInteger(int min, int max) {
        if (max - min <= 0)
            return min;

        return min + _random.nextInt(max - min);
    }

    /**
     * Updates (drifts) a integer value without specified range defined
     *
     * @param value a integer value to drift.
     * @return updated random integer value.
     */
    public static int updateInteger(int value) {
        return updateInteger(value, 0);
    }

    /**
     * Updates (drifts) a integer value within specified range defined
     *
     * @param value a integer value to drift.
     * @param range (optional) a range. Default: 10% of the value
     * @return updated random integer value.
     */
    public static int updateInteger(int value, int range) {
        range = range == 0 ? (int) (0.1 * value) : range;
        int min = value - range;
        int max = value + range;
        return nextInteger(min, max);
    }

    /**
     * Generates a random sequence of integers starting from 0 like: [0,1,2,3...??]
     *
     * @param size size of sequence
     * @return generated array of integers.
     */
    public static List<Integer> sequence(int size) {
        return sequence(size, size);
    }

    /**
     * Generates a random sequence of integers starting from 0 like: [0,1,2,3...??]
     *
     * @param min minimum value of the integer that will be generated. If 'max' is
     *            omitted, then 'max' is set to 'min' and 'min' is set to 0.
     * @param max (optional) maximum value of the integer that will be generated.
     *            Defaults to 'min' if omitted.
     * @return generated array of integers.
     */
    public static List<Integer> sequence(int min, int max) {
        int count = nextInteger(min, max);

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < count; i++)
            result.add(i);

        return result;
    }
}
