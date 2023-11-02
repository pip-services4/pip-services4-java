package org.pipservices4.expressions.tokenizers.utilities;

/**
 * Validates characters that are processed by Tokenizers.
 */
public class CharValidator {
    public static final int EOF = 0xffff;
    public static final int ZERO = "0".codePointAt(0);
    public static final int NINE = "9".codePointAt(0);

    public static boolean isEof(int value) {
        return value == CharValidator.EOF || value == -1;
    }

    public static boolean isEol(int value) {
        return value == 10 || value == 13;
    }

    public static boolean isDigit(int value) {
        return value >= CharValidator.ZERO && value <= CharValidator.NINE;
    }
}
