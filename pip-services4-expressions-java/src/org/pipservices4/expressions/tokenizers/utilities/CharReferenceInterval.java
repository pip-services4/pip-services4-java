package org.pipservices4.expressions.tokenizers.utilities;

/**
 * Represents a character interval that keeps a reference.
 * This class is internal and used by {@link CharReferenceMap}.
 */
public class CharReferenceInterval<T> {
    private final int _start;
    private final int _end;
    private final T _reference;

    public CharReferenceInterval(int start, int end, T reference) throws Exception {
        if (start > end)
            throw new Exception("Start must be less or equal End");

        this._start = start;
        this._end = end;
        this._reference = reference;
    }

    public int getStart() {
        return this._start;
    }

    public int getEnd() {
        return this._end;
    }

    public T getReference() {
        return this._reference;
    }

    public boolean inRange(int symbol) {
        return symbol >= this._start && symbol <= this._end;
    }
}
