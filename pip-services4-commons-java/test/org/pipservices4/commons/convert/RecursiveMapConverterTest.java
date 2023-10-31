package org.pipservices4.commons.convert;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import java.util.*;
import org.junit.*;

public class RecursiveMapConverterTest {
	
	class TestClass {
		public TestClass(Object value1, Object value2) {
			this.value1 = value1;
			this.value2 = value2;
		}
		
		public Object value1;
		public Object value2;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testObjectToMap() {
		// Handling nulls
		Object value = null;
		Map<String, Object> result = RecursiveMapConverter.toNullableMap(value);
		assertNull(result);
		
		// Handling simple objects
		value = new TestClass(123, 234);
		result = RecursiveMapConverter.toNullableMap(value);
		assertEquals(123, result.get("value1"));
		assertEquals(234, result.get("value2"));

        // Handling dictionaries
        value = new HashMap<String, Object>();
        result = RecursiveMapConverter.toNullableMap(value);
        assertSame(value, result);

        // Non-recursive conversion
//        value = new TestClass(123, new TestClass(111, 222));
//        result = RecursiveMapConverter.toMap(value, null, false);
//        assertNotNull(result);
//        assertEquals(123, result.get("value1"));
//        assertNotNull(result.get("value2"));
//        assertFalse(result.get("value2") instanceof Map<?,?>);
//        assertTrue(result.get("value2") instanceof TestClass);

        // Recursive conversion
        value = new TestClass(123, new TestClass(111, 222));
        result = RecursiveMapConverter.toNullableMap(value);
        assertNotNull(result);
        assertEquals(123, result.get("value1"));
        assertNotNull(result.get("value2"));
        assertTrue(result.get("value2") instanceof Map<?,?>);

        // Handling arrays
        value = new TestClass(new Object[] { new TestClass(111, 222) }, null);
        result = RecursiveMapConverter.toNullableMap(value);
        assertNotNull(result);
        assertTrue(result.get("value1") instanceof List<?>);
        List<Object> resultElements = ((List<Object>)result.get("value1"));
        Map<String, Object> resultElement0 = (Map<String, Object>)resultElements.get(0);
        assertNotNull(resultElement0);
        assertEquals(111, resultElement0.get("value1"));
        assertEquals(222, resultElement0.get("value2"));

        // Handling lists
        value = new TestClass(List.of(new TestClass(111, 222)), null);
        result = RecursiveMapConverter.toNullableMap(value);
        assertNotNull(result);
        assertTrue(result.get("value1") instanceof List<?>);
        resultElements = ((List<Object>)result.get("value1"));
        resultElement0 = (Map<String, Object>)resultElements.get(0);
        assertNotNull(resultElement0);
        assertEquals(111, resultElement0.get("value1"));
        assertEquals(222, resultElement0.get("value2"));
	}

}
