package org.pipservices4.data.random;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomBooleanTest {
    @Test
    public void testChance() {
        boolean value;
        value = RandomBoolean.chance(5, 10);
        assertTrue(value || !value);

        value = RandomBoolean.chance(5, 5);
        assertTrue(value || !value);

        value = RandomBoolean.chance(0, 0);
        assertFalse(value);

        value = RandomBoolean.chance(-1, 0);
        assertFalse(value);

        value = RandomBoolean.chance(-1, -1);
        assertFalse(value);
    }

}