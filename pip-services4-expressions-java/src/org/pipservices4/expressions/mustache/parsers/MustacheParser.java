package org.pipservices4.expressions.mustache.parsers;

import org.pipservices4.expressions.mustache.MustacheException;
import org.pipservices4.expressions.mustache.tokenizers.MustacheTokenizer;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MustacheParser {
    private final ITokenizer _tokenizer = new MustacheTokenizer();
    private String _template = "";
    private List<Token> _originalTokens = new ArrayList<>();
    private List<MustacheToken> _initialTokens = new ArrayList<>();
    private int _currentTokenIndex;
    private List<String> _variableNames = new ArrayList<>();
    private List<MustacheToken> _resultTokens = new ArrayList<>();

    public MustacheParser() throws Exception {
    }

    /**
     * The mustache template.
     */
    public String getTemplate() {
        return this._template;
    }

    /**
     * The mustache template.
     */
    public void setTemplate(String value) throws Exception {
        this.parseString(value);
    }

    public List<Token> getOriginalTokens() {
        return this._originalTokens;
    }

    public void setOriginalTokens(List<Token> value) throws MustacheException {
        this.parseTokens(value);
    }

    /**
     * The list of original mustache tokens.
     */
    public List<MustacheToken> getInitialTokens() {
        return this._initialTokens;
    }

    /**
     * The list of parsed mustache tokens.
     */
    public List<MustacheToken> getResultTokens() {
        return this._resultTokens;
    }

    /**
     * The list of found variable names.
     */
    public List<String> getVariableNames() {
        return this._variableNames;
    }

    /**
     * Sets a new mustache string and parses it into internal byte code.
     *
     * @param mustache A new mustache string.
     */
    public void parseString(String mustache) throws Exception {
        this.clear();
        this._template = mustache != null ? mustache.trim() : "";
        this._originalTokens = this.tokenizeMustache(this._template);
        this.performParsing();
    }

    public void parseTokens(List<Token> tokens) throws MustacheException {
        this.clear();
        this._originalTokens = tokens;
        this._template = this.composeMustache(tokens);
        this.performParsing();
    }

    /**
     * Clears parsing results.
     */
    public void clear() {
        this._template = null;
        this._originalTokens = new ArrayList<>();
        this._initialTokens = new ArrayList<>();
        this._resultTokens = new ArrayList<>();
        this._currentTokenIndex = 0;
        this._variableNames = new ArrayList<>();
    }

    /**
     * Checks are there more tokens for processing.
     *
     * @returns <code>true</code> if some tokens are present.
     */
    private boolean hasMoreTokens() {
        return this._currentTokenIndex < this._initialTokens.size();
    }

    /**
     * Checks are there more tokens available and throws exception if no more tokens available.
     */
    private void checkForMoreTokens() throws MustacheException {
        if (!this.hasMoreTokens()) {
            throw new MustacheException(null, MustacheErrorCode.UNEXPECTED_END, "Unexpected end of mustache", 0, 0);
        }
    }

    /**
     * Gets the current token object.
     *
     * @return The current token object.
     */
    private MustacheToken getCurrentToken() {
        return this._currentTokenIndex < this._initialTokens.size()
                ? this._initialTokens.get(this._currentTokenIndex) : null;
    }

    /**
     * Gets the next token object.
     *
     * @return The next token object.
     */
    private MustacheToken getNextToken() {
        return (this._currentTokenIndex + 1) < this._initialTokens.size()
                ? this._initialTokens.get(this._currentTokenIndex + 1) : null;
    }

    /**
     * Moves to the next token object.
     */
    private void moveToNextToken() {
        this._currentTokenIndex++;
    }

    /**
     * Adds an mustache to the result list
     *
     * @param type   The type of the token to be added.
     * @param value  The value of the token to be added.
     * @param line   The line where the token is.
     * @param column The column number where the token is.
     */
    private MustacheToken addTokenToResult(MustacheTokenType type, String value, int line, int column) {
        var token = new MustacheToken(type, value, line, column);
        this._resultTokens.add(token);
        return token;
    }

    private List<Token> tokenizeMustache(String mustache) throws Exception {
        mustache = mustache != null ? mustache.trim() : "";
        if (!mustache.isEmpty()) {
            this._tokenizer.setSkipWhitespaces(true);
            this._tokenizer.setSkipComments(true);
            this._tokenizer.setSkipEof(true);
            this._tokenizer.setDecodeStrings(true);
            return this._tokenizer.tokenizeBuffer(mustache);
        } else {
            return new ArrayList<>();
        }
    }

    private String composeMustache(List<Token> tokens) {
        StringBuilder builder = new StringBuilder();
        for (var token : tokens)
            builder.append(token.getValue());

        return builder.toString();
    }

    private void performParsing() throws MustacheException {
        if (!this._originalTokens.isEmpty()) {
            this.completeLexicalAnalysis();
            this.performSyntaxAnalysis();
            if (this.hasMoreTokens()) {
                var token = this.getCurrentToken();
                throw new MustacheException(
                        null,
                        MustacheErrorCode.ERROR_NEAR,
                        "Syntax error near " + (token != null ? token.getValue() : "unknown"),
                        token != null ? token.getLine() : 0,
                        token != null ? token.getColumn() : 0);
            }
            this.lookupVariables();
        }
    }

    /**
     * Tokenizes the given mustache and prepares an initial tokens list.
     */
    private void completeLexicalAnalysis() throws MustacheException {
        MustacheLexicalState state = MustacheLexicalState.Value;
        String closingBracket = null;
        String operator1 = null;
        String operator2 = null;
        String variable = null;

        for (var token : this._originalTokens) {
            var tokenType = MustacheTokenType.Unknown;
            String tokenValue = null;

            if (state == MustacheLexicalState.Comment) {
                if (Objects.equals(token.getValue(), "}}") || Objects.equals(token.getValue(), "}}}")) {
                    state = MustacheLexicalState.Closure;
                } else {
                    continue;
                }
            }

            switch (token.getType()) {
                case Special:
                    if (state == MustacheLexicalState.Value) {
                        tokenType = MustacheTokenType.Value;
                        tokenValue = token.getValue();
                    }
                    break;
                case Symbol:
                    if (state == MustacheLexicalState.Value && (Objects.equals(token.getValue(), "{{") || Objects.equals(token.getValue(), "{{{"))) {
                        closingBracket = Objects.equals(token.getValue(), "{{") ? "}}" : "}}}";
                        state = MustacheLexicalState.Operator1;
                        continue;
                    }
                    if (state == MustacheLexicalState.Operator1 && Objects.equals(token.getValue(), "!")) {
                        operator1 = token.getValue();
                        state = MustacheLexicalState.Comment;
                        continue;
                    }
                    if (state == MustacheLexicalState.Operator1 &&
                            (Objects.equals(token.getValue(), "/")
                                    || Objects.equals(token.getValue(), "#")
                                    || Objects.equals(token.getValue(), "^"))) {
                        operator1 = token.getValue();
                        state = MustacheLexicalState.Operator2;
                        continue;
                    }

                    if (state == MustacheLexicalState.Variable && (Objects.equals(token.getValue(), "}}") || Objects.equals(token.getValue(), "}}}"))) {
                        if (!Objects.equals(operator1, "/")) {
                            variable = operator2;
                            operator2 = null;
                        }
                        state = MustacheLexicalState.Closure;
                        // Pass through
                    }
                    if (state == MustacheLexicalState.Closure &&
                            (Objects.equals(token.getValue(), "}}") || Objects.equals(token.getValue(), "}}}"))) {
                        if (!closingBracket.equals(token.getValue())) {
                            throw new MustacheException(null,
                                    MustacheErrorCode.MISMATCHED_BRACKETS,
                                    "Mismatched brackets. Expected '" + closingBracket + "'",
                                    token.getLine(), token.getColumn());
                        }

                        if (Objects.equals(operator1, "#") && (operator2 == null || operator2.equals("if"))) {
                            tokenType = MustacheTokenType.Section;
                            tokenValue = variable;
                        }

                        if (Objects.equals(operator1, "#") && Objects.equals(operator2, "unless")) {
                            tokenType = MustacheTokenType.InvertedSection;
                            tokenValue = variable;
                        }

                        if (Objects.equals(operator1, "^") && operator2 == null) {
                            tokenType = MustacheTokenType.InvertedSection;
                            tokenValue = variable;
                        }

                        if (Objects.equals(operator1, "/")) {
                            tokenType = MustacheTokenType.SectionEnd;
                            tokenValue = variable;
                        }

                        if (operator1 == null) {
                            tokenType = closingBracket.equals("}}") ? MustacheTokenType.Variable : MustacheTokenType.EscapedVariable;
                            tokenValue = variable;
                        }

                        if (tokenType == MustacheTokenType.Unknown) {
                            throw new MustacheException(
                                    null, MustacheErrorCode.INTERNAL,
                                    "Internal error",
                                    token.getLine(),
                                    token.getColumn()
                            );
                        }

                        operator1 = null;
                        operator2 = null;
                        variable = null;
                        state = MustacheLexicalState.Value;
                    }
                    break;
                case Word:
                    if (state == MustacheLexicalState.Operator1) {
                        state = MustacheLexicalState.Variable;
                    }
                    if (state == MustacheLexicalState.Operator2 &&
                            (Objects.equals(token.getValue(), "if")
                                    || Objects.equals(token.getValue(), "unless"))) {
                        operator2 = token.getValue();
                        state = MustacheLexicalState.Variable;
                        continue;
                    }
                    if (state == MustacheLexicalState.Operator2) {
                        state = MustacheLexicalState.Variable;
                    }
                    if (state == MustacheLexicalState.Variable) {
                        variable = token.getValue();
                        state = MustacheLexicalState.Closure;
                        continue;
                    }
                    break;
                case Whitespace:
                    continue;
            }
            if (tokenType == MustacheTokenType.Unknown) {
                throw new MustacheException(null,
                        MustacheErrorCode.UNEXPECTED_SYMBOL, "Unexpected symbol '" + token.getValue() + "'",
                        token.getLine(),
                        token.getColumn()
                );
            }
            this._initialTokens.add(new MustacheToken(tokenType, tokenValue, token.getLine(), token.getColumn()));
        }

        if (state != MustacheLexicalState.Value)
            throw new MustacheException(null, MustacheErrorCode.UNEXPECTED_END, "Unexpected end of file", 0, 0);
    }

    /**
     * Performs a syntax analysis at level 0.
     */
    private void performSyntaxAnalysis() throws MustacheException {
        this.checkForMoreTokens();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            this.moveToNextToken();

            if (token == null || token.getType() == MustacheTokenType.SectionEnd) {
                throw new MustacheException(
                        null,
                        MustacheErrorCode.UNEXPECTED_SECTION_END,
                        "Unexpected section end for variable '" + (token != null ? token.getValue() : "unknown") + "'",
                        token != null ? token.getLine() : 0,
                        token != null ? token.getColumn() : 0
                );
            }

            var result = this.addTokenToResult(token.getType(), token.getValue(), token.getLine(), token.getColumn());

            if (token.getType() == MustacheTokenType.Section || token.getType() == MustacheTokenType.InvertedSection)
                result.getTokens().addAll(this.performSyntaxAnalysisForSection(token.getValue()));

        }
    }

    /**
     * Performs a syntax analysis for section
     */
    private List<MustacheToken> performSyntaxAnalysisForSection(String variable) throws MustacheException {
        List<MustacheToken> result = new ArrayList<>();

        this.checkForMoreTokens();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            this.moveToNextToken();

            if (token != null) {
                if (token.getType() == MustacheTokenType.SectionEnd && (Objects.equals(token.getValue(), variable) || token.getValue() == null)) {
                    return result;
                }

                if (token.getType() == MustacheTokenType.SectionEnd) {
                    throw new MustacheException(
                            null,
                            MustacheErrorCode.UNEXPECTED_SECTION_END,
                            "Unexpected section end for variable '" + variable + "'",
                            token.getLine(),
                            token.getColumn()
                    );
                }

                var resultToken = new MustacheToken(token.getType(), token.getValue(), token.getLine(), token.getColumn());

                if (token.getType() == MustacheTokenType.Section || token.getType() == MustacheTokenType.InvertedSection)
                    resultToken.getTokens().addAll(this.performSyntaxAnalysisForSection(token.getValue()));


                result.add(resultToken);
            }
        }

        var token = this.getCurrentToken();
        throw new MustacheException(
                null,
                MustacheErrorCode.NOT_CLOSED_SECTION,
                "Not closed section for variable '" + variable + "'",
                token != null ? token.getLine() : 0,
                token != null ? token.getColumn() : 0
        );
    }

    /**
     * Retrieves variables from the parsed output.
     */
    private void lookupVariables() {
        if (this._originalTokens == null) return;

        this._variableNames = new ArrayList<>();
        for (var token : this._initialTokens) {
            if (token.getType() != MustacheTokenType.Value
                    && token.getType() != MustacheTokenType.Comment
                    && token.getValue() != null) {
                var variableName = token.getValue().toLowerCase();
                var found = this._variableNames.stream()
                        .anyMatch((v) -> v.toLowerCase().equals(variableName));
                if (!found) {
                    this._variableNames.add(token.getValue());
                }
            }
        }
    }

}
