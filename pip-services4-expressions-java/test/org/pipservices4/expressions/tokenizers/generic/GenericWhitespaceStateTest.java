package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

import static org.junit.Assert.*;

public class GenericWhitespaceStateTest {
    @Test
    public void testNextToken() throws Exception {
        var state = new GenericWhitespaceState();

        var scanner = new StringScanner(" \t\n\r #");
        var token = state.nextToken(scanner, null);
        assertEquals(" \t\n\r ", token.getValue());
        assertEquals(TokenType.Whitespace, token.getType());
    }
}
