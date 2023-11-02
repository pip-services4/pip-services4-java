package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.INumberState;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * A NumberState object returns a number from a scanner. This state's idea of a number allows
 * an optional, initial minus sign, followed by one or more digits. A decimal point and another string
 * of digits may follow these digits.
 */
public class GenericNumberState implements INumberState {
    protected final int MINUS = '-';
    protected final int DOT = '.';

    /**
     * Gets the next token from the stream started from the character linked to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var absorbedDot = false;
        var gotADigit = false;
        StringBuilder tokenValue = new StringBuilder();
        var nextSymbol = scanner.read();
        var line = scanner.line();
        var column = scanner.column();

        // Parses leading minus.
        if (nextSymbol == this.MINUS) {
            tokenValue.append('-');
            nextSymbol = scanner.read();
        }

        // Parses digits before decimal separator.
        for (; CharValidator.isDigit(nextSymbol)
                && !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
            gotADigit = true;
            tokenValue.append((char)nextSymbol);
        }

        // Parses part after the decimal separator.
        if (nextSymbol == this.DOT) {
            absorbedDot = true;
            tokenValue.append('.');
            nextSymbol = scanner.read();

            // Absorb all digits.
            for (; CharValidator.isDigit(nextSymbol)
                    && !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
                gotADigit = true;
                tokenValue.append((char)nextSymbol);
            }
        }

        // Pushback last unprocessed symbol.
        if (!CharValidator.isEof(nextSymbol))
            scanner.unread();


        // Process the result.
        if (!gotADigit) {
            scanner.unreadMany(tokenValue.length());
            if (tokenizer.getSymbolState() != null)
                return tokenizer.getSymbolState().nextToken(scanner, tokenizer);
            else
                throw new Exception("Tokenizer must have an assigned symbol state.");

        }

        return new Token(absorbedDot ? TokenType.Float : TokenType.Integer, tokenValue.toString(), line, column);
    }
}
