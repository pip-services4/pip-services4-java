package org.pipservices4.expressions.tokenizers;

import org.pipservices4.expressions.io.IScanner;

import java.util.List;

public interface ITokenizer {

    /**
     * Skip unknown characters
     */
    Boolean getSkipUnknown();

    /**
     * Skip unknown characters
     */
    void setSkipUnknown(boolean value);


    /**
     * Skips whitespaces.
     */
    Boolean getSkipWhitespaces();

    /**
     * Skips whitespaces.
     */
    void setSkipWhitespaces(boolean value);

    /**
     * Skips comments.
     */
    Boolean getSkipComments();

    /**
     * Skips comments.
     */
    void setSkipComments(boolean value);

    /**
     * Skips End-Of-File token at the end of stream.
     */
    Boolean getSkipEof();

    /**
     * Skips End-Of-File token at the end of stream.
     */
    void setSkipEof(boolean value);

    /**
     * Merges whitespaces.
     */
    Boolean getMergeWhitespaces();

    /**
     * Merges whitespaces.
     */
    void setMergeWhitespaces(boolean value);

    /**
     * Unifies numbers: "Integers" and "Floats" makes just "Numbers"
     */
    Boolean getUnifyNumbers();

    /**
     * Unifies numbers: "Integers" and "Floats" makes just "Numbers"
     */
    void setUnifyNumbers(boolean value);

    /**
     * Decodes quoted strings.
     */
    Boolean getDecodeStrings();

    /**
     * Decodes quoted strings.
     */
    void setDecodeStrings(boolean value);

    /**
     * A token state to process comments.
     */
    ICommentState getCommentState();

    /**
     * A token state to process comments.
     */
    void setCommentState(ICommentState value);

    /**
     * A token state to process numbers.
     */
    INumberState getNumberState();

    /**
     * A token state to process numbers.
     */
    void setNumberState(INumberState value);

    /**
     * A token state to process quoted strings.
     */
    IQuoteState getQuoteState();

    /**
     * A token state to process quoted strings.
     */
    void setQuoteState(IQuoteState value);


    /**
     * A token state to process symbols (single like "=" or muti-character like "<>")
     */
    ISymbolState getSymbolState();

    /**
     * A token state to process symbols (single like "=" or muti-character like "<>")
     */
    void setSymbolState(ISymbolState value);

    /**
     * A token state to process white space delimiters.
     */
    IWhitespaceState getWhitespaceState();

    /**
     * A token state to process white space delimiters.
     */
    void setWhitespaceState(IWhitespaceState value);

    /**
     * A token state to process words or indentificators.
     */
    IWordState getWordState();

    /**
     * A token state to process words or indentificators.
     */
    void setWordState(IWordState value);

    /**
     * The stream scanner to tokenize.
     */
    IScanner getScanner();

    /**
     * The stream scanner to tokenize.
     */
    void setScanner(IScanner scanner);

    /**
     * Checks if there is the next token exist.
     *
     * @return <code>true</code> if scanner has the next token.
     */
    Boolean hasNextToken() throws Exception;

    /**
     * Gets the next token from the scanner.
     *
     * @return Next token of <code>null</code> if there are no more tokens left.
     */
    Token nextToken() throws Exception;

    /**
     * Tokenizes a textual stream into a list of token structures.
     *
     * @param scanner A textual stream to be tokenized.
     * @return A list of token structures.
     */
    List<Token> tokenizeStream(IScanner scanner) throws Exception;

    /**
     * Tokenizes a string buffer into a list of tokens structures.
     *
     * @param buffer A string buffer to be tokenized.
     * @return A list of token structures.
     */
    List<Token> tokenizeBuffer(String buffer) throws Exception;

    /**
     * Tokenizes a textual stream into a list of strings.
     *
     * @param scanner A textual stream to be tokenized.
     * @return A list of token strings.
     */
    List<String> tokenizeStreamToStrings(IScanner scanner) throws Exception;

    /**
     * Tokenizes a string buffer into a list of strings.
     *
     * @param buffer A string buffer to be tokenized.
     * @return A list of token strings.
     */
    List<String> tokenizeBufferToStrings(String buffer) throws Exception;
}
