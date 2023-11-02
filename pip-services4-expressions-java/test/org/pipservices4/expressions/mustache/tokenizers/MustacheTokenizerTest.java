package org.pipservices4.expressions.mustache.tokenizers;

import org.junit.Test;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.TokenizerFixture;

import java.util.List;

public class MustacheTokenizerTest {

    public MustacheTokenizerTest() throws Exception {
    }

    @Test
    public void testTemplate1() throws Exception {
        var tokenString = "Hello, {{ Name }}!";
        var expectedTokens = List.of(
                new Token(TokenType.Special, "Hello, ", 0, 0),
                new Token(TokenType.Symbol, "{{", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Word, "Name", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Symbol, "}}", 0, 0),
                new Token(TokenType.Special, "!", 0, 0)
        );

        MustacheTokenizer tokenizer = new MustacheTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testTemplate2() throws Exception {
        var tokenString = "Hello, {{{ Name }}}!";
        var expectedTokens = List.of(
                new Token(TokenType.Special, "Hello, ", 0, 0),
                new Token(TokenType.Symbol, "{{{", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Word, "Name", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Symbol, "}}}", 0, 0),
                new Token(TokenType.Special, "!", 0, 0)
        );

        var tokenizer = new MustacheTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testTemplate3() throws Exception {
        var tokenString = "{{ Name }}}";
        var expectedTokens = List.of(
                new Token(TokenType.Symbol, "{{", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Word, "Name", 0, 0),
                new Token(TokenType.Whitespace, " ", 0, 0),
                new Token(TokenType.Symbol, "}}}", 0, 0)
        );

        var tokenizer = new MustacheTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testTemplate4() throws Exception {
        var tokenString = "Hello, World!";
        var expectedTokens = List.of(new Token(TokenType.Special, "Hello, World!", 0, 0));

        var tokenizer = new MustacheTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }


}
