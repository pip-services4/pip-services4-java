package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.IQuoteState;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * A quoteState returns a quoted string token from a scanner. This state will collect characters
 * until it sees a match to the character that the tokenizer used to switch to this state.
 * For example, if a tokenizer uses a double-quote character to enter this state,
 * then <code>nextToken()</code> will search for another double-quote until it finds one
 * or finds the end of the scanner.
 */
public class GenericQuoteState implements IQuoteState {
    /**
     * Return a quoted string token from a scanner. This method will collect
     * characters until it sees a match to the character that the tokenizer used
     * to switch to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) {
        var firstSymbol = scanner.read();
        var line = scanner.line();
        var column = scanner.column();
        StringBuilder tokenValue = new StringBuilder(String.valueOf((char)firstSymbol));

        for (var nextSymbol = scanner.read(); !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
            tokenValue.append((char)nextSymbol);
            if (nextSymbol == firstSymbol)
                break;

        }

        return new Token(TokenType.Quoted, tokenValue.toString(), line, column);
    }

    /**
     * Encodes a string value.
     *
     * @param value       A string value to be encoded.
     * @param quoteSymbol A string quote character.
     * @return An encoded string.
     */
    @Override
    public String encodeString(String value, int quoteSymbol) {
        if (value == null) return null;
        return (char)quoteSymbol + value + (char)quoteSymbol;
    }

    /**
     * Decodes a string value.
     *
     * @param value       A string value to be decoded.
     * @param quoteSymbol A string quote character.
     * @return A decoded string.
     */
    @Override
    public String decodeString(String value, int quoteSymbol) {
        if (value == null) return null;

        if (value.length() >= 2 && value.codePointAt(0) == quoteSymbol
                && value.codePointAt(value.length() - 1) == quoteSymbol) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
