package org.pipservices4.data.random;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.commons.convert.*;

public class RandomDoubleTest {
    @Test
    public void testNextDouble() {
        double value = RandomDouble.nextDouble(5);
        assertTrue(value < 5);
        assertEquals(value, DoubleConverter.toDouble(value), 0.0);

        value = RandomDouble.nextDouble(2, 5);
        assertTrue(value < 5 && value > 2);
        assertEquals(value, DoubleConverter.toDouble(value), 0.0);
    }

    @Test
    public void testUpdateDouble() {
        double value = RandomDouble.updateDouble(0, 5);

        assertTrue(value <= 5 && value >= -5);
        assertEquals(value, DoubleConverter.toDouble(value), 0.0);
    }
}