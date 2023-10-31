package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.time.*;
import java.util.*;
import org.junit.*;

public class DateTimeConverterTest {
	
	@Test
    public void testToDateTime() {
        assertNull(DateTimeConverter.toDateTime(null));

        ZonedDateTime date1 = ZonedDateTime.of(LocalDateTime.of(1975, 4, 8, 0, 0), ZoneId.systemDefault());
        assertEquals(date1, DateTimeConverter.toDateTimeWithDefault(null, date1));
        assertEquals(date1, DateTimeConverter.toDateTime(new GregorianCalendar(1975, 3, 8)));
        
        ZonedDateTime date2 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(123456), ZoneId.systemDefault());
        assertEquals(date2, DateTimeConverter.toDateTime(123456));
        
        ZonedDateTime date3 = ZonedDateTime.of(LocalDateTime.of(1975, 4, 8, 0, 0), ZoneId.of("Z"));
        assertEquals(date3, DateTimeConverter.toDateTime("1975-04-08T00:00:00Z"));
        assertEquals(date1, DateTimeConverter.toDateTime("1975/04/08"));
        
        assertNull(DateTimeConverter.toDateTime("XYZ"));
    }
	
}
