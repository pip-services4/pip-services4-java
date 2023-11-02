package org.pipservices4.expressions.io;

public class StringScanner implements IScanner {
    public static final int EOF = -1;

    private final String _content;
    private int _position;
    private int _line;
    private int _column;

    /**
     * Creates an instance of this class.
     *
     * @param content A text content to be read.
     */
    public StringScanner(String content) {
        if (content == null)
            throw new NullPointerException("Content cannot be null");

        this._content = content;
        this._position = -1;
        this._line = 1;
        this._column = 0;
    }

    /**
     * Returns character from a specified position in the stream
     *
     * @param position a position to read character
     * @return a character from the specified position or EOF (-1)
     */
    private int charAt(int position) {
        if (position < 0 || position >= this._content.length())
            return StringScanner.EOF;

        return this._content.codePointAt(position);
    }

    /**
     * Checks if the current character represents a new line
     *
     * @param charBefore the character before the current one
     * @param charAt     the current character
     * @param charAfter  the character after the current one
     * @return <code>true</code> if the current character is a new line, or <code>false</code> otherwise.
     */
    private boolean isLine(int charBefore, int charAt, int charAfter) {
        if (charAt != 10 && charAt != 13)
            return false;

        return charAt != 13 || (charBefore != 10 && charAfter != 10);
    }

    /**
     * Checks if the current character represents a column
     *
     * @param charAt the current character
     * @return <code>true</code> if the current character is a column, or <code>false</code> otherwise.
     */
    private boolean isColumn(int charAt) {
        return charAt != 10 && charAt != 13;
    }

    /**
     * Gets the current line number
     *
     * @return The current line number in the stream
     */
    @Override
    public int line() {
        return this._line;
    }

    /**
     * Gets the column in the current line
     *
     * @return The column in the current line in the stream
     */
    @Override
    public int column() {
        return this._column;
    }

    /**
     * Reads character from the top of the stream.
     * A read character or <code>-1</code> if stream processed to the end.
     */
    @Override
    public int read() {
        // Skip if we are at the end
        if ((this._position + 1) > this._content.length())
            return StringScanner.EOF;

        // Update the current position
        this._position++;

        if (this._position >= this._content.length())
            return StringScanner.EOF;

        // Update line and columns
        var charBefore = this.charAt(this._position - 1);
        var charAt = this.charAt(this._position);
        var charAfter = this.charAt(this._position + 1);

        if (this.isLine(charBefore, charAt, charAfter)) {
            this._line++;
            this._column = 0;
        }
        if (this.isColumn(charAt))
            this._column++;


        return charAt;
    }

    /**
     * Returns the character from the top of the stream without moving the stream pointer.
     *
     * @return A character from the top of the stream or <code>-1</code> if stream is empty.
     */
    @Override
    public int peek() {
        return this.charAt(this._position + 1);
    }

    /**
     * Gets the next character line number
     *
     * @return The next character line number in the stream
     */
    @Override
    public int peekLine() {
        var charBefore = this.charAt(this._position);
        var charAt = this.charAt(this._position + 1);
        var charAfter = this.charAt(this._position + 2);

        return this.isLine(charBefore, charAt, charAfter) ? this._line + 1 : this._line;
    }

    /**
     * Gets the next character column number
     *
     * @return The next character column number in the stream
     */
    @Override
    public int peekColumn() {
        var charBefore = this.charAt(this._position);
        var charAt = this.charAt(this._position + 1);
        var charAfter = this.charAt(this._position + 2);

        if (this.isLine(charBefore, charAt, charAfter))
            return 0;

        return this.isColumn(charAt) ? this._column + 1 : this._column;
    }

    /**
     * Puts the one character back into the stream stream.
     */
    @Override
    public void unread() {
        // Skip if we are at the beginning
        if (this._position < -1)
            return;

        // Update the current position
        this._position--;

        // Update line and columns (optimization)
        if (this._column > 0) {
            this._column--;
            return;
        }

        // Update line and columns (full version)
        this._line = 1;
        this._column = 0;

        var charBefore = StringScanner.EOF;
        var charAt = StringScanner.EOF;
        var charAfter = this.charAt(0);

        for (var position = 0; position <= this._position; position++) {
            charBefore = charAt;
            charAt = charAfter;
            charAfter = this.charAt(position + 1);

            if (this.isLine(charBefore, charAt, charAfter)) {
                this._line++;
                this._column = 0;
            }
            if (this.isColumn(charAt))
                this._column++;

        }
    }

    /**
     * Pushes the specified number of characters to the top of the stream.
     *
     * @param count A number of characcted to be pushed back.
     */
    @Override
    public void unreadMany(int count) {
        while (count > 0) {
            this.unread();
            count--;
        }
    }

    /**
     * Resets scanner to the initial position
     */
    @Override
    public void reset() {
        this._position = -1;
        this._line = 1;
        this._column = 0;
    }
}
