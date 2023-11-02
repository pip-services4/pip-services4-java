package org.pipservices4.expressions.tokenizers.generic;

import org.junit.Test;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.TokenizerFixture;

import java.util.List;

import static org.junit.Assert.*;

public class GenericTokenizerTest {
    @Test
    public void testExpression() throws Exception {
        var tokenString = "A+B/123 - \t 'xyz'\n <>-10.11# This is a comment";
        var expectedTokens = List.of(
                new Token(TokenType.Word, "A", 0, 0),
                new Token(TokenType.Symbol, "+", 0, 0),
                new Token(TokenType.Word, "B", 0, 0),
                new Token(TokenType.Symbol, "/", 0, 0),
                new Token(TokenType.Integer, "123", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Symbol, "-", 0, 0),
                new Token(TokenType.Whitespace, " \t ", 0, 0),
                new Token(TokenType.Quoted, "'xyz'", 0, 0),
                new Token(TokenType.Whitespace, "\n ", 0, 0),
                new Token(TokenType.Symbol, "<>", 0, 0),
                new Token(TokenType.Float, "-10.11", 0, 0),
                new Token(TokenType.Comment, "# This is a comment", 0, 0),
                new Token(TokenType.Eof, null, 0, 0)
        );

        var tokenizer = new GenericTokenizer();
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testWordToken() throws Exception {
        var tokenString = "A'xyz'Ebf_2\n2x_2";
        var expectedTokens = List.of(
                new Token(TokenType.Word, "A", 0, 0),
                new Token(TokenType.Quoted, "xyz", 0, 0),
                new Token(TokenType.Word, "Ebf_2", 0, 0),
                new Token(TokenType.Whitespace, "\n", 0, 0),
                new Token(TokenType.Integer, "2", 0, 0),
                new Token(TokenType.Word, "x_2", 0, 0)
        );

        var tokenizer = new GenericTokenizer();
        tokenizer.setSkipEof(true);

        tokenizer.setDecodeStrings(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testNumberToken() throws Exception {
        var tokenString = "123-321 .543-.76-. -123.456";
        var expectedTokens = List.of(
                new Token(TokenType.Integer, "123", 0, 0),
                new Token(TokenType.Integer, "-321", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, ".543", 0, 0),
                new Token(TokenType.Float, "-.76", 0, 0),
                new Token(TokenType.Symbol, "-", 0, 0),
                new Token(TokenType.Symbol, ".", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, "-123.456", 0, 0)
        );

        var tokenizer = new GenericTokenizer();
        tokenizer.setSkipEof(true);
        tokenizer.setDecodeStrings(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testExpressionToken() throws Exception {
        var tokenString = "A + b / (3 - Max(-123, 1)*2)";

        var tokenizer = new GenericTokenizer();
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        assertEquals(24, tokenList.size());
    }

    @Test
    public void testExpressionToken2() throws Exception {
        var tokenString = "1>2";
        var expectedTokens = List.of(
                new Token(TokenType.Integer, "1", 0, 0),
                new Token(TokenType.Symbol, ">", 0, 0),
                new Token(TokenType.Integer, "2", 0, 0)
        );

        var tokenizer = new GenericTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }
}
