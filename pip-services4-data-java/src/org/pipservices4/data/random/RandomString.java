package org.pipservices4.data.random;

/**
 * Random generator for string values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * char value1 = RandomString.pickChar("ABC");     // Possible result: "C"
 * String value2 = RandomString.pick(new String {"A","B","C"}); // Possible result: "B"
 * }
 * </pre>
 */
public class RandomString {
    private static final String _digits = "01234956789";
    private static final String _symbols = "_,.:-/.[].{},#-!,$=%.+^.&*-() ";
    private static final String _alphaLower = "abcdefghijklmnopqrstuvwxyz";
    private static final String _alphaUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String _alpha = _alphaUpper + _alphaLower;
    private static final String _chars = _alpha + _digits + _symbols;

    /**
     * Picks a random character from a string.
     *
     * @param values a string to pick a char from
     * @return a randomly picked char.
     */
    public static char pickChar(String values) {
        if (values == null || values.isEmpty())
            return '\0';

        int index = RandomInteger.nextInteger(values.length());
        return values.charAt(index);
    }

    /**
     * Picks a random string from an array of string.
     *
     * @param values strings to pick from.
     * @return a randomly picked string.
     */
    public static String pick(String[] values) {
        if (values == null || values.length == 0)
            return "";

        int index = RandomInteger.nextInteger(values.length);
        return values[index];
    }

    /**
     * Distorts a string by randomly replacing characters in it.
     *
     * @param value a string to distort.
     * @return a distored string.
     */
    public static String distort(String value) {
        value = value.toLowerCase();

        if (RandomBoolean.chance(1, 5))
            value = value.substring(0, 1).toUpperCase() + value.substring(1);

        if (RandomBoolean.chance(1, 3))
            value = value + pickChar(_symbols);

        return value;
    }

    /**
     * Generates random alpha characted [A-Za-z]
     *
     * @return a random characted.
     */
    public static char nextAlphaChar() {
        int index = RandomInteger.nextInteger(_alpha.length());
        return _alpha.charAt(index);
    }

    /**
     * Generates a random string, consisting of upper and lower case letters (of the
     * English alphabet), digits (0-9), and symbols.
     *
     * @param min (optional) minimum string length.
     * @param max maximum string length.
     * @return a random string.
     */
    public static String nextString(int min, int max) {
        StringBuilder result = new StringBuilder();

        int length = RandomInteger.nextInteger(min, max);
        for (int i = 0; i < length; i++) {
            int index = RandomInteger.nextInteger(_chars.length());
            result.append(_chars.charAt(index));
        }

        return result.toString();
    }
}
