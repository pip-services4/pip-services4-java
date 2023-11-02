package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

import static org.junit.Assert.*;

public class GenericWordStateTest {
    @Test
    public void testNextToken() throws Exception {
        var state = new GenericWordState();

        var scanner = new StringScanner("AB_CD=");
        var token = state.nextToken(scanner, null);
        assertEquals("AB_CD", token.getValue());
        assertEquals(TokenType.Word, token.getType());
    }
}
