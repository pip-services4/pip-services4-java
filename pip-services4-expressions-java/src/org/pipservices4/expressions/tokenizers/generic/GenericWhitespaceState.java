package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.IWhitespaceState;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharReferenceMap;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * A whitespace state ignores whitespace (such as blanks and tabs), and returns the tokenizer's
 * next token. By default, all characters from 0 to 32 are whitespace.
 */
public class GenericWhitespaceState implements IWhitespaceState {
    private final CharReferenceMap<Boolean> _map = new CharReferenceMap<>();

    /**
     * Constructs a whitespace state with a default idea of what characters are, in fact, whitespace.
     */
    public GenericWhitespaceState() throws Exception {
        this.setWhitespaceChars(0, ' ', true);
    }

    /**
     * Ignore whitespace (such as blanks and tabs), and return the tokenizer's next token.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        var line = scanner.peekLine();
        var column = scanner.peekColumn();
        int nextSymbol;
        StringBuilder tokenValue = new StringBuilder();
        for (nextSymbol = scanner.read();
             nextSymbol != -1 && this._map.lookup(nextSymbol) != null && this._map.lookup(nextSymbol);
             nextSymbol = scanner.read()) {
            tokenValue.append((char)nextSymbol);
        }

        if (!CharValidator.isEof(nextSymbol)) {
            scanner.unread();
        }

        return new Token(TokenType.Whitespace, tokenValue.toString(), line, column);
    }

    /**
     * Establish the given characters as whitespace to ignore.
     *
     * @param fromSymbol First character index of the interval.
     * @param toSymbol   Last character index of the interval.
     * @param enable     <code>true</code> if this state should ignore characters in the given range.
     */
    @Override
    public void setWhitespaceChars(int fromSymbol, int toSymbol, boolean enable) throws Exception {
        this._map.addInterval(fromSymbol, toSymbol, enable);
    }

    /**
     * Clears definitions of whitespace characters.
     */
    @Override
    public void clearWhitespaceChars() {
        this._map.clear();
    }
}
