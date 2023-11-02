package org.pipservices4.expressions.tokenizers;

/**
 * Defines an interface for tokenizer state that processes whitespaces (' ', '\t')
 */
public interface IWhitespaceState extends ITokenizerState {
    /**
     * Establish the given characters as whitespace to ignore.
     *
     * @param fromSymbol First character index of the interval.
     * @param toSymbol   Last character index of the interval.
     * @param enable     <code>true</code> if this state should ignore characters in the given range.
     */
    void setWhitespaceChars(int fromSymbol, int toSymbol, boolean enable) throws Exception;

    /**
     * Clears definitions of whitespace characters.
     */
    void clearWhitespaceChars();
}
