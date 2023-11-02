package org.pipservices4.expressions.tokenizers.generic;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.expressions.io.StringScanner;
import org.pipservices4.expressions.tokenizers.TokenType;

public class GenericCommentStateTest {
    @Test
    public void testNextToken() throws Exception {
        var state = new GenericCommentState();

        var scanner = new StringScanner("# Comment \r# Comment ");
        var token = state.nextToken(scanner, null);
        assertEquals("# Comment ", token.getValue());
        assertEquals(TokenType.Comment, token.getType());

        scanner = new StringScanner("# Comment \n# Comment ");
        token = state.nextToken(scanner, null);
        assertEquals("# Comment ", token.getValue());
        assertEquals(TokenType.Comment, token.getType());
    }
}
