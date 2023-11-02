package org.pipservices4.expressions.calculator.tokenizers;

import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.generic.GenericSymbolState;

/**
 * Implements a symbol state object.
 */
public class ExpressionSymbolState extends GenericSymbolState {

    /**
     * Constructs an instance of this class.
     */
    public ExpressionSymbolState() throws Exception {
        super();

        this.add("<=", TokenType.Symbol);
        this.add(">=", TokenType.Symbol);
        this.add("<>", TokenType.Symbol);
        this.add("!=", TokenType.Symbol);
        this.add(">>", TokenType.Symbol);
        this.add("<<", TokenType.Symbol);
    }
}
