package org.pipservices4.data.random;

import java.util.*;

/**
 * Random generator for array objects.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * int value1 = RandomArray.pick(new int []{1, 2, 3, 4}); // Possible result: 3
 * }
 * </pre>
 */
public class RandomArray {
    /**
     * Picks a random element from specified array.
     *
     * @param values an array of any type
     * @return a randomly picked item.
     */
    public static <T> T pick(T[] values) {
        if (values == null || values.length == 0)
            return null;

        return values[RandomInteger.nextInteger(values.length)];
    }

    /**
     * Picks a random element from specified array.
     *
     * @param values an list of any type
     * @return a randomly picked item.
     */
    public static <T> T pick(List<T> values) {
        if (values == null || values.isEmpty())
            return null;

        int index = RandomInteger.nextInteger(values.size());
        return values.get(index);
    }
}
