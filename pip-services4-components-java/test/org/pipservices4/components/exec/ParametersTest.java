package org.pipservices4.components.exec;

import org.junit.Test;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.data.*;

import java.util.Map;

import static org.junit.Assert.*;

public class ParametersTest {
	@Test
	public void testDefaults() {
        Parameters result = Parameters.fromTuples(
    		"value1", 123, 
    		"value2", 234
		);
        Parameters defaults = Parameters.fromTuples(
    		"value2", 432, 
    		"value3", 345
		);
        result = result.setDefaults(defaults, false);
        assertEquals(3, result.size());
        assertEquals(123, result.get("value1"));
        assertEquals(234, result.get("value2"));
        assertEquals(345, result.get("value3"));
	}

	@Test
    public void testOverrideRecursive() {
        Parameters result = Parameters.fromJson(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 } }"
		);
        Parameters defaults = Parameters.fromJson(
    		"{ \"value2\": { \"value22\": 777, \"value23\": 333 }, \"value3\": 345 }"
		);
        result = result.setDefaults(defaults, true);

        assertEquals(3, result.size());
        assertEquals(123, result.get("value1"));
        assertEquals(345, result.get("value3"));

        AnyValueMap deepResult = result.getAsMap("value2");
        assertEquals(3, deepResult.size());
        assertEquals(111, deepResult.get("value21"));
        assertEquals(222, deepResult.get("value22"));
        assertEquals(333, deepResult.get("value23"));
    }

	@Test
    public void testOverrideWithNulls() {
        Parameters result = Parameters.fromJson(
    		"{ \"value1\": 123, \"value2\": 234 }"
		);
        result = result.override(null, true);

        assertEquals(2, result.size());
        assertEquals(123, result.get("value1"));
        assertEquals(234, result.get("value2"));
    }
	
	@Test 
	public void testAssignTo() {
		TestClass value = new TestClass(null, null);
		Parameters newValues = Parameters.fromJson(
			"{ \"value1\": 123, \"value2\": \"ABC\", \"value3\": 456 }"
		);
		
		newValues.assignTo(value);
		assertNotNull(value.value1);
		assertEquals(123, value.value1);
		assertNotNull(value.getValue2());
		assertEquals("ABC", value.getValue2());
	}
	
	@Test
    public void testGet() {
        Parameters config = Parameters.fromJson(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 } }"
		);

        Object value = config.get("");
        assertNull(value);

        value = config.get("value1");
        assertNotNull(value);
        assertEquals(123, value);

        value = config.get("value2");
        assertNotNull(value);

        value = config.get("value3");
        assertNull(value);

        value = config.get("value2.value21");
        assertNotNull(value);
        assertEquals(111, value);

        value = config.get("value2.value31");
        assertNull(value);

        value = config.get("value2.value21.value211");
        assertNull(value);

        value = config.get("valueA.valueB.valueC");
        assertNull(value);
    }

    @Test
    public void testContains() {
        Parameters config = Parameters.fromJson(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 } }"
		);

        boolean has = config.containsKey("");
        assertFalse(has);

        has = config.containsKey("value1");
        assertTrue(has);

        has = config.containsKey("value2");
        assertTrue(has);

        has = config.containsKey("value3");
        assertFalse(has);

        has = config.containsKey("value2.value21");
        assertTrue(has);

        has = config.containsKey("value2.value31");
        assertFalse(has);

        has = config.containsKey("value2.value21.value211");
        assertFalse(has);

        has = config.containsKey("valueA.valueB.valueC");
        assertFalse(has);
    }

	@Test
    public void testPut() {
    	Parameters config = new Parameters();
    	
    	config.put(null, 123);
    	assertEquals(0, config.size());
    	
    	config.put("field1", 123);
    	assertEquals(1, config.size());
    	assertEquals(123, config.get("field1"));

    	config.put("field2", "ABC");
    	assertEquals(2, config.size());
    	assertEquals("ABC", config.get("field2"));

    	config.put("field2.field1", 123);
    	assertEquals("ABC", config.get("field2"));

    	config.put("field3.field31", 456);
    	assertEquals(3, config.size());
    	Map<String, Object> subConfig = config.getAsMap("field3");
    	assertNotNull(subConfig);
    	assertEquals(456, subConfig.get("field31"));
    	
    	config.put("field3.field32", "XYZ");
    	assertEquals("XYZ", config.get("field3.field32"));
    }
	
	@Test
	public void TestFromConfig() {
		ConfigParams config = ConfigParams.fromTuples(
			"field1.field11", 123,
			"field2", "ABC",
			"field1.field12", "XYZ"
		);
		
		Parameters params = Parameters.fromConfig(config);
		assertEquals(2, params.size());
		assertEquals("ABC", params.get("field2"));
		Map<String, Object> value = params.getAsMap("field1");
		assertEquals(2, value.size());
		assertEquals("123", value.get("field11"));
		assertEquals("XYZ", value.get("field12"));
	}
}
