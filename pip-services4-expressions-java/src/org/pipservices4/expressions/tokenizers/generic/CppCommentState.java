package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

import java.security.InvalidParameterException;

/**
 * This state will either delegate to a comment-handling state, or return a token with just a slash in it.
 */
public class CppCommentState extends GenericCommentState {
    protected final int STAR = '*';
    protected final int SLASH = '/';

    /**
     * Ignore everything up to a closing star and slash, and then return the tokenizer's next token.
     *
     * @param scanner
     * @return
     */
    protected String getMultiLineComment(IScanner scanner) {
        StringBuilder result = new StringBuilder();
        int lastSymbol = 0;
        for (var nextSymbol = scanner.read(); !CharValidator.isEof(nextSymbol); nextSymbol = scanner.read()) {
            result.append((char)nextSymbol);
            if (lastSymbol == this.STAR && nextSymbol == this.SLASH)
                break;

            lastSymbol = nextSymbol;
        }
        return result.toString();
    }

    /**
     * Ignore everything up to an end-of-line and return the tokenizer's next token.
     * @param scanner
     */
    protected String getSingleLineComment(IScanner scanner) {
        StringBuilder result = new StringBuilder();
        int nextSymbol;
        for (nextSymbol = scanner.read();
             !CharValidator.isEof(nextSymbol) && !CharValidator.isEol(nextSymbol);
             nextSymbol = scanner.read()) {
            result.append((char) nextSymbol);
        }
        if (CharValidator.isEol(nextSymbol)) {
            scanner.unread();
        }
        return result.toString();
    }

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
            throw new InvalidParameterException("Incorrect usage of CppCommentState.");
        }

        var secondSymbol = scanner.read();
        if (secondSymbol == this.STAR) {
            return new Token(TokenType.Comment, "/*" + this.getMultiLineComment(scanner), line, column);
        } else if (secondSymbol == this.SLASH) {
            return new Token(TokenType.Comment, "//" + this.getSingleLineComment(scanner), line, column);
        } else {
            if (!CharValidator.isEof(secondSymbol)) {
                scanner.unread();
            }
            if (!CharValidator.isEof(firstSymbol)) {
                scanner.unread();
            }
            return tokenizer.getSymbolState().nextToken(scanner, tokenizer);
        }
    }
}
