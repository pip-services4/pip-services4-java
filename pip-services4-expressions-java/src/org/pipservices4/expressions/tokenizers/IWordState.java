package org.pipservices4.expressions.tokenizers;

/**
 * Defines an interface for tokenizer state that processes words, identificators or keywords
 */
public interface IWordState extends ITokenizerState {
    /**
     * Establish characters in the given range as valid characters for part of a word after
     * the first character. Note that the tokenizer must determine which characters are valid
     * as the beginning character of a word.
     *
     * @param fromSymbol First character index of the interval.
     * @param toSymbol   Last character index of the interval.
     * @param enable     <code>true</code> if this state should use characters in the given range.
     */
    void setWordChars(int fromSymbol, int toSymbol, boolean enable) throws Exception;

    /**
     * Clears definitions of word chars.
     */
    void clearWordChars();
}
