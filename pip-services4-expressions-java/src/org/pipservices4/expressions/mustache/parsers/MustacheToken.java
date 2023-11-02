package org.pipservices4.expressions.mustache.parsers;

import java.util.ArrayList;
import java.util.List;

public class MustacheToken {
    private final MustacheTokenType _type;
    private final String _value;
    private final List<MustacheToken> _tokens = new ArrayList<>();
    private final int _line;
    private final int _column;

    /**
     * Creates an instance of a mustache token.
     *
     * @param type   a token type.
     * @param value  a token value.
     * @param line   a line number where the token is.
     * @param column a column numer where the token is.
     */
    public MustacheToken(MustacheTokenType type, String value, int line, int column) {
        this._type = type == null ? MustacheTokenType.Unknown : type;
        this._value = value;
        this._line = line;
        this._column = column;
    }

    /**
     * Gets the token type.
     */
    public MustacheTokenType getType() {
        return this._type;
    }

    /**
     * Gets the token value or variable name.
     */
    public String getValue() {
        return this._value;
    }

    /**
     * Gets a list of subtokens is this token a section.
     */
    public List<MustacheToken> getTokens() {
        return this._tokens;
    }

    /**
     * The line number where the token is.
     */
    public int getLine() {
        return this._line;
    }

    /**
     * The column number where the token is.
     */
    public int getColumn() {
        return this._column;
    }
}
