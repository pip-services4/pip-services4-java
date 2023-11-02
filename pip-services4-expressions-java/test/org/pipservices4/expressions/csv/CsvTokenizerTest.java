package org.pipservices4.expressions.csv;

import org.junit.Test;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.tokenizers.TokenType;
import org.pipservices4.expressions.tokenizers.TokenizerFixture;

import java.util.List;

public class CsvTokenizerTest {
    @Test
    public void testTokenizerWithDefaultParameters() throws Exception {
        var tokenString = "\n\r\"John \"\"Da Man\"\"\",Repici,120 Jefferson St.,Riverside, NJ,08075\r\n"
                + "Stephen,Tyler,\"7452 Terrace \"\"At the Plaza\"\" road\",SomeTown,SD, 91234\r"
                + ",Blankman,,SomeTown, SD, 00298\n";
        var expectedTokens = List.of(
                new Token(TokenType.Eol, "\n\r", 0, 0),
                new Token(TokenType.Quoted, "\"John \"\"Da Man\"\"\"", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "Repici", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "120 Jefferson St.", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "Riverside", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, " NJ", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "08075", 0, 0), new Token(TokenType.Eol, "\r\n", 0, 0),
                new Token(TokenType.Word, "Stephen", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "Tyler", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Quoted, "\"7452 Terrace \"\"At the Plaza\"\" road\"", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "SomeTown", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "SD", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, " 91234", 0, 0), new Token(TokenType.Eol, "\r", 0, 0),
                new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "Blankman", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, "SomeTown", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, " SD", 0, 0), new Token(TokenType.Symbol, ",", 0, 0),
                new Token(TokenType.Word, " 00298", 0, 0), new Token(TokenType.Eol, "\n", 0, 0)
        );

        var tokenizer = new CsvTokenizer();
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }

    @Test
    public void testTokenizerWithOverridenParameters() throws Exception {
        var tokenString = "\n\r\'John, \'\'Da Man\'\'\'\tRepici\t120 Jefferson St.\tRiverside\t NJ\t08075\r\n"
                + "Stephen\t\"Tyler\"\t\'7452 \t\nTerrace \'\'At the Plaza\'\' road\'\tSomeTown\tSD\t 91234\r"
                + "\tBlankman\t\tSomeTown \'xxx\t\'\t SD\t 00298\n";

        var expectedTokens = List.of(
                new Token(TokenType.Eol, "\n\r", 0, 0),
                new Token(TokenType.Quoted, "\'John, \'\'Da Man\'\'\'", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "Repici", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "120 Jefferson St.", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "Riverside", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, " NJ", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "08075", 0, 0), new Token(TokenType.Eol, "\r\n", 0, 0),
                new Token(TokenType.Word, "Stephen", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Quoted, "\"Tyler\"", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Quoted, "\'7452 \t\nTerrace \'\'At the Plaza\'\' road\'", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "SomeTown", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "SD", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, " 91234", 0, 0), new Token(TokenType.Eol, "\r", 0, 0),
                new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "Blankman", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, "SomeTown ", 0, 0), new Token(TokenType.Quoted, "\'xxx\t\'", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, " SD", 0, 0), new Token(TokenType.Symbol, "\t", 0, 0),
                new Token(TokenType.Word, " 00298", 0, 0), new Token(TokenType.Eol, "\n", 0, 0)
        );

        var tokenizer = new CsvTokenizer();
        tokenizer.setFieldSeparators(List.of((int) '\t'));
        tokenizer.setQuoteSymbols(List.of((int) '\'', (int) '\"'));
        tokenizer.setEndOfLine("\n");
        tokenizer.setSkipEof(true);
        var tokenList = tokenizer.tokenizeBuffer(tokenString);

        TokenizerFixture.assertAreEqualsTokenLists(expectedTokens, tokenList);
    }
}
