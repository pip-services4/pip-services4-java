package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;

import java.util.Objects;

/**
 * This class is a special case of a <code>SymbolNode</code>. A <code>SymbolRootNode</code>
 * object has no symbol of its own, but has children that represent all possible symbols.
 */
public class SymbolRootNode extends SymbolNode {
    /**
     * Creates and initializes a root node.
     */
    public SymbolRootNode() {
        super(null, 0);
    }

    /**
     * Add the given string as a symbol.
     *
     * @param value     The character sequence to add.
     * @param tokenType
     */
    public void add(String value, TokenType tokenType) throws Exception {
        if (Objects.equals(value, "")) {
            throw new Exception("Value must have at least 1 character");
        }
        var childNode = this.ensureChildWithChar(value.codePointAt(0));
        if (childNode.getTokenType() == TokenType.Unknown) {
            childNode.setValid(true);
            childNode.setTokenType(TokenType.Symbol);
        }
        childNode.addDescendantLine(value.substring(1), tokenType);
    }

    /**
     * Return a symbol string from a scanner.
     *
     * @param scanner A scanner to read from
     * @return A symbol string from a scanner
     */
    public Token nextToken(IScanner scanner) {
        var nextSymbol = scanner.read();
        var line = scanner.line();
        var column = scanner.column();

        var childNode = this.findChildWithChar(nextSymbol);
        if (childNode != null) {
            childNode = childNode.deepestRead(scanner);
            childNode = childNode.unreadToValid(scanner);
            return new Token(childNode.getTokenType(), childNode.ancestry(), line, column);
        } else {
            return new Token(TokenType.Symbol, String.valueOf((char)nextSymbol), line, column);
        }
    }
}
