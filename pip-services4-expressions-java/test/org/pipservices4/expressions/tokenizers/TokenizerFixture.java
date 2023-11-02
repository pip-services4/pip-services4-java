package org.pipservices4.expressions.tokenizers;

import java.util.List;

import static org.junit.Assert.*;

public class TokenizerFixture {
    public static void assertAreEqualsTokenLists(
            List<Token> expectedTokens, List<Token> actualTokens) {
        assertEquals(expectedTokens.size(), actualTokens.size());

        for (var i = 0; i < expectedTokens.size(); i++) {
            assertEquals(expectedTokens.get(i).getType(), actualTokens.get(i).getType());
            assertEquals(expectedTokens.get(i).getValue(), actualTokens.get(i).getValue());
        }
    }
}
