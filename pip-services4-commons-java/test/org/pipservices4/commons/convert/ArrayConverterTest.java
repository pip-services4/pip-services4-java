package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.*;

public class ArrayConverterTest {

	@Test
    public void testToNullableArray() {
        assertNull(ArrayConverter.toNullableArray(null));

        assertEquals(1, ArrayConverter.toNullableArray(2).size());
        assertSame("java.util.ArrayList", ArrayConverter.toNullableArray(2).getClass().getName());
        
        List<Integer> array = new ArrayList<Integer>();
        array.add(1);
		array.add(2);
        assertEquals(2, ArrayConverter.toNullableArray(array).size());
        assertSame("java.util.ArrayList", ArrayConverter.toNullableArray(array).getClass().getName());
        
        String[] stringArray = {"ab", "cd"};
        assertSame("java.util.ArrayList", ArrayConverter.toNullableArray(stringArray).getClass().getName());
        
        List<Object> value = ArrayConverter.listToArray("123,456");
        assertEquals(2, value.size());
        assertEquals("123", value.get(0));
        assertEquals("456", value.get(1));
    }

    @Test
    public void testToArray() {
        List<Object> value = ArrayConverter.toArray(null);
        assertEquals(0, value.size());

        value = ArrayConverter.toArray(123);
        assertEquals(1, value.size());
        assertEquals(123, value.get(0));

        value = ArrayConverter.toArray(List.of(123));
        assertEquals(1, value.size());
        assertEquals(123, value.get(0));

        value = ArrayConverter.toArray("123");
        assertEquals(1, value.size());
        assertEquals("123", value.get(0));

        value = ArrayConverter.listToArray("123,456");
        assertEquals(2, value.size());
        assertEquals("123", value.get(0));
        assertEquals("456", value.get(1));

        value = ArrayConverter.toArray(Map.of("field1", "abc", "field2", 123));
        assertEquals(2, value.size());
        assertTrue(value.containsAll(List.of("abc", 123)));
    }
}
