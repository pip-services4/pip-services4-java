package org.pipservices4.expressions.calculator.tokenizers;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.generic.GenericNumberState;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * Implements an Expression-specific number state object.
 */
public class ExpressionNumberState extends GenericNumberState {
    protected final int PLUS = '+';
    protected final int EXP1 = 'e';
    protected final int EXP2 = 'E';

    /**
     * Gets the next token from the stream started from the character linked to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var line = scanner.peekLine();
        var column = scanner.peekColumn();

        // Process leading minus.
        if (scanner.peek() == this.MINUS)
            return tokenizer.getSymbolState().nextToken(scanner, tokenizer);


        // Process numbers using base class algorithm.
        var token = super.nextToken(scanner, tokenizer);

        // Exit if number was not detected.
        if (token.getType() != TokenType.Integer && token.getType() != TokenType.Float)
            return token;

        // Exit if number is not in scientific format.
        var nextChar = scanner.peek();
        if (nextChar != this.EXP1 && nextChar != this.EXP2)
            return token;

        StringBuilder tokenValue = new StringBuilder(String.valueOf((char) scanner.read()));

        // Process '-' or '+' in mantissa
        nextChar = scanner.peek();
        if (nextChar == this.MINUS || nextChar == this.PLUS) {
            tokenValue.append((char) scanner.read());
            nextChar = scanner.peek();
        }

        // Exit if mantissa has no digits.
        if (!CharValidator.isDigit(nextChar)) {
            scanner.unreadMany(tokenValue.length());
            return token;
        }

        // Process matissa digits
        for (; CharValidator.isDigit(nextChar); nextChar = scanner.peek())
            tokenValue.append((char) scanner.read());

        return new Token(TokenType.Float, token.getValue() + tokenValue, line, column);
    }
}
