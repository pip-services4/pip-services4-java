package org.pipservices4.expressions.tokenizers.generic;

import org.pipservices4.expressions.io.IScanner;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.utilities.CharReferenceMap;
import org.pipservices4.expressions.tokenizers.utilities.CharValidator;

public class SymbolNode {
    private final SymbolNode _parent;
    private final int _character;
    private CharReferenceMap<SymbolNode> _children;
    private TokenType _tokenType = TokenType.Unknown;
    private boolean _valid;
    private String _ancestry;

    /**
     * Constructs a SymbolNode with the given parent, representing the given character.
     *
     * @param parent    This node's parent
     * @param character This node's associated character.
     */
    public SymbolNode(SymbolNode parent, int character) {
        this._parent = parent;
        this._character = character;
    }

    /**
     * Find or create a child for the given character.
     *
     * @param value
     */
    public SymbolNode ensureChildWithChar(int value) throws Exception {
        if (this._children == null)
            this._children = new CharReferenceMap<>();


        var childNode = this._children.lookup(value);
        if (childNode == null) {
            childNode = new SymbolNode(this, value);
            this._children.addInterval(value, value, childNode);
        }
        return childNode;
    }

    /**
     * Add a line of descendants that represent the characters in the given string.
     *
     * @param value
     * @param tokenType
     */
    public void addDescendantLine(String value, TokenType tokenType) throws Exception {
        if (!value.isEmpty()) {
            var childNode = this.ensureChildWithChar(value.codePointAt(0));
            childNode.addDescendantLine(value.substring(1), tokenType);
        } else {
            this._valid = true;
            this._tokenType = tokenType;
        }
    }

    /**
     * Find the descendant that takes as many characters as possible from the input.
     *
     * @param scanner
     */
    public SymbolNode deepestRead(IScanner scanner) {
        var nextSymbol = scanner.read();
        var childNode = !CharValidator.isEof(nextSymbol)
                ? this.findChildWithChar(nextSymbol) : null;
        if (childNode == null) {
            scanner.unread();
            return this;
        }
        return childNode.deepestRead(scanner);
    }

    /**
     * Find a child with the given character.
     *
     * @param value
     */
    public SymbolNode findChildWithChar(int value) {
        return this._children != null ? this._children.lookup(value) : null;
    }

    /**
     * Unwind to a valid node; this node is "valid" if its ancestry represents a complete symbol.
     * If this node is not valid, put back the character and ask the parent to unwind.
     *
     * @param scanner
     */
    public SymbolNode unreadToValid(IScanner scanner) {
        if (!this._valid && this._parent != null) {
            scanner.unread();
            return this._parent.unreadToValid(scanner);
        }
        return this;
    }

    public boolean getValid() {
        return this._valid;
    }

    public void setValid(boolean value) {
        this._valid = value;
    }

    public TokenType getTokenType() {
        return this._tokenType;
    }

    public void setTokenType(TokenType value) {
        this._tokenType = value;
    }

    /**
     * Show the symbol this node represents.
     *
     * @return The symbol this node represents.
     */
    public String ancestry() {
        if (this._ancestry == null) {
            this._ancestry = (this._parent != null ? this._parent.ancestry() : "")
                    + (this._character != 0 ? String.valueOf((char)this._character) : "");
        }
        return this._ancestry;
    }
}
