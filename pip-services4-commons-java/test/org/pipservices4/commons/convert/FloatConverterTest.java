package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class FloatConverterTest {

	@Test
    public void testToFloat() {
        assertTrue(Math.abs(123 - FloatConverter.toFloat(123)) < 0.001);
        assertTrue(Math.abs(123.456 - FloatConverter.toFloat(123.456)) < 0.001);
        assertTrue(Math.abs(123.456 - FloatConverter.toFloat("123.456")) < 0.001);

        assertTrue(Math.abs(123 - FloatConverter.toFloatWithDefault(null, 123)) < 0.001);
        assertTrue(Math.abs(0 - FloatConverter.toFloatWithDefault(false, 123)) < 0.001);
        assertTrue(Math.abs(123 - FloatConverter.toFloatWithDefault("ABC", 123)) < 0.001);
    }
	
}
