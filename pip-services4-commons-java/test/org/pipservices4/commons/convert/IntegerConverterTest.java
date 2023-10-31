package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class IntegerConverterTest {
	
	@Test
	public void testToInteger() {
        assertEquals(123, IntegerConverter.toInteger(123));
        assertEquals(123, IntegerConverter.toInteger(123.456));
        assertEquals(123, IntegerConverter.toInteger("123"));
        assertEquals(123, IntegerConverter.toInteger("123.465"));

        assertEquals(123, IntegerConverter.toIntegerWithDefault(null, 123));
        assertEquals(0, IntegerConverter.toIntegerWithDefault(false, 123));
        assertEquals(123, IntegerConverter.toIntegerWithDefault("ABC", 123));
	}

}
