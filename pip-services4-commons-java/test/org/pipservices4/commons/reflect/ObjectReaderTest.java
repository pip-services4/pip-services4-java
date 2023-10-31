package org.pipservices4.commons.reflect;

import org.junit.*;
import org.pipservices4.commons.data.*;

import static org.junit.Assert.*;

import java.util.*;

public class ObjectReaderTest {
	
	@Test
	public void testGetObjectProperty() {
		TestClass obj = new TestClass();

		Object value = ObjectReader.getProperty(obj, "privateField");
		assertNull(value);
		
		value = ObjectReader.getProperty(obj, "publicField");
		assertEquals("ABC", value);
		
		value = ObjectReader.getProperty(obj, "PublicProp");
		assertNotNull(value);
	}

	@Test
	public void testGetMapProperty() {
		AnyValueMap map = AnyValueMap.fromTuples(
			"key1", 123,
			"key2", "ABC"
		);

		Object value = ObjectReader.getProperty(map, "key3");
		assertNull(value);
		
		value = ObjectReader.getProperty(map, "Key1");
		assertEquals(123, value);
		
		value = ObjectReader.getProperty(map, "KEY2");
		assertEquals("ABC", value);
	}

	@Test
	public void testGetArrayProperty() {
		AnyValueArray list = AnyValueArray.fromValues(123, "ABC");

		Object value = ObjectReader.getProperty(list, "3");
		assertNull(value);
		
		value = ObjectReader.getProperty(list, "0");
		assertEquals(123, value);
		
		value = ObjectReader.getProperty(list, "1");
		assertEquals("ABC", value);

		Object[] array = new Object[] { 123, "ABC" };
		
		value = ObjectReader.getProperty(array, "3");
		assertNull(value);
		
		value = ObjectReader.getProperty(array, "0");
		assertEquals(123, value);
		
		value = ObjectReader.getProperty(array, "1");
		assertEquals("ABC", value);
	}

	@Test
	public void testGetObjectProperties() {
		TestClass obj = new TestClass();
		List<String> names = ObjectReader.getPropertyNames(obj);
		assertEquals(3, names.size());
		assertTrue(names.contains("publicField"));
		assertTrue(names.contains("publicProp"));
		
		Map<String, Object> map = ObjectReader.getProperties(obj);
		assertEquals(3, map.size());
		assertEquals("ABC", map.get("publicField"));
		assertNotNull(map.get("publicProp"));
	}

	@Test
	public void testGetMapProperties() {
		AnyValueMap map = AnyValueMap.fromTuples(
			"key1", 123,
			"key2", "ABC"
		);
		List<String> names = ObjectReader.getPropertyNames(map);
		assertEquals(2, names.size());
		assertTrue(names.contains("key1"));
		assertTrue(names.contains("key2"));
		
		Map<String, Object> values = ObjectReader.getProperties(map);
		assertEquals(2, values.size());
		assertEquals(123, values.get("key1"));
		assertEquals("ABC", values.get("key2"));
	}

	@Test
	public void testGetArrayProperties() {
		AnyValueArray list = AnyValueArray.fromValues(123, "ABC" );
		
		List<String> names = ObjectReader.getPropertyNames(list);
		assertEquals(2, names.size());
		assertTrue(names.contains("0"));
		assertTrue(names.contains("1"));
		
		Map<String, Object> values = ObjectReader.getProperties(list);
		assertEquals(2, values.size());
		assertEquals(123, values.get("0"));
		assertEquals("ABC", values.get("1"));
		
		Object[] array = new Object[] { 123, "ABC" };

		names = ObjectReader.getPropertyNames(array);
		assertEquals(2, names.size());
		assertTrue(names.contains("0"));
		assertTrue(names.contains("1"));
		
		values = ObjectReader.getProperties(array);
		assertEquals(2, values.size());
		assertEquals(123, values.get("0"));
		assertEquals("ABC", values.get("1"));
	}

}
