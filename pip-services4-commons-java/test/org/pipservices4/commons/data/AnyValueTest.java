package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import org.junit.*;

public class AnyValueTest {
    @Test
    public void testGetAndSetAnyValue() {
        AnyValue value = new AnyValue();
        assertNull(value.getAsObject());

        value.setAsObject(1);
        assertEquals(1, value.getAsInteger());
        assertTrue(1.0 - value.getAsFloat() < 0.001);
        assertEquals("1", value.getAsString());
        //assertEquals(TimeSpan.FromMilliseconds(1), value.GetAsTimeSpan());
        //assertEquals(EnumTest.Fatal, value.GetAsEnum<EnumTest>());
    }

    @Test
    public void testEqualAnyValue() {
        AnyValue value = new AnyValue(1);

        assertTrue(value.equals(1));
        //assertTrue(value.equals(1.0));
        assertTrue(value.equalsAsType(String.class, "1"));
        //assertTrue(value.equals(TimeSpan.FromMilliseconds(1)));
        //assertTrue(value.equalsAsType<EnumTest>(EnumTest.Fatal));
    }
}
