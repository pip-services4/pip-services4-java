package org.pipservices4.expressions.csv;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.IQuoteState;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

public class CsvQuoteState implements IQuoteState {
    /**
     * Gets the next token from the stream started from the character linked to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var firstSymbol = scanner.read();
        var line = scanner.line();
        var column = scanner.column();
        StringBuilder tokenValue = new StringBuilder();
        tokenValue.append((char)firstSymbol);

        for (var nextSymbol = scanner.read(); !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
            tokenValue.append((char)nextSymbol);
            if (nextSymbol == firstSymbol) {
                if (scanner.peek() == firstSymbol) {
                    nextSymbol = scanner.read();
                    tokenValue.append((char)nextSymbol);
                } else {
                    break;
                }
            }
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
        if (value != null) {
            var quoteString = String.valueOf((char)quoteSymbol);
            return quoteString
                    + value.replace(quoteString, quoteString + quoteString)
                    + quoteString;
        } else {
            return null;
        }
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
            var quoteString = String.valueOf((char)quoteSymbol);
            return value.substring(1, value.length() - 1).replace(quoteString + quoteString, quoteString);
        }
        return value;
    }


}
