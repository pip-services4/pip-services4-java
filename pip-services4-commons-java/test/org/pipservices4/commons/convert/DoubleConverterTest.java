package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class DoubleConverterTest {

	@Test
    public void testToDouble() {
		assertNull(DoubleConverter.toNullableDouble(null));
        assertTrue(Math.abs(123 - DoubleConverter.toDouble(123)) < 0.001);
        assertTrue(Math.abs(123.456 - DoubleConverter.toDouble(123.456)) < 0.001);
        assertTrue(Math.abs(123.456 - DoubleConverter.toDouble("123.456")) < 0.001);

        assertTrue(Math.abs(123 - DoubleConverter.toDoubleWithDefault(null, 123)) < 0.001);
        assertTrue(Math.abs(0 - DoubleConverter.toDoubleWithDefault(false, 123)) < 0.001);
        assertTrue(Math.abs(123 - DoubleConverter.toDoubleWithDefault("ABC", 123)) < 0.001);
    }
	
}
