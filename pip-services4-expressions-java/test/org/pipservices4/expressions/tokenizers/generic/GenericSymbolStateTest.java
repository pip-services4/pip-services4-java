package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;
import static org.junit.Assert.*;

import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

public class GenericSymbolStateTest {
    @Test
    public void testNextToken() throws Exception {
        var state = new GenericSymbolState();
        state.add("<", TokenType.Symbol);
        state.add("<<", TokenType.Symbol);
        state.add("<>", TokenType.Symbol);

        var scanner = new StringScanner("<A<<<>");

        var token = state.nextToken(scanner, null);
        assertEquals("<", token.getValue());
        assertEquals(TokenType.Symbol, token.getType());

        token = state.nextToken(scanner, null);
        assertEquals("A", token.getValue());
        assertEquals(TokenType.Symbol, token.getType());

        token = state.nextToken(scanner, null);
        assertEquals("<<", token.getValue());
        assertEquals(TokenType.Symbol, token.getType());

        token = state.nextToken(scanner, null);
        assertEquals("<>", token.getValue());
        assertEquals(TokenType.Symbol, token.getType());
    }
}
