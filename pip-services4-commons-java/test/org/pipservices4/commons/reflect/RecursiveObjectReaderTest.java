package org.pipservices4.commons.reflect;

import org.junit.*;
import org.pipservices4.commons.convert.*;

import static org.junit.Assert.*;

import java.util.*;

public class RecursiveObjectReaderTest {

    @Test
    public void testHasProperty() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        boolean has = RecursiveObjectReader.hasProperty(obj, "");
        assertFalse(has);

        has = RecursiveObjectReader.hasProperty(obj, "value1");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value2");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value2.value21");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value2.value31");
        assertFalse(has);

        has = RecursiveObjectReader.hasProperty(obj, "value2.value21.value211");
        assertFalse(has);

        has = RecursiveObjectReader.hasProperty(obj, "valueA.valueB.valueC");
        assertFalse(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3.0");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3.0.value311");
        assertFalse(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3.1");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3.1.value311");
        assertTrue(has);

        has = RecursiveObjectReader.hasProperty(obj, "value3.2");
        assertFalse(has);
    }

    @Test
    public void testGetProperty() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        Object value = RecursiveObjectReader.getProperty(obj, "");
        assertNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value1");
        assertEquals(123, value);

        value = RecursiveObjectReader.getProperty(obj, "value2");
        assertNotNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value2.value21");
        assertEquals(111, value);

        value = RecursiveObjectReader.getProperty(obj, "value2.value31");
        assertNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value2.value21.value211");
        assertNull(value);

        value = RecursiveObjectReader.getProperty(obj, "valueA.valueB.valueC");
        assertNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value3");
        assertNotNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value3.0");
        assertEquals(444, value);

        value = RecursiveObjectReader.getProperty(obj, "value3.0.value311");
        assertNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value3.1");
        assertNotNull(value);

        value = RecursiveObjectReader.getProperty(obj, "value3.1.value311");
        assertEquals(555, value);

        value = RecursiveObjectReader.getProperty(obj, "value3.2");
        assertNull(value);
    }

    @Test
    public void testGetPropertyNames() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        List<String> names = RecursiveObjectReader.getPropertyNames(obj);
        assertEquals(5, names.size());
        assertTrue(names.contains("value1"));
        assertTrue(names.contains("value2.value21"));
        assertTrue(names.contains("value2.value22"));
        assertTrue(names.contains("value3.0"));
        assertTrue(names.contains("value3.1.value311"));
    }

    @Test
    public void testGetProperties() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        Map<String, Object> values = RecursiveObjectReader.getProperties(obj);
        assertEquals(5, values.size());
        assertEquals(123, values.get("value1"));
        assertEquals(111, values.get("value2.value21"));
        assertEquals(222, values.get("value2.value22"));
        assertEquals(444, values.get("value3.0"));
        assertEquals(555, values.get("value3.1.value311"));
    }

}
