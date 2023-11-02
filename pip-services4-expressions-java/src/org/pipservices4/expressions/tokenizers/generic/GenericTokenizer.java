package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.tokenizers.AbstractTokenizer;
import org.pipservices4.expressions.tokenizers.TokenType;

/**
 * Implements a default tokenizer class.
 */
public class GenericTokenizer extends AbstractTokenizer {
    public GenericTokenizer() throws Exception {
        super();

        this.setSymbolState(new GenericSymbolState());
        this.getSymbolState().add("<>", TokenType.Symbol);
        this.getSymbolState().add("<=", TokenType.Symbol);
        this.getSymbolState().add(">=", TokenType.Symbol);

        this.setNumberState(new GenericNumberState());
        this.setQuoteState(new GenericQuoteState());
        this.setWhitespaceState(new GenericWhitespaceState());
        this.setWordState(new GenericWordState());
        this.setCommentState(new GenericCommentState());

        this.clearCharacterStates();
        this.setCharacterState(0x0000, 0x00ff, this.getSymbolState());
        this.setCharacterState(0x0000, ' ', this.getWhitespaceState());

        this.setCharacterState('a', 'z', this.getWordState());
        this.setCharacterState('A', 'Z', this.getWordState());
        this.setCharacterState(0x00c0, 0x00ff, this.getWordState());
        this.setCharacterState(0x0100, 0xfffe, this.getWordState());

        this.setCharacterState('-', '-', this.getNumberState());
        this.setCharacterState('0', '9', this.getNumberState());
        this.setCharacterState('.', '.', this.getNumberState());

        this.setCharacterState('\"', '\"', this.getQuoteState());
        this.setCharacterState('\'', '\'', this.getQuoteState());

        this.setCharacterState('#', '#', this.getCommentState());
    }
}
