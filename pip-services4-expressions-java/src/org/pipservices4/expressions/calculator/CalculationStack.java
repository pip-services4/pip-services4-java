package org.pipservices4.expressions.calculator;

import org.pipservices4.expressions.variants.Variant;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a stack of Variant values.
 */
public class CalculationStack {
    private final List<Variant> _values = new ArrayList<>();

    public int length() {
        return this._values.size();
    }

    public void push(Variant value) {
        this._values.add(value);
    }

    public Variant pop() {
        if (this._values.isEmpty())
            throw new IndexOutOfBoundsException("Stack is empty.");

        var result = this._values.get(this._values.size() - 1);
        this._values.remove(_values.size() - 1);
        return result;
    }

    public Variant peekAt(int index) {
        return this._values.get(index);
    }

    public Variant peek() {
        if (this._values.isEmpty())
            throw new IndexOutOfBoundsException("Stack is empty.");

        return this._values.get(this._values.size() - 1);
    }

}
