package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * This state will either delegate to a comment-handling state, or return a token with just a slash in it.
 */
public class CCommentState extends CppCommentState {
    /**
     * Either delegate to a comment-handling state, or return a token with just a slash in it.
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

        if (firstSymbol != this.SLASH) {
            scanner.unread();
            throw new Exception("Incorrect usage of CCommentState.");
        }

        var secondSymbol = scanner.read();
        if (secondSymbol == this.STAR) {
            return new Token(TokenType.Comment, "/*" + this.getMultiLineComment(scanner), line, column);
        } else {
            if (!CharValidator.isEof(secondSymbol))
                scanner.unread();

            if (!CharValidator.isEof(firstSymbol))
                scanner.unread();

            return tokenizer.getSymbolState().nextToken(scanner, tokenizer);
        }
    }
}
