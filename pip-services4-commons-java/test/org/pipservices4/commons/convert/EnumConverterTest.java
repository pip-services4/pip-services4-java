package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class EnumConverterTest {
	
	@Test
    public void testToEnum() {
        assertEquals(EnumTest.None, EnumConverter.toEnum(EnumTest.class, "ABC"));
        assertEquals(EnumTest.Fatal, EnumConverter.toEnum(EnumTest.class, 1));
        assertEquals(EnumTest.Fatal, EnumConverter.toEnum(EnumTest.class, EnumTest.Fatal));
        assertEquals(EnumTest.Fatal, EnumConverter.toEnum(EnumTest.class, "Fatal"));
    }
	
}
