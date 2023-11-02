package org.pipservices4.expressions.calculator;

import org.junit.Test;
import org.pipservices4.expressions.variants.Variant;
import org.pipservices4.expressions.variants.VariantType;

import static org.junit.Assert.*;

public class ExpressionCalculatorTest {

    @Test
    public void testSimpleExpression() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("2 + 2");
        var result = calculator.evaluate();
        assertEquals(VariantType.Integer, result.getType());
        assertEquals(4, (int) result.getAsInteger());
    }

    @Test
    public void testFunctionExpression() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("A + b / (3 - Max(-123, 1)*2)");
        // calculator.expression = "Abs(1)";
        assertEquals("A", calculator.getDefaultVariables().findByName("a").getName());
        assertEquals("b", calculator.getDefaultVariables().findByName("b").getName());
        calculator.getDefaultVariables().findByName("a").setValue(new Variant("xyz"));
        calculator.getDefaultVariables().findByName("b").setValue(new Variant(123));

        var result = calculator.evaluate();
        assertEquals(VariantType.String, result.getType());
        assertEquals("xyz123", result.getAsString());
    }

    @Test
    public void testArrayExpression() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("'abc'[1]");
        var result = calculator.evaluate();
        assertEquals(VariantType.String, result.getType());
        assertEquals("b", result.getAsString());
    }

    @Test
    public void testBooleanExpression() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("1 > 2");
        var result = calculator.evaluate();
        assertEquals(VariantType.Boolean, result.getType());
        assertFalse(result.getAsBoolean());
    }

    @Test
    public void testUnknownFunction() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("XXX(1)");
        try {
            calculator.evaluate();
            fail("Expected exception on unknown function");
        } catch (Exception ignore) {
            // Expected exception
        }
    }

    @Test
    public void testInExpression() throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("2 IN ARRAY(1,2,3)");
        var result = calculator.evaluate();
        assertEquals(VariantType.Boolean, result.getType());
        assertTrue(result.getAsBoolean());

        calculator = new ExpressionCalculator();

        calculator.setExpression("5 NOT IN ARRAY(1,2,3)");
        result = calculator.evaluate();
        assertEquals(VariantType.Boolean, result.getType());
        assertTrue(result.getAsBoolean());
    }
}
