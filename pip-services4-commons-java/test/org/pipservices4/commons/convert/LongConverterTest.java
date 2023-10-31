package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.Date;

import org.junit.*;

public class LongConverterTest {
	
	@Test
	public void testToLong() {
		Date date = new Date();
		assertEquals(date.getTime(), LongConverter.toLong(date));
		assertEquals(100, LongConverter.toLong(Duration.ofMillis(100)));
        assertEquals(123, LongConverter.toLong(123));
        assertEquals(123, LongConverter.toLong((short)123));
        assertEquals(123, LongConverter.toLong(123.456));
        assertEquals(123, LongConverter.toLong(DoubleConverter.toDouble(123.456)));
        assertEquals(123, LongConverter.toLong("123"));
        assertEquals(123, LongConverter.toLong("123.465"));
        assertEquals(0, LongConverter.toLong(null));
	}

	@Test
	public void testToLongWithDefault() {
        assertEquals(123, LongConverter.toLongWithDefault(null, 123));
        assertEquals(0, LongConverter.toLongWithDefault(false, 123));
        assertEquals(123, LongConverter.toLongWithDefault("ABC", 123));
	}

}
