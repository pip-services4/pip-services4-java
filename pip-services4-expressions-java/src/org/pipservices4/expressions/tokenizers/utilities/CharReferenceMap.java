package org.pipservices4.expressions.tokenizers.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps references associated with specific characters
 */
public class CharReferenceMap<T> {
    private List<T> _initialInterval;
    private List<CharReferenceInterval<T>> _otherIntervals;

    public CharReferenceMap() {
        clear();
    }

    public void addDefaultInterval(T reference) throws Exception {
        this.addInterval(0x0000, 0xfffe, reference);
    }

    public void addInterval(int start, int end, T reference) throws Exception {
        if (start > end)
            throw new Exception("Start must be less or equal End");

        end = end == 0xffff ? 0xfffe : end;

        for (var index = start; index < 0x0100 && index <= end; index++)
            this._initialInterval.set(index, reference);

        if (end >= 0x0100) {
            start = Math.max(start, 0x0100);
            this._otherIntervals.set(0,
                    new CharReferenceInterval<>(start, end, reference));
        }
    }

    public void clear() {
        this._initialInterval = new ArrayList<>(0x0100 - 1);
        this._otherIntervals =  new ArrayList<>(0x0100 - 1);
        for (var index = 0; index < 0x0100; index++) {
            this._initialInterval.add(null);
            this._otherIntervals.add(null);
        }
    }

    public T lookup(int symbol) {
        if (symbol < 0x0100) {
            return this._initialInterval.get(symbol);
        } else {
            for (var interval : this._otherIntervals) {
                if (interval != null && interval.inRange(symbol))
                    return interval.getReference();
            }
            return null;
        }
    }
}
