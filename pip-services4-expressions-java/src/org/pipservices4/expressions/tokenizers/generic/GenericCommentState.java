package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ICommentState;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * A CommentState object returns a comment from a scanner.
 */
public class GenericCommentState implements ICommentState {
    protected final int LF = '\r';
    protected final int CR = '\n';

    /**
     * Either delegate to a comment-handling state, or return a token with just a slash in it.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        int line = scanner.peekLine();
        int column = scanner.peekColumn();
        StringBuilder tokenValue = new StringBuilder();
        int nextSymbol;
        for (nextSymbol = scanner.read(); !CharValidator.isEof(nextSymbol)
                && nextSymbol != this.CR && nextSymbol != this.LF; nextSymbol = scanner.read()) {
            tokenValue.append((char)nextSymbol);
        }
        if (!CharValidator.isEof(nextSymbol)) {
            scanner.unread();
        }

        return new Token(TokenType.Comment, tokenValue.toString(), line, column);
    }
}
