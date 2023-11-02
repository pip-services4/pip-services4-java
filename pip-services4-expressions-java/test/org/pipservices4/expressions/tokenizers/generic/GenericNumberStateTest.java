package org.pipservices4.expressions.tokenizers.generic;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

public class GenericNumberStateTest {
    @Test
    public void testNextToken() throws Exception {
        var state = new GenericNumberState();

        var scanner = new StringScanner("ABC");
        var failed = false;
        try {
            state.nextToken(scanner, null);
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed);

        scanner = new StringScanner("123#");
        var token = state.nextToken(scanner, null);
        assertEquals("123", token.getValue());
        assertEquals(TokenType.Integer, token.getType());

        scanner = new StringScanner("-123#");
        token = state.nextToken(scanner, null);
        assertEquals("-123", token.getValue());
        assertEquals(TokenType.Integer, token.getType());

        scanner = new StringScanner("123.#");
        token = state.nextToken(scanner, null);
        assertEquals("123.", token.getValue());
        assertEquals(TokenType.Float, token.getType());

        scanner = new StringScanner("123.456#");
        token = state.nextToken(scanner, null);
        assertEquals("123.456", token.getValue());
        assertEquals(TokenType.Float, token.getType());

        scanner = new StringScanner("-123.456#");
        token = state.nextToken(scanner, null);
        assertEquals("-123.456", token.getValue());
        assertEquals(TokenType.Float, token.getType());
    }
}
