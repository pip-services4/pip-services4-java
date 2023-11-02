package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

import static org.junit.Assert.*;

public class SymbolRootNodeTest {
    @Test
    public void testNextToken() throws Exception {
        var node = new SymbolRootNode();
        node.add("<", TokenType.Symbol);
        node.add("<<", TokenType.Symbol);
        node.add("<>", TokenType.Symbol);

        var scanner = new StringScanner("<A<<<>");

        var token = node.nextToken(scanner);
        assertEquals("<", token.getValue());

        token = node.nextToken(scanner);
        assertEquals("A", token.getValue());

        token = node.nextToken(scanner);
        assertEquals("<<", token.getValue());

        token = node.nextToken(scanner);
        assertEquals("<>", token.getValue());
    }
}
