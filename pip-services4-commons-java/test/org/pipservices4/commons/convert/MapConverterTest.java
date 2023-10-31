package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

public class MapConverterTest {

	@Test
	public void testToNullableMap() {
		assertNull(MapConverter.toNullableMap(null));
		assertNull(MapConverter.toNullableMap(5));
		
		Collection<Object> array = new ArrayList<>();
	    array.add(1);
	    array.add(2);
        assertEquals(MapConverter.toNullableMap(array).size(), 2);
        assertTrue(MapConverter.toNullableMap(array).containsKey("0"));
        assertTrue(MapConverter.toNullableMap(array).containsKey("1"));
        assertEquals(MapConverter.toNullableMap(array).get("0"), 1);
        assertEquals(MapConverter.toNullableMap(array).get("1"), 2);
        assertTrue(MapConverter.toNullableMap(array).containsValue(1));
        assertTrue(MapConverter.toNullableMap(array).containsValue(2));
        
        String[] values = {"ab", "cd"};
        assertEquals(MapConverter.toNullableMap(values).size(), 2);
        assertTrue(MapConverter.toNullableMap(values).containsKey("0"));
        assertTrue(MapConverter.toNullableMap(values).containsKey("1"));
        assertEquals(MapConverter.toNullableMap(values).get("0"), "ab");
        assertEquals(MapConverter.toNullableMap(values).get("1"), "cd");
        assertTrue(MapConverter.toNullableMap(values).containsValue("ab"));
        assertTrue(MapConverter.toNullableMap(values).containsValue("cd"));
        
        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(8, "title 8");
        map.put(11, "title 11");
        assertEquals(MapConverter.toNullableMap(map).size(), 2);
        assertTrue(MapConverter.toNullableMap(map).containsKey("8"));
        assertTrue(MapConverter.toNullableMap(map).containsKey("11"));
        assertEquals(MapConverter.toNullableMap(map).get("8"), "title 8");
        assertEquals(MapConverter.toNullableMap(map).get("11"), "title 11");
        assertTrue(MapConverter.toNullableMap(map).containsValue("title 8"));
        assertTrue(MapConverter.toNullableMap(map).containsValue("title 11")); 
	}
	
	@Test
	public void testToMap() {
		assertEquals(MapConverter.toMap(null).size(), 0);
		assertEquals(MapConverter.toMap(5).size(), 0);
	}
	
	@Test
	public void testToMapWithDefault() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name1", "title 1");
        map.put("name2", "title 2");
		assertEquals(MapConverter.toMapWithDefault(null, map).size(), 2);
		assertEquals(MapConverter.toMapWithDefault(null, map).get("name1"), "title 1");
        assertEquals(MapConverter.toMapWithDefault(null, map).get("name2"), "title 2");
        
		assertEquals(MapConverter.toMapWithDefault(5, map).size(), 2);
		assertEquals(MapConverter.toMapWithDefault(5, map).get("name1"), "title 1");
        assertEquals(MapConverter.toMapWithDefault(5, map).get("name2"), "title 2");
	}
}
