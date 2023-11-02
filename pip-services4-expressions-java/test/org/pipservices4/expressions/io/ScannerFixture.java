package org.pipservices4.expressions.io;

import static junit.framework.TestCase.assertEquals;

public class ScannerFixture {
    private IScanner _scanner;
    private String _content;

    public ScannerFixture(IScanner scanner, String content) {
        this._scanner = scanner;
        this._content = content;
    }

    public void testRead() {
        this._scanner.reset();

        for (var i = 0; i < this._content.length(); i++) {
            var chr = this._scanner.read();
            assertEquals(this._content.codePointAt(i), chr);
        }

        var chr = this._scanner.read();
        assertEquals(-1, chr);

        chr = this._scanner.read();
        assertEquals(-1, chr);
    }

    public void testUnread() {
        this._scanner.reset();

        var chr = this._scanner.peek();
        assertEquals(this._content.codePointAt(0), chr);

        chr = this._scanner.read();
        assertEquals(this._content.codePointAt(0), chr);

        chr = this._scanner.read();
        assertEquals(this._content.codePointAt(1), chr);

        this._scanner.unread();
        chr = this._scanner.read();
        assertEquals(this._content.codePointAt(1), chr);

        this._scanner.unreadMany(2);
        chr = this._scanner.read();
        assertEquals(this._content.codePointAt(0), chr);
        chr = this._scanner.read();
        assertEquals(this._content.codePointAt(1), chr);
    }

    public void testLineColumn(int position, int charAt, int line, int column) {
        this._scanner.reset();

        // Get in position
        while (position > 1) {
            this._scanner.read();
            position--;
        }

        // Test forward scanning
        var chr = this._scanner.read();
        assertEquals(charAt, chr);
        var ln = this._scanner.line();
        assertEquals(line, ln);
        var col = this._scanner.column();
        assertEquals(column, col);

        // Moving backward
        chr = this._scanner.read();
        if (chr != -1) {
            this._scanner.unread();
        }
        this._scanner.unread();

        // Test backward scanning
        chr = this._scanner.read();
        assertEquals(charAt, chr);
        ln = this._scanner.line();
        assertEquals(line, ln);
        col = this._scanner.column();
        assertEquals(column, col);
    }
}
