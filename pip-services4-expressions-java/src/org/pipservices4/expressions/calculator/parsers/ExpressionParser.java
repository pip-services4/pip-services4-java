package org.pipservices4.expressions.calculator.parsers;

import org.pipservices4.commons.convert.FloatConverter;
import org.pipservices4.commons.convert.IntegerConverter;
import org.pipservices4.expressions.calculator.SyntaxErrorCode;
import org.pipservices4.expressions.calculator.SyntaxException;
import org.pipservices4.expressions.calculator.tokenizers.ExpressionTokenizer;
import org.pipservices4.expressions.tokenizers.ITokenizer;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.variants.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an expression parser class.
 */
public class ExpressionParser {
    /**
     * Defines a list of operators.
     */
    private final String[] Operators = new String[]{
            "(", ")", "[", "]", "+", "-", "*", "/", "%", "^",
            "=", "<>", "!=", ">", "<", ">=", "<=", "<<", ">>",
            "AND", "OR", "XOR", "NOT", "IS", "IN", "NULL", "LIKE", ","
    };

    /**
     * Defines a list of operator token types.
     * Note: it must match to operators.
     */
    private final ExpressionTokenType[] OperatorTypes = new ExpressionTokenType[]{
            ExpressionTokenType.LeftBrace, ExpressionTokenType.RightBrace,
            ExpressionTokenType.LeftSquareBrace, ExpressionTokenType.RightSquareBrace,
            ExpressionTokenType.Plus, ExpressionTokenType.Minus,
            ExpressionTokenType.Star, ExpressionTokenType.Slash,
            ExpressionTokenType.Procent, ExpressionTokenType.Power,
            ExpressionTokenType.Equal, ExpressionTokenType.NotEqual,
            ExpressionTokenType.NotEqual, ExpressionTokenType.More,
            ExpressionTokenType.Less, ExpressionTokenType.EqualMore,
            ExpressionTokenType.EqualLess, ExpressionTokenType.ShiftLeft,
            ExpressionTokenType.ShiftRight, ExpressionTokenType.And,
            ExpressionTokenType.Or, ExpressionTokenType.Xor,
            ExpressionTokenType.Not, ExpressionTokenType.Is,
            ExpressionTokenType.In, ExpressionTokenType.Null,
            ExpressionTokenType.Like, ExpressionTokenType.Comma
    };

    private final ITokenizer _tokenizer = new ExpressionTokenizer();
    private String _expression = "";
    private List<Token> _originalTokens = new ArrayList<>();
    private List<ExpressionToken> _initialTokens = new ArrayList<>();
    private int _currentTokenIndex;
    private List<String> _variableNames = new ArrayList<>();
    private List<ExpressionToken> _resultTokens = new ArrayList<>();

    public ExpressionParser() throws Exception {
    }

    /**
     * The expression string.
     */
    public String getExpression() {
        return this._expression;
    }

    /**
     * The expression string.
     */
    public void setExpression(String value) throws Exception {
        this.parseString(value);
    }

    public List<Token> getOriginalTokens() {
        return this._originalTokens;
    }

    public void setOriginalTokens(List<Token> value) throws SyntaxException {
        this.parseTokens(value);
    }

    /**
     * The list of original expression tokens.
     */
    public List<ExpressionToken> getInitialTokens() {
        return this._initialTokens;
    }

    /**
     * The list of parsed expression tokens.
     */
    public List<ExpressionToken> getResultTokens() {
        return this._resultTokens;
    }

    /**
     * The list of found variable names.
     */
    public List<String> getVariableNames() {
        return this._variableNames;
    }

    /**
     * Sets a new expression string and parses it into internal byte code.
     *
     * @param expression A new expression string.
     */
    public void parseString(String expression) throws Exception {
        this.clear();
        this._expression = expression != null ? expression.trim() : "";
        this._originalTokens = this.tokenizeExpression(this._expression);
        this.performParsing();
    }

    public void parseTokens(List<Token> tokens) throws SyntaxException {
        this.clear();
        this._originalTokens = tokens;
        this._expression = this.composeExpression(tokens);
        this.performParsing();
    }

    /**
     * Clears parsing results.
     */
    public void clear() {
        this._expression = null;
        this._originalTokens = new ArrayList<>();
        this._initialTokens = new ArrayList<>();
        this._resultTokens = new ArrayList<>();
        this._currentTokenIndex = 0;
        this._variableNames = new ArrayList<>();
    }

    /**
     * Checks are there more tokens for processing.
     *
     * @return <code>true</code> if some tokens are present.
     */
    private boolean hasMoreTokens() {
        return this._currentTokenIndex < this._initialTokens.size();
    }

    /**
     * Checks are there more tokens available and throws exception if no more tokens available.
     */
    private void checkForMoreTokens() throws SyntaxException {
        if (!this.hasMoreTokens()) {
            throw new SyntaxException(null, SyntaxErrorCode.UNEXPECTED_END, "Unexpected end of expression.", 0, 0);
        }
    }

    /**
     * Gets the current token object.
     *
     * @return The current token object.
     */
    private ExpressionToken getCurrentToken() {
        return this._currentTokenIndex < this._initialTokens.size()
                ? this._initialTokens.get(this._currentTokenIndex) : null;
    }

    /**
     * Gets the next token object.
     *
     * @return The next token object.
     */
    private ExpressionToken getNextToken() {
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
     * Adds an expression to the result list
     *
     * @param type   The type of the token to be added.
     * @param value  The value of the token to be added.
     * @param line   The line number where the token is.
     * @param column The column number where the token is.
     */
    private void addTokenToResult(ExpressionTokenType type, Variant value, int line, int column) {
        this._resultTokens.add(new ExpressionToken(type, value, line, column));
    }

    /**
     * Matches available tokens types with types from the list.
     * If tokens matchs then shift the list.
     *
     * @param types A list of token types to compare.
     *              <code>true</code> if token types match.
     */
    private boolean matchTokensWithTypes(ExpressionTokenType... types) {
        var matches = false;
        for (var i = 0; i < types.length; i++) {
            if (this._currentTokenIndex + i < this._initialTokens.size()) {
                matches = this._initialTokens.get(this._currentTokenIndex + i).getType() == types[i];
            } else {
                matches = false;
                break;
            }
        }

        if (matches) {
            this._currentTokenIndex += types.length;
        }
        return matches;
    }

    private List<Token> tokenizeExpression(String expression) throws Exception {
        expression = expression != null ? expression.trim() : "";
        if (!expression.isEmpty()) {
            this._tokenizer.setSkipWhitespaces(true);
            this._tokenizer.setSkipComments(true);
            this._tokenizer.setSkipEof(true);
            this._tokenizer.setDecodeStrings(true);
            return this._tokenizer.tokenizeBuffer(expression);
        } else {
            return new ArrayList<>();
        }
    }

    private String composeExpression(List<Token> tokens) {
        StringBuilder builder = new StringBuilder();
        for (var token : tokens)
            builder.append(token.getValue());

        return builder.toString();
    }

    private void performParsing() throws SyntaxException {
        if (!this._originalTokens.isEmpty()) {
            this.completeLexicalAnalysis();
            this.performSyntaxAnalysis();
            if (this.hasMoreTokens()) {
                var token = this.getCurrentToken();
                throw new SyntaxException(
                        null,
                        SyntaxErrorCode.ERROR_NEAR,
                        String.valueOf(token != null ? token.getValue() : "unknown"),
                        token != null ? token.getLine() : 0,
                        token != null ? token.getColumn() : 0);
            }
        }
    }

    /**
     * Tokenizes the given expression and prepares an initial tokens list.
     */
    private void completeLexicalAnalysis() throws SyntaxException {
        for (var token : this._originalTokens) {
            var tokenType = ExpressionTokenType.Unknown;
            var tokenValue = Variant.Empty;

            switch (token.getType()) {
                case Comment:
                case Whitespace:
                    continue;
                case Keyword: {
                    var temp = token.getValue().toUpperCase();
                    if (temp.equals("TRUE")) {
                        tokenType = ExpressionTokenType.Constant;
                        tokenValue = Variant.fromBoolean(true);
                    } else if (temp.equals("FALSE")) {
                        tokenType = ExpressionTokenType.Constant;
                        tokenValue = Variant.fromBoolean(false);
                    } else {
                        for (var index = 0; index < this.Operators.length; index++) {
                            if (temp.equals(this.Operators[index])) {
                                tokenType = this.OperatorTypes[index];
                                break;
                            }
                        }
                    }
                    break;
                }
                case Word: {
                    tokenType = ExpressionTokenType.Variable;
                    tokenValue = Variant.fromString(token.getValue());
                    break;
                }
                case Integer: {
                    tokenType = ExpressionTokenType.Constant;
                    tokenValue = Variant.fromInteger(IntegerConverter.toInteger(token.getValue()));
                    break;
                }
                case Float: {
                    tokenType = ExpressionTokenType.Constant;
                    tokenValue = Variant.fromFloat(FloatConverter.toFloat(token.getValue()));
                    break;
                }
                case Quoted: {
                    tokenType = ExpressionTokenType.Constant;
                    tokenValue = Variant.fromString(token.getValue());
                    break;
                }
                case Symbol: {
                    var temp = token.getValue().toUpperCase();
                    for (var i = 0; i < this.Operators.length; i++) {
                        if (temp.equals(this.Operators[i])) {
                            tokenType = this.OperatorTypes[i];
                            break;
                        }
                    }
                    break;
                }
            }
            if (tokenType == ExpressionTokenType.Unknown)
                throw new SyntaxException(null, SyntaxErrorCode.UNKNOWN_SYMBOL, "Unknown symbol " + token.getValue(), token.getLine(), token.getColumn());

            this._initialTokens.add(new ExpressionToken(tokenType, tokenValue, token.getLine(), token.getColumn()));
        }
    }

    /**
     * Performs a syntax analysis at level 0.
     */
    private void performSyntaxAnalysis() throws SyntaxException {
        this.checkForMoreTokens();
        this.performSyntaxAnalysisAtLevel1();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            if (token != null && (token.getType() == ExpressionTokenType.And
                    || token.getType() == ExpressionTokenType.Or
                    || token.getType() == ExpressionTokenType.Xor)) {
                this.moveToNextToken();
                this.performSyntaxAnalysisAtLevel1();
                this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
                continue;
            }
            break;
        }
    }

    /**
     * Performs a syntax analysis at level 1.
     */
    private void performSyntaxAnalysisAtLevel1() throws SyntaxException {
        this.checkForMoreTokens();
        var token = this.getCurrentToken();
        if (token != null && token.getType() == ExpressionTokenType.Not) {
            this.moveToNextToken();
            this.performSyntaxAnalysisAtLevel2();
            this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
        } else {
            this.performSyntaxAnalysisAtLevel2();
        }
    }

    /**
     * Performs a syntax analysis at level 2.
     */
    private void performSyntaxAnalysisAtLevel2() throws SyntaxException {
        this.checkForMoreTokens();
        this.performSyntaxAnalysisAtLevel3();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            if (token != null && (token.getType() == ExpressionTokenType.Equal
                    || token.getType() == ExpressionTokenType.NotEqual
                    || token.getType() == ExpressionTokenType.More
                    || token.getType() == ExpressionTokenType.Less
                    || token.getType() == ExpressionTokenType.EqualMore
                    || token.getType() == ExpressionTokenType.EqualLess)) {
                this.moveToNextToken();
                this.performSyntaxAnalysisAtLevel3();
                this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
                continue;
            }
            break;
        }
    }

    /**
     * Performs a syntax analysis at level 3.
     */
    private void performSyntaxAnalysisAtLevel3() throws SyntaxException {
        this.checkForMoreTokens();
        this.performSyntaxAnalysisAtLevel4();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            if (token != null) {
                if (token.getType() == ExpressionTokenType.Plus
                        || token.getType() == ExpressionTokenType.Minus
                        || token.getType() == ExpressionTokenType.Like) {
                    this.moveToNextToken();
                    this.performSyntaxAnalysisAtLevel4();
                    this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
                } else if (this.matchTokensWithTypes(ExpressionTokenType.Not, ExpressionTokenType.Like)) {
                    this.performSyntaxAnalysisAtLevel4();
                    this.addTokenToResult(ExpressionTokenType.NotLike, Variant.Empty, token.getLine(), token.getColumn());
                } else if (this.matchTokensWithTypes(ExpressionTokenType.Is, ExpressionTokenType.Null)) {
                    this.addTokenToResult(ExpressionTokenType.IsNull, Variant.Empty, token.getLine(), token.getColumn());
                } else if (this.matchTokensWithTypes(ExpressionTokenType.Is, ExpressionTokenType.Not,
                        ExpressionTokenType.Null)) {
                    this.addTokenToResult(ExpressionTokenType.IsNotNull, Variant.Empty, token.getLine(), token.getColumn());
                } else if (this.matchTokensWithTypes(ExpressionTokenType.Not, ExpressionTokenType.In)) {
                    this.performSyntaxAnalysisAtLevel4();
                    this.addTokenToResult(ExpressionTokenType.NotIn, Variant.Empty, token.getLine(), token.getColumn());
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Performs a syntax analysis at level 4.
     */
    private void performSyntaxAnalysisAtLevel4() throws SyntaxException {
        this.checkForMoreTokens();
        this.performSyntaxAnalysisAtLevel5();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            if (token != null && (token.getType() == ExpressionTokenType.Star
                    || token.getType() == ExpressionTokenType.Slash
                    || token.getType() == ExpressionTokenType.Procent)) {
                this.moveToNextToken();
                this.performSyntaxAnalysisAtLevel5();
                this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
                continue;
            }
            break;
        }
    }

    /**
     * Performs a syntax analysis at level 5.
     */
    private void performSyntaxAnalysisAtLevel5() throws SyntaxException {
        this.checkForMoreTokens();
        this.performSyntaxAnalysisAtLevel6();
        while (this.hasMoreTokens()) {
            var token = this.getCurrentToken();
            if (token != null && (token.getType() == ExpressionTokenType.Power
                    || token.getType() == ExpressionTokenType.In
                    || token.getType() == ExpressionTokenType.ShiftLeft
                    || token.getType() == ExpressionTokenType.ShiftRight)) {
                this.moveToNextToken();
                this.performSyntaxAnalysisAtLevel6();
                this.addTokenToResult(token.getType(), Variant.Empty, token.getLine(), token.getColumn());
                continue;
            }
            break;
        }
    }

    /**
     * Performs a syntax analysis at level 6.
     */
    private void performSyntaxAnalysisAtLevel6() throws SyntaxException {
        this.checkForMoreTokens();
        // Process unary '+' or '-'.
        var unaryToken = this.getCurrentToken();
        if (unaryToken != null && unaryToken.getType() == ExpressionTokenType.Plus) {
            unaryToken = null;
            this.moveToNextToken();
        } else if (unaryToken != null && unaryToken.getType() == ExpressionTokenType.Minus) {
            unaryToken = new ExpressionToken(
                    ExpressionTokenType.Unary,
                    unaryToken.getValue(),
                    unaryToken.getLine(),
                    unaryToken.getColumn()
            );
            this.moveToNextToken();
        } else {
            unaryToken = null;
        }

        this.checkForMoreTokens();

        // Identify function calls.
        var primitiveToken = this.getCurrentToken();
        var nextToken = this.getNextToken();
        if (primitiveToken.getType() == ExpressionTokenType.Variable
                && nextToken != null && nextToken.getType() == ExpressionTokenType.LeftBrace) {
            primitiveToken = new ExpressionToken(
                    ExpressionTokenType.Function,
                    primitiveToken.getValue(),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn()
            );
        }

        if (primitiveToken.getType() == ExpressionTokenType.Constant) {
            this.moveToNextToken();
            this.addTokenToResult(
                    primitiveToken.getType(),
                    primitiveToken.getValue(),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn()
            );
        } else if (primitiveToken.getType() == ExpressionTokenType.Variable) {
            this.moveToNextToken();

            var temp = primitiveToken.getValue().getAsString();
            if (!this._variableNames.contains(temp))
                this._variableNames.add(temp);

            this.addTokenToResult(
                    primitiveToken.getType(),
                    primitiveToken.getValue(),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn()
            );
        } else if (primitiveToken.getType() == ExpressionTokenType.LeftBrace) {
            this.moveToNextToken();
            this.performSyntaxAnalysis();
            this.checkForMoreTokens();
            primitiveToken = this.getCurrentToken();
            if (primitiveToken.getType() != ExpressionTokenType.RightBrace)
                throw new SyntaxException(
                        null,
                        SyntaxErrorCode.MISSED_CLOSE_PARENTHESIS, "Expected ')' was not found",
                        primitiveToken.getLine(),
                        primitiveToken.getColumn()
                );

            this.moveToNextToken();
        } else if (primitiveToken.getType() == ExpressionTokenType.Function) {
            this.moveToNextToken();
            var token = this.getCurrentToken();
            if (token.getType() != ExpressionTokenType.LeftBrace)
                throw new SyntaxException(
                        null,
                        SyntaxErrorCode.INTERNAL, "Internal error",
                        token.getLine(),
                        token.getColumn()
                );

            var paramCount = 0;
            do {
                this.moveToNextToken();
                token = this.getCurrentToken();
                if (token == null || token.getType() == ExpressionTokenType.RightBrace) {
                    break;
                }
                paramCount++;
                this.performSyntaxAnalysis();
                token = this.getCurrentToken();
            } while (token != null && token.getType() == ExpressionTokenType.Comma);

            this.checkForMoreTokens();

            if (token != null && token.getType() != ExpressionTokenType.RightBrace)
                throw new SyntaxException(
                        null,
                        SyntaxErrorCode.MISSED_CLOSE_PARENTHESIS, "Expected ')' was not found",
                        token.getLine(),
                        token.getColumn()
                );

            this.moveToNextToken();

            this.addTokenToResult(
                    ExpressionTokenType.Constant,
                    new Variant(paramCount),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn()
            );

            this.addTokenToResult(
                    primitiveToken.getType(),
                    primitiveToken.getValue(),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn()
            );
        } else {
            throw new SyntaxException(
                    null,
                    SyntaxErrorCode.ERROR_AT, "Syntax error at " + primitiveToken.getValue(),
                    primitiveToken.getLine(),
                    primitiveToken.getColumn());
        }

        if (unaryToken != null)
            this.addTokenToResult(unaryToken.getType(), Variant.Empty, unaryToken.getLine(), unaryToken.getColumn());


        // Process [] operator.
        if (this.hasMoreTokens()) {
            primitiveToken = this.getCurrentToken();
            if (primitiveToken.getType() == ExpressionTokenType.LeftSquareBrace) {
                this.moveToNextToken();
                this.performSyntaxAnalysis();
                this.checkForMoreTokens();
                primitiveToken = this.getCurrentToken();
                if (primitiveToken.getType() != ExpressionTokenType.RightSquareBrace) {
                    throw new SyntaxException(
                            null,
                            SyntaxErrorCode.MISSED_CLOSE_SQUARE_BRACKET, "Expected ']' was not found",
                            primitiveToken.getLine(),
                            primitiveToken.getColumn());
                }
                this.moveToNextToken();
                this.addTokenToResult(
                        ExpressionTokenType.Element,
                        Variant.Empty, primitiveToken.getLine(),
                        primitiveToken.getColumn()
                );
            }
        }
    }

}
