package org.pipservices4.expressions.tokenizers;

import org.pipservices4.expressions.io.IScanner;

/**
 * A tokenizerState returns a token, given a scanner, an initial character read from the scanner,
 * and a tokenizer that is conducting an overall tokenization of the scanner. The tokenizer will
 * typically have a character state table that decides which state to use, depending on an initial
 * character. If a single character is insufficient, a state such as <code>SlashState</code>
 * will read a second character, and may delegate to another state, such as <code>SlashStarState</code>.
 * This prospect of delegation is the reason that the <code>nextToken()</code>
 * method has a tokenizer argument.
 */
public interface ITokenizerState {
    /**
     * Gets the next token from the stream started from the character linked to this state.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception;
}
