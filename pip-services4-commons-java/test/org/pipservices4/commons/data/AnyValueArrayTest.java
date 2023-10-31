package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.*;

public class AnyValueArrayTest {

    @Test
    public void testGetAndSet() {
        AnyValue value = new AnyValue();

        value.setAsObject(1);
        assertEquals(1, value.getAsInteger());
        assertTrue(1.0 - value.getAsFloat() < 0.001);
        assertEquals("1", value.getAsString());
    }

    @Test
    public void testCreateValueArray() {
        AnyValueArray array = new AnyValueArray();
        assertEquals(0, array.size());

        array = new AnyValueArray(new Object[] { 1, 2, 3 });
        assertEquals(3, array.size());
        assertEquals("1,2,3", array.toString());

        array = AnyValueArray.fromString("Fatal,Error,Info,", ",");
        assertEquals(4, array.size());

        array = new AnyValueArray(new Object[] { 1, 2, 3 });
        assertEquals(3, array.size());
        assertTrue(array.contains(1));
//        assertTrue(array.containsAsType<EnumTest>(EnumTest.Error));

        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        array = new AnyValueArray(list);
        assertEquals(3, array.size());
        assertTrue(array.contains(2));
    }
	
}
