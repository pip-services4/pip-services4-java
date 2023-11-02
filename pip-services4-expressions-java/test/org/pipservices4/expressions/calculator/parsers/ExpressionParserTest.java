package org.pipservices4.expressions.calculator.parsers;

import org.pipservices4.expressions.variants.Variant;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ExpressionParserTest {
    @Test
    public void testParseString() throws Exception {
        var parser = new ExpressionParser();
        parser.setExpression("(2+2)*ABS(-2)");
        var expectedTokens = List.of(
                new ExpressionToken(ExpressionTokenType.Constant, Variant.fromInteger(2), 0, 0),
                new ExpressionToken(ExpressionTokenType.Constant, Variant.fromInteger(2), 0, 0),
                new ExpressionToken(ExpressionTokenType.Plus, Variant.Empty, 0, 0),
                new ExpressionToken(ExpressionTokenType.Constant, Variant.fromInteger(2), 0, 0),
                new ExpressionToken(ExpressionTokenType.Unary, Variant.Empty, 0, 0),
                new ExpressionToken(ExpressionTokenType.Constant, Variant.fromInteger(1), 0, 0),
                new ExpressionToken(ExpressionTokenType.Function, Variant.fromString("ABS"), 0, 0),
                new ExpressionToken(ExpressionTokenType.Star, Variant.Empty, 0, 0)
        );

        var tokens = parser.getResultTokens();
        assertEquals(expectedTokens.size(), tokens.size());

        for (var i = 0; i < tokens.size(); i++) {
            assertEquals(expectedTokens.get(i).getType(), tokens.get(i).getType());
            assertEquals(expectedTokens.get(i).getValue().getType(), tokens.get(i).getValue().getType());
            assertEquals(expectedTokens.get(i).getValue().getAsObject(), tokens.get(i).getValue().getAsObject());
        }
    }

    @Test
    public void testWrongExpression() throws Exception {
        var parser = new ExpressionParser();
        parser.setExpression("1 > 2");
        assertEquals("1 > 2", parser.getExpression());
    }
}
