package org.pipservices4.commons.reflect;

import org.junit.*;
import org.pipservices4.commons.reflect.TestClass.TestNestedClass;

import static org.junit.Assert.*;

import java.util.*;

public class PropertyReflectorTest {
	@Test
	public void testGetProperty() {
		TestClass obj = new TestClass();

		Object value = PropertyReflector.getProperty(obj, "privateField");
		assertNull(value);
		
		value = PropertyReflector.getProperty(obj, "publicField");
		assertEquals("ABC", value);
		
		value = PropertyReflector.getProperty(obj, "PublicProp");
		assertNotNull(value);
	}

	@Test
	public void testGetProperties() {
		TestClass obj = new TestClass();
		List<String> names = PropertyReflector.getPropertyNames(obj);
		assertEquals(3, names.size());
		assertTrue(names.contains("publicField"));
		assertTrue(names.contains("publicProp"));
		
		Map<String, Object> map = PropertyReflector.getProperties(obj);
		assertEquals(3, map.size());
		assertEquals("ABC", map.get("publicField"));
		assertNotNull(map.get("publicProp"));
	}
	
	@Test
	public void TestGetNestedProperties()
    {
        TestClass obj = new TestClass();
        TestNestedClass val = obj.getNestedProperty();      
        val.setIntProperty(10);
        obj.setNestedProperty( val);

        Map<String, Object> map = PropertyReflector.getProperties(obj);
        assertEquals(3, map.size());
        assertTrue(map.containsKey("nestedProperty"));
        assertEquals(10, ((TestNestedClass)map.get("nestedProperty")).getIntProperty());
    }
	
	@Test
	public void TestHasProperties()
    {
        TestClass obj = new TestClass();

        assertFalse(PropertyReflector.hasProperty(obj, "123"));
        assertTrue(PropertyReflector.hasProperty(obj, "PublicProp"));
        assertTrue(PropertyReflector.hasProperty(obj, "NestedProperty"));
    }
}
