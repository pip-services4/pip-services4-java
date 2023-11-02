package org.pipservices4.expressions.tokenizers.generic;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

public class GenericQuoteStateTest {
    @Test
    public void testNextToken() {
        var state = new GenericQuoteState();

        var scanner = new StringScanner("'ABC#DEF'#");
        var token = state.nextToken(scanner, null);
        assertEquals("'ABC#DEF'", token.getValue());
        assertEquals(TokenType.Quoted, token.getType());

        scanner = new StringScanner("'ABC#DEF''");
        token = state.nextToken(scanner, null);
        assertEquals("'ABC#DEF'", token.getValue());
        assertEquals(TokenType.Quoted, token.getType());
    }

    @Test
    public void testEncodeAndDecode() {
        var state = new GenericQuoteState();

        var value = state.encodeString("ABC", "'".codePointAt(0));
        assertEquals("'ABC'", value);

        value = state.decodeString(value, "'".codePointAt(0));
        assertEquals("ABC", value);

        value = state.decodeString("'ABC'DEF'", "'".codePointAt(0));
        assertEquals("ABC'DEF", value);
    }
}
