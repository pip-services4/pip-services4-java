package org.pipservices4.expressions.calculator.parsers;

import org.pipservices4.expressions.variants.Variant;

/**
 * Defines an expression token holder.
 */
public class ExpressionToken {
    private final ExpressionTokenType _type;
    private final Variant _value;
    private final int _line;
    private final int _column;

    /**
     * Creates an instance of this token and initializes it with specified values.
     *
     * @param type   The type of this token.
     * @param value  The value of this token.
     * @param line   the line number where the token is.
     * @param column the column number where the token is.
     */
    public ExpressionToken(ExpressionTokenType type, Variant value, int line, int column) {
        this._type = type;
        this._value = value;
        this._line = line;
        this._column = column;
    }

    /**
     * The type of this token.
     */
    public ExpressionTokenType getType() {
        return this._type;
    }

    /**
     * The value of this token.
     */
    public Variant getValue() {
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
}
