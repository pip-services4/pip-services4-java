package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import org.junit.*;

public class DurationConverterTest {

	@Test
    public void testToNullableDuration() {
        assertNull(DurationConverter.toNullableDuration(null));
        assertEquals(6, DurationConverter.toNullableDuration((int) 6000).getSeconds());
        assertEquals(6, DurationConverter.toNullableDuration((short) 6000).getSeconds());
        assertEquals(6, DurationConverter.toNullableDuration(6000.5).getSeconds());
        assertEquals(-1, DurationConverter.toNullableDuration(-600).getSeconds());
        assertEquals(0, DurationConverter.toNullableDuration(0).getSeconds());
    }
	
	@Test
    public void testToDateTime() {
        assertNull(DurationConverter.toDuration(null));
        assertEquals(6, DurationConverter.toDuration((int) 6000).getSeconds());
        assertEquals(6, DurationConverter.toDuration((short) 6000).getSeconds());
        assertEquals(6, DurationConverter.toDuration(6000.5).getSeconds());
        assertEquals(-1, DurationConverter.toDuration(-600).getSeconds());
        assertEquals(0, DurationConverter.toDuration(0).getSeconds());
    }

}
