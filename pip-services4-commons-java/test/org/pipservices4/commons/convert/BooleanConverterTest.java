package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class BooleanConverterTest {

	@Test
    public void testToBoolean() {
        assertTrue(BooleanConverter.toBoolean(true));
        assertTrue(BooleanConverter.toBoolean(1));
        assertTrue(BooleanConverter.toBoolean("True"));
        assertTrue(BooleanConverter.toBoolean("yes"));
        assertTrue(BooleanConverter.toBoolean("1"));
        assertTrue(BooleanConverter.toBoolean("Y"));

        assertFalse(BooleanConverter.toBoolean(false));
        assertFalse(BooleanConverter.toBoolean(0));
        assertFalse(BooleanConverter.toBoolean("False"));
        assertFalse(BooleanConverter.toBoolean("no"));
        assertFalse(BooleanConverter.toBoolean("0"));
        assertFalse(BooleanConverter.toBoolean("N"));

        assertFalse(BooleanConverter.toBoolean(123));
		assertFalse(BooleanConverter.toBoolean(null));
        assertTrue(BooleanConverter.toBooleanWithDefault("XYZ", true));
    }

}
