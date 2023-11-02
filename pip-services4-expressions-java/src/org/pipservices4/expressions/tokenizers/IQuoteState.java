package org.pipservices4.expressions.tokenizers;

/**
 * Defines an interface for tokenizer state that processes quoted strings.
 */
public interface IQuoteState extends ITokenizerState {
    /**
     * Encodes a string value.
     *
     * @param value       A string value to be encoded.
     * @param quoteSymbol A string quote character.
     * @return An encoded string.
     */
    String encodeString(String value, int quoteSymbol);

    /**
     * Decodes a string value.
     *
     * @param value       A string value to be decoded.
     * @param quoteSymbol A string quote character.
     * @return An decoded string.
     */
    String decodeString(String value, int quoteSymbol);
}
