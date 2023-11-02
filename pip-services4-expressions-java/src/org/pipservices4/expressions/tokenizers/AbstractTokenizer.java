package org.pipservices4.expressions.tokenizers;


import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.utilities.CharReferenceMap;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implements an abstract tokenizer class.
 */
public abstract class AbstractTokenizer implements ITokenizer {
    private final CharReferenceMap<ITokenizerState> _map = new CharReferenceMap<>();

    private boolean skipUnknown = false;
    private boolean skipWhitespaces = false;
    private boolean skipComments = false;
    private boolean skipEof = false;
    private boolean mergeWhitespaces = false;
    private boolean unifyNumbers = false;
    private boolean decodeStrings = false;

    private ICommentState commentState;
    private INumberState numberState;
    private IQuoteState quoteState;
    private ISymbolState symbolState;
    private IWhitespaceState whitespaceState;
    private IWordState wordState;

    protected IScanner _scanner;
    protected Token _nextToken;
    protected TokenType _lastTokenType = TokenType.Unknown;

    @Override
    public Boolean getSkipUnknown() {
        return skipUnknown;
    }

    @Override
    public void setSkipUnknown(boolean value) {
        skipUnknown = value;
    }

    @Override
    public Boolean getSkipWhitespaces() {
        return skipWhitespaces;
    }

    @Override
    public void setSkipWhitespaces(boolean value) {
        skipWhitespaces = value;
    }

    @Override
    public Boolean getSkipComments() {
        return skipComments;
    }

    @Override
    public void setSkipComments(boolean value) {
        skipComments = value;
    }

    @Override
    public Boolean getSkipEof() {
        return skipEof;
    }

    @Override
    public void setSkipEof(boolean value) {
        skipEof = value;
    }

    @Override
    public Boolean getMergeWhitespaces() {
        return mergeWhitespaces;
    }

    @Override
    public void setMergeWhitespaces(boolean value) {
        mergeWhitespaces = value;
    }

    @Override
    public Boolean getUnifyNumbers() {
        return unifyNumbers;
    }

    @Override
    public void setUnifyNumbers(boolean value) {
        unifyNumbers = value;
    }

    @Override
    public Boolean getDecodeStrings() {
        return decodeStrings;
    }

    @Override
    public void setDecodeStrings(boolean value) {
        decodeStrings = value;
    }

    @Override
    public ICommentState getCommentState() {
        return commentState;
    }

    @Override
    public void setCommentState(ICommentState value) {
        commentState = value;
    }

    @Override
    public INumberState getNumberState() {
        return numberState;
    }

    @Override
    public void setNumberState(INumberState value) {
        numberState = value;
    }

    @Override
    public IQuoteState getQuoteState() {
        return quoteState;
    }

    @Override
    public void setQuoteState(IQuoteState value) {
        quoteState = value;
    }

    @Override
    public ISymbolState getSymbolState() {
        return symbolState;
    }

    @Override
    public void setSymbolState(ISymbolState value) {
        symbolState = value;
    }

    @Override
    public IWhitespaceState getWhitespaceState() {
        return whitespaceState;
    }

    @Override
    public void setWhitespaceState(IWhitespaceState value) {
        whitespaceState = value;
    }

    @Override
    public IWordState getWordState() {
        return wordState;
    }

    @Override
    public void setWordState(IWordState value) {
        wordState = value;
    }

    @Override
    public IScanner getScanner() {
        return _scanner;
    }

    @Override
    public void setScanner(IScanner value) {
        this._scanner = value;
        this._nextToken = null;
        this._lastTokenType = TokenType.Unknown;
    }

    public ITokenizerState getCharacterState(int symbol) {
        return this._map.lookup(symbol);
    }

    public void setCharacterState(int fromSymbol, int toSymbol, ITokenizerState state) throws Exception {
        this._map.addInterval(fromSymbol, toSymbol, state);
    }

    public void clearCharacterStates() {
        this._map.clear();
    }

    @Override
    public Boolean hasNextToken() throws Exception {
        this._nextToken = this._nextToken == null ? this.readNextToken() : this._nextToken;
        return this._nextToken != null;
    }

    @Override
    public Token nextToken() throws Exception {
        var token = this._nextToken == null ? this.readNextToken() : this._nextToken;
        this._nextToken = null;
        return token;
    }

    protected Token readNextToken() throws Exception {
        if (this._scanner == null)
            return null;


        var line = this._scanner.peekLine();
        var column = this._scanner.peekColumn();
        Token token = null;

        while (true) {
            // Read character
            var nextChar = this._scanner.peek();

            // If reached Eof then exit
            if (CharValidator.isEof(nextChar)) {
                token = null;
                break;
            }

            // Get state for character
            ITokenizerState state = this.getCharacterState(nextChar);
            if (state != null)
                token = state.nextToken(this._scanner, this);


            // Check for unknown characters and endless loops...
            if (token == null || Objects.equals(token.getValue(), ""))
                token = new Token(TokenType.Unknown, String.valueOf((char)this._scanner.read()), line, column);


            // Skip unknown characters if option set.
            if (token.getType() == TokenType.Unknown && this.skipUnknown) {
                this._lastTokenType = token.getType();
                continue;
            }

            // Decode strings is option set.
            if (state instanceof IQuoteState && this.decodeStrings) { // state.getClass().getMethod("decodeString", String.class, int.class);
                token = new Token(token.getType(), this.quoteState.decodeString(token.getValue(), nextChar), line, column);
            }

            // Skips comments if option set.
            if (token.getType() == TokenType.Comment && this.skipComments) {
                this._lastTokenType = token.getType();
                continue;
            }

            // Skips whitespaces if option set.
            if (token.getType() == TokenType.Whitespace
                    && this._lastTokenType == TokenType.Whitespace
                    && this.skipWhitespaces) {
                this._lastTokenType = token.getType();
                continue;
            }

            // Unifies whitespaces if option set.
            if (token.getType() == TokenType.Whitespace && this.mergeWhitespaces) {
                token = new Token(TokenType.Whitespace, " ", line, column);
            }

            // Unify numbers if option set.
            if (this.unifyNumbers
                    && (token.getType() == TokenType.Integer
                    || token.getType() == TokenType.Float
                    || token.getType() == TokenType.HexDecimal)) {
                token = new Token(TokenType.Number, token.getValue(), line, column);
            }

            break;
        }

        // Adds an Eof if option is not set.
        if (token == null && this._lastTokenType != TokenType.Eof && !this.skipEof)
            token = new Token(TokenType.Eof, null, line, column);

        // Assigns the last token type
        this._lastTokenType = token != null ? token.getType() : TokenType.Eof;

        return token;
    }

    @Override
    public List<Token> tokenizeStream(IScanner scanner) throws Exception {
        this._scanner = scanner;
        List<Token> tokenList = new ArrayList<>();
        for (var token = this.nextToken(); token != null; token = this.nextToken()) {
            tokenList.add(token);
        }
        return tokenList;
    }

    @Override
    public List<Token> tokenizeBuffer(String buffer) throws Exception {
        var scanner = new StringScanner(buffer);
        return this.tokenizeStream(scanner);
    }

    @Override
    public List<String> tokenizeStreamToStrings(IScanner scanner) throws Exception {
        this._scanner = scanner;
        List<String> stringList = new ArrayList<>();
        for (var token = this.nextToken(); token != null; token = this.nextToken()) {
            stringList.add(token.getValue());
        }
        return stringList;
    }

    @Override
    public List<String> tokenizeBufferToStrings(String buffer) throws Exception {
        var scanner = new StringScanner(buffer);
        return this.tokenizeStreamToStrings(scanner);
    }
}
