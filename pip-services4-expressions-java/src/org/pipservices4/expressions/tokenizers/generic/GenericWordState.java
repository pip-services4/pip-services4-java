package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.IWordState;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharReferenceMap;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

/**
 * A wordState returns a word from a scanner. Like other states, a tokenizer transfers the job
 * of reading to this state, depending on an initial character. Thus, the tokenizer decides
 * which characters may begin a word, and this state determines which characters may appear
 * as a second or later character in a word. These are typically different sets of characters;
 * in particular, it is typical for digits to appear as parts of a word, but not
 * as the initial character of a word.
 * <p/>
 * By default, the following characters may appear in a word.
 * The method <code>setWordChars()</code> allows customizing this.
 * <blockquote><pre>
 * From    To
 *   'a', 'z'
 *   'A', 'Z'
 *   '0', '9'
 *
 *    as well as: minus sign, underscore, and apostrophe.
 * </pre></blockquote>
 */
public class GenericWordState implements IWordState {
    private final CharReferenceMap<Boolean> _map = new CharReferenceMap<>();

    /**
     * Constructs a word state with a default idea of what characters
     * are admissible inside a word (as described in the class comment).
     */
    public GenericWordState() throws Exception {
        this.setWordChars('a', 'z', true);
        this.setWordChars('A', 'Z', true);
        this.setWordChars('0', '9', true);
        this.setWordChars('-', '-', true);
        this.setWordChars('_', '_', true);
        //this.setWordChars(39, 39, true);
        this.setWordChars(0x00c0, 0x00ff, true);
        this.setWordChars(0x0100, 0xfffe, true);
    }

    /**
     * Ignore word (such as blanks and tabs), and return the tokenizer's next token.
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
             nextSymbol = scanner.read())
            tokenValue.append((char) nextSymbol);

        if (!CharValidator.isEof(nextSymbol))
            scanner.unread();

        return new Token(TokenType.Word, tokenValue.toString(), line, column);
    }

    /**
     * Establish characters in the given range as valid characters for part of a word after
     * the first character. Note that the tokenizer must determine which characters are valid
     * as the beginning character of a word.
     *
     * @param fromSymbol First character index of the interval.
     * @param toSymbol   Last character index of the interval.
     * @param enable     <code>true</code> if this state should use characters in the given range.
     */
    @Override
    public void setWordChars(int fromSymbol, int toSymbol, boolean enable) throws Exception {
        this._map.addInterval(fromSymbol, toSymbol, enable);
    }

    /**
     * Clears definitions of word chars.
     */
    @Override
    public void clearWordChars() {
        this._map.clear();
    }
}
