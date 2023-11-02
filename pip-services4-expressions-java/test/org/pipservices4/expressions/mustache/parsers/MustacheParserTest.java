package org.pipservices4.expressions.mustache.parsers;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;


public class MustacheParserTest {

    @Test
    public void testLexicalAnalysis() throws Exception {
        var parser = new MustacheParser();
        parser.setTemplate("Hello, {{{NAME}}}{{ #if ESCLAMATION }}!{{/if}}{{{^ESCLAMATION}}}.{{{/ESCLAMATION}}}");
        var expectedTokens = List.of(
                new MustacheToken(MustacheTokenType.Value, "Hello, ", 0, 0),
                new MustacheToken(MustacheTokenType.EscapedVariable, "NAME", 0, 0),
                new MustacheToken(MustacheTokenType.Section, "ESCLAMATION", 0, 0),
                new MustacheToken(MustacheTokenType.Value, "!", 0, 0),
                new MustacheToken(MustacheTokenType.SectionEnd, null, 0, 0),
                new MustacheToken(MustacheTokenType.InvertedSection, "ESCLAMATION", 0, 0),
                new MustacheToken(MustacheTokenType.Value, ".", 0, 0),
                new MustacheToken(MustacheTokenType.SectionEnd, "ESCLAMATION", 0, 0)
        );

        var tokens = parser.getInitialTokens();
        assertEquals(expectedTokens.size(), tokens.size());

        for (var i = 0; i < tokens.size(); i++) {
            assertEquals(expectedTokens.get(i).getType(), tokens.get(i).getType());
            assertEquals(expectedTokens.get(i).getValue(), tokens.get(i).getValue());
        }
    }

    @Test
    public void testSyntaxAnalysis() throws Exception {
        var parser = new MustacheParser();
        parser.setTemplate("Hello, {{{NAME}}}{{ #if ESCLAMATION }}!{{/if}}{{{^ESCLAMATION}}}.{{{/ESCLAMATION}}}");
        var expectedTokens = List.of(
                new MustacheToken(MustacheTokenType.Value, "Hello, ", 0, 0),
                new MustacheToken(MustacheTokenType.EscapedVariable, "NAME", 0, 0),
                new MustacheToken(MustacheTokenType.Section, "ESCLAMATION", 0, 0),
                new MustacheToken(MustacheTokenType.InvertedSection, "ESCLAMATION", 0, 0)
        );

        var tokens = parser.getResultTokens();

        assertEquals(expectedTokens.size(), tokens.size());

        for (var i = 0; i < tokens.size(); i++) {
            assertEquals(expectedTokens.get(i).getType(), tokens.get(i).getType());
            assertEquals(expectedTokens.get(i).getValue(), tokens.get(i).getValue());
        }
    }


    @Test
    public void testVariableNames() throws Exception {
        var parser = new MustacheParser();
        parser.setTemplate("Hello, {{{NAME}}}{{ #if ESCLAMATION }}!{{/if}}{{{^ESCLAMATION}}}.{{{/ESCLAMATION}}}");
        assertEquals(2, parser.getVariableNames().size());
        assertEquals("NAME", parser.getVariableNames().get(0));
        assertEquals("ESCLAMATION", parser.getVariableNames().get(1));
    }
}
