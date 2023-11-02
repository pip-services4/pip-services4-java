package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;

import static org.junit.Assert.*;

import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

public class CppCommentStateTest {

    @Test
    public void testNextToken() throws Exception {
        var state = new CppCommentState();

        var scanner = new StringScanner("-- Comment \n Comment ");
        var failed = false;
        try {
            state.nextToken(scanner, null);
        } catch (Exception ex) {
            failed = true;
        }
        assertTrue(failed);

        scanner = new StringScanner("// Comment \n Comment ");
        var token = state.nextToken(scanner, null);
        assertEquals("// Comment ", token.getValue());
        assertEquals(TokenType.Comment, token.getType());

        scanner = new StringScanner("/* Comment \n Comment */#");
        token = state.nextToken(scanner, null);
        assertEquals("/* Comment \n Comment */", token.getValue());
        assertEquals(TokenType.Comment, token.getType());
    }
}
