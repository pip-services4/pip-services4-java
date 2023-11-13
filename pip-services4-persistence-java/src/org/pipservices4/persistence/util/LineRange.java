package org.pipservices4.persistence.util;

/* The LineRange class defines a range of lines in a document with a starting and ending line number. */
public class LineRange {
    private long _lineStart;
    private long _lineEnd;

    public long get_lineStart() {
        return _lineStart;
    }

    public void set_lineStart(long _lineStart) {
        this._lineStart = _lineStart;
    }    
    
    public long get_lineEnd() {
        return _lineEnd;
    }

    public void set_lineEnd(long _lineEnd) {
        this._lineEnd = _lineEnd;
    }

    public LineRange(long _lineStart, long _lineEnd) {
        this._lineStart = _lineStart;
        this._lineEnd = _lineEnd;
    }
}
