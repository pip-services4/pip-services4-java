package org.pipservices4.expressions.mustache.tokenizers;

import org.pipservices4.expressions.tokenizers.AbstractTokenizer;
import org.pipservices4.expressions.tokenizers.ITokenizerState;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.generic.GenericQuoteState;
import org.pipservices4.expressions.tokenizers.generic.GenericSymbolState;
import org.pipservices4.expressions.tokenizers.generic.GenericWhitespaceState;
import org.pipservices4.expressions.tokenizers.generic.GenericWordState;

import java.util.Objects;

public class MustacheTokenizer extends AbstractTokenizer {
    private boolean _special = true;
    private final ITokenizerState _specialState;

    /**
     * Constructs this object with default parameters.
     */
    public MustacheTokenizer() throws Exception {
        super();

        this.setSymbolState(new GenericSymbolState());

        var symbolState = this.getSymbolState();
        symbolState.add("{{", TokenType.Symbol);
        symbolState.add("}}", TokenType.Symbol);
        symbolState.add("{{{", TokenType.Symbol);
        symbolState.add("}}}", TokenType.Symbol);

        this.setNumberState(null);
        this.setQuoteState(new GenericQuoteState());
        this.setWhitespaceState(new GenericWhitespaceState());
        this.setWordState(new GenericWordState());
        this.setCommentState(null);
        this._specialState = new MustacheSpecialState();

        this.clearCharacterStates();
        this.setCharacterState(0x0000, 0x00ff, this.getSymbolState());
        this.setCharacterState(0x0000, ' ', this.getWhitespaceState());

        this.setCharacterState('a', 'z', this.getWordState());
        this.setCharacterState('A', 'Z', this.getWordState());
        this.setCharacterState('0', '9', this.getWordState());
        this.setCharacterState('_', '_', this.getWordState());
        this.setCharacterState(0x00c0, 0x00ff, this.getWordState());
        this.setCharacterState(0x0100, 0xfffe, this.getWordState());

        this.setCharacterState('\"', '\"', this.getQuoteState());
        this.setCharacterState('\'', '\'', this.getQuoteState());

        this.setSkipWhitespaces(true);
        this.setSkipComments(true);
        this.setSkipEof(true);
    }

    @Override
    protected Token readNextToken() throws Exception {
        if (this._scanner == null) {
            return null;
        }

        // Check for initial state
        if (this._nextToken == null && this._lastTokenType == TokenType.Unknown)
            this._special = true;


        // Process quotes
        if (this._special) {
            var token = this._specialState.nextToken(this._scanner, this);
            if (token != null && !Objects.equals(token.getValue(), ""))
                return token;

        }

        // Process other tokens
        this._special = false;
        var token = super.readNextToken();
        // Switch to quote when '{{' or '{{{' symbols found
        if (token != null && (Objects.equals(token.getValue(), "}}") || Objects.equals(token.getValue(), "}}}")))
            this._special = true;

        return token;
    }

}
