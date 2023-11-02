package org.pipservices4.expressions.tokenizers;

import java.util.Objects;

/**
 * A token represents a logical chunk of a string. For example, a typical tokenizer would break
 * the string "1.23 &lt;= 12.3" into three tokens: the number 1.23, a less-than-or-equal symbol,
 * and the number 12.3. A token is a receptacle, and relies on a tokenizer to decide precisely how
 * to divide a string into tokens.
 */
public class Token {

    private final TokenType _type;
    private final String _value;
    private final int _line;
    private final int _column;

    public Token(TokenType type, String value, int line, int column) {
        this._type = type;
        this._value = value;
        this._line = line;
        this._column = column;
    }

    /**
     * The token type.
     */
    public TokenType getType() {
        return this._type;
    }

    /**
     * The token value.
     */
    public String getValue() {
        return this._value;
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

    public boolean equals(Object obj) {
        if (obj instanceof Token token) {
            return token._type == this._type && Objects.equals(token._value, this._value);
        }
        return false;
    }

}
