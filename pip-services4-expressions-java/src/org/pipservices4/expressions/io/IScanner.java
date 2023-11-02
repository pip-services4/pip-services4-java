package org.pipservices4.expressions.io;

/**
 * Defines scanner that can read and unread characters and count lines.
 * This scanner is used by tokenizers to process input streams.
 */
public interface IScanner {
    /**
     * Reads character from the top of the stream.
     * @return A read character or <code>-1</code> if stream processed to the end.
     */
    int read();

    /**
     * Gets the current line number
     * @return The current line number in the stream
     */
    int line();

    /**
     * Gets the column in the current line
     * @return The column in the current line in the stream
     */
    int column();

    /**
     * Returns the character from the top of the stream without moving the stream pointer.
     * @return A character from the top of the stream or <code>-1</code> if stream is empty.
     */
    int peek();

    /**
     * Gets the next character line number
     * @return The next character line number in the stream
     */
    int peekLine();

    /**
     * Gets the next character column number
     * @return The next character column number in the stream
     */
    int peekColumn();

    /**
     * Puts the one character back into the stream stream.
     */
    void unread();

    /**
     * Pushes the specified number of characters to the top of the stream.
     * @param count A number of characcted to be pushed back.
     */
    void unreadMany(int count);

    /**
     * Resets scanner to the initial position
     */
    void reset();
}
