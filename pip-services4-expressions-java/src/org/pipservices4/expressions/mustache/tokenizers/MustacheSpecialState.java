package org.pipservices4.expressions.mustache.tokenizers;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.ITokenizerState;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * Implements a quote string state object for Mustache templates.
 */
public class MustacheSpecialState implements ITokenizerState {
    private static final int Bracket = '{';

    /**
     * Gets the next token from the stream started from the character linked to this state.
     * @param scanner A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var line = scanner.peekLine();
        var column = scanner.peekColumn();
        StringBuilder tokenValue = new StringBuilder();

        for (var nextSymbol = scanner.read(); !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
            if (nextSymbol == MustacheSpecialState.Bracket) {
                if (scanner.peek() == MustacheSpecialState.Bracket) {
                    scanner.unread();
                    break;
                }
            }

            tokenValue.append((char)nextSymbol);
        }

        return new Token(TokenType.Special, tokenValue.toString(), line, column);
    }
}
