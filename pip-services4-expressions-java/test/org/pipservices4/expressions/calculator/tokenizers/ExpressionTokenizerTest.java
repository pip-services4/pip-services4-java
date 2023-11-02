package org.pipservices4.expressions.calculator.tokenizers;

import org.junit.Test;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.TokenizerFixture;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ExpressionTokenizerTest {
    @Test
    public void testQuoteToken() throws Exception {
        var tokenString = "A'xyz'\"abc\ndeg\" 'jkl\"def'\"ab\"\"de\"'df''er'";
        var expectedTokens = List.of(
                new Token(TokenType.Word, "A", 0, 0), new Token(TokenType.Quoted, "xyz", 0, 0),
                new Token(TokenType.Word, "abc\ndeg", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Quoted, "jkl\"def", 0, 0), new Token(TokenType.Word, "ab\"de", 0, 0),
                new Token(TokenType.Quoted, "df'er", 0, 0)
        );


        var tokenizer = new ExpressionTokenizer();
        tokenizer.setSkipEof(true);
        tokenizer.setDecodeStrings(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testWordToken() throws Exception {
        var tokenString = "A'xyz'Ebf_2\n2_2";
        var expectedTokens = List.of(
                new Token(TokenType.Word, "A", 0, 0), new Token(TokenType.Quoted, "xyz", 0, 0),
                new Token(TokenType.Word, "Ebf_2", 0, 0), new Token(TokenType.Whitespace, "\n", 0, 0),
                new Token(TokenType.Integer, "2", 0, 0), new Token(TokenType.Word, "_2", 0, 0)
        );

        var tokenizer = new ExpressionTokenizer();
        tokenizer.setSkipEof(true);
        tokenizer.setDecodeStrings(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testNumberToken() throws Exception {
        var tokenString = "123-321 .543-.76-. 123.456 123e45 543.11E+43 1e 3E-";
        var expectedTokens = List.of(
                new Token(TokenType.Integer, "123", 0, 0), new Token(TokenType.Symbol, "-", 0, 0),
                new Token(TokenType.Integer, "321", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, ".543", 0, 0), new Token(TokenType.Symbol, "-", 0, 0),
                new Token(TokenType.Float, ".76", 0, 0), new Token(TokenType.Symbol, "-", 0, 0),
                new Token(TokenType.Symbol, ".", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, "123.456", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, "123e45", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Float, "543.11E+43", 0, 0), new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Integer, "1", 0, 0), new Token(TokenType.Word, "e", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0), new Token(TokenType.Integer, "3", 0, 0),
                new Token(TokenType.Word, "E", 0, 0), new Token(TokenType.Symbol, "-", 0, 0)
        );

        var tokenizer = new ExpressionTokenizer();
        tokenizer.setSkipEof(true);
        tokenizer.setDecodeStrings(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testExpressionToken() throws Exception {
        var tokenString = "A + b / (3 - Max(-123, 1)*2)";

        var tokenizer = new ExpressionTokenizer();
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        assertEquals(25, tokenList.size());
    }
}
