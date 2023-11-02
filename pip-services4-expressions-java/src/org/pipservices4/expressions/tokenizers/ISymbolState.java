package org.pipservices4.expressions.tokenizers;

/**
 * Defines an interface for tokenizer state that processes delimiters.
 */
public interface ISymbolState extends ITokenizerState {
    /**
     * Add a multi-character symbol.
     *
     * @param value     The symbol to add, such as "=:="
     * @param tokenType The token type
     */
    void add(String value, TokenType tokenType) throws Exception;
}
