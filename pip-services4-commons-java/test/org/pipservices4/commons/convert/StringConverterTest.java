package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class StringConverterTest {

	@Test
	public void testToString() {
		assertNull(StringConverter.toNullableString(null));
		assertEquals("xyz", StringConverter.toString("xyz"));
        assertEquals("123", StringConverter.toString(123));
        assertEquals("true", StringConverter.toString(true));
        //assertEquals("{ prop = xyz }", StringConverter.toString(new { prop = "xyz" }, "xyz"));

        assertEquals("xyz", StringConverter.toStringWithDefault(null, "xyz"));
	}
	
}
