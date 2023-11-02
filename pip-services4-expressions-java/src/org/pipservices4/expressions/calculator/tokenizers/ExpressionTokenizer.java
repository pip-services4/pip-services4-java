package org.pipservices4.expressions.calculator.tokenizers;

import org.pipservices4.expressions.tokenizers.AbstractTokenizer;
import org.pipservices4.expressions.tokenizers.generic.CppCommentState;
import org.pipservices4.expressions.tokenizers.generic.GenericWhitespaceState;

/**
 * Implement tokenizer to perform lexical analysis for expressions.
 */
public class ExpressionTokenizer extends AbstractTokenizer {
    /**
     * Constructs an instance of this class.
     */
    public ExpressionTokenizer() throws Exception {
        super();

        this.setDecodeStrings(false);

        this.setWhitespaceState(new GenericWhitespaceState());

        this.setSymbolState(new ExpressionSymbolState());
        this.setNumberState(new ExpressionNumberState());
        this.setQuoteState(new ExpressionQuoteState());
        this.setWordState(new ExpressionWordState());
        this.setCommentState(new CppCommentState());

        this.clearCharacterStates();
        this.setCharacterState(0x0000, 0xfffe, this.getSymbolState());
        this.setCharacterState(0, ' ', this.getWhitespaceState());

        this.setCharacterState('a', 'z', this.getWordState());
        this.setCharacterState('A', 'Z', this.getWordState());
        this.setCharacterState(0x00c0, 0x00ff, this.getWordState());
        this.setCharacterState('_', '_', this.getWordState());

        this.setCharacterState('0', '9', this.getNumberState());
        this.setCharacterState('-', '-', this.getNumberState());
        this.setCharacterState('.', '.', this.getNumberState());

        this.setCharacterState('"', '"', this.getQuoteState());
        this.setCharacterState('\'', '\'', this.getQuoteState());

        this.setCharacterState('/', '/', this.getCommentState());
    }
}
