package org.pipservices4.data.random;

import static org.junit.Assert.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class RandomDateTimeTest {
    @Test
    public void testNextDate() {
        ZonedDateTime date;
        date = RandomDateTime.nextDate(2015, 2016);
        assertTrue(date.getYear() == 2015 || date.getYear() == 2016);
    }

    @Test
    public void testUpdateDateTime() {
        ZonedDateTime oldDate = ZonedDateTime.of(2016, 10, 10, 0, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime date;

        date = RandomDateTime.updateDateTime(oldDate);
        assertTrue(date.getDayOfYear() >= oldDate.getDayOfYear() - 10
                || date.getDayOfYear() <= oldDate.getDayOfYear() + 10);

        date = RandomDateTime.updateDateTime(oldDate, 3);
        assertTrue(date.getDayOfYear() >= oldDate.getDayOfYear() - 3
                || date.getDayOfYear() <= oldDate.getDayOfYear() + 3);

        date = RandomDateTime.updateDateTime(oldDate, -3);
        assertEquals(date.getDayOfYear(), oldDate.getDayOfYear());
    }

}