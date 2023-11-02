package org.pipservices4.expressions.calculator.variables;

import org.pipservices4.expressions.variants.Variant;

public class Variable implements IVariable {
    private final String _name;
    private Variant _value;

    /**
     * Constructs this variable with name and value.
     *
     * @param name  The name of this variable.
     * @param value The variable value.
     */
    public Variable(String name, Variant value) {
        if (name == null)
            throw new NullPointerException("Name parameter cannot be null.");

        this._name = name;
        this._value = value != null ? value : new Variant();
    }

    public Variable(String name) {
        if (name == null)
            throw new NullPointerException("Name parameter cannot be null.");

        this._name = name;
        this._value = new Variant();
    }

    /**
     * The variable name.
     */
    @Override
    public String getName() {
        return this._name;
    }

    /**
     * The variable value.
     * Can't change name.
     */
    @Override
    public void setName(String value) {
        // can't change name
    }

    /**
     * The variable value.
     */
    @Override
    public Variant getValue() {
        return _value;
    }

    /**
     * The variable value.
     */
    @Override
    public void setValue(Variant value) {
        _value = value;
    }
}
