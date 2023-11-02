package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.ISymbolState;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;

/**
 * The idea of a symbol is a character that stands on its own, such as an ampersand or a parenthesis.
 * For example, when tokenizing the expression <code>(isReady)& (isWilling) </code>, a typical
 * tokenizer would return 7 tokens, including one for each parenthesis and one for the ampersand.
 * Thus a series of symbols such as <code>)&( </code> becomes three tokens, while a series of letters
 * such as <code>isReady</code> becomes a single word token.
 * <p/>
 * Multi-character symbols are an exception to the rule that a symbol is a standalone character.
 * For example, a tokenizer may want less-than-or-equals to tokenize as a single token. This class
 * provides a method for establishing which multi-character symbols an object of this class should
 * treat as single symbols. This allows, for example, <code>"cat &lt;= dog"</code> to tokenize as
 * three tokens, rather than splitting the less-than and equals symbols into separate tokens.
 * <p/>
 * By default, this state recognizes the following multi-character symbols:
 * <code>!=, :-, &lt;=, &gt;=</code>
 */
public class GenericSymbolState implements ISymbolState {
    private final SymbolRootNode _symbols = new SymbolRootNode();


    /**
     * Add a multi-character symbol.
     *
     * @param value     The symbol to add, such as "=:="
     * @param tokenType
     */
    @Override
    public void add(String value, TokenType tokenType) throws Exception {
        this._symbols.add(value, tokenType);
    }

    /**
     * Return a symbol token from a scanner.
     *
     * @param scanner   A textual string to be tokenized.
     * @param tokenizer A tokenizer class that controls the process.
     * @return The next token from the top of the stream.
     */
    @Override
    public Token nextToken(IScanner scanner, ITokenizer tokenizer) throws Exception {
        return this._symbols.nextToken(scanner);
    }
}
