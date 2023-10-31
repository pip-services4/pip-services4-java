package org.pipservices4.commons.reflect;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.pipservices4.commons.convert.*;

public class RecursiveObjectWriterTest {

    @Test
    public void testSetProperty() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        //RecursiveObjectWriter.setProperty(obj, "", null);
        RecursiveObjectWriter.setProperty(obj, "value1", "AAA");
        RecursiveObjectWriter.setProperty(obj, "value2", "BBB");
        RecursiveObjectWriter.setProperty(obj, "value3.1.value312", "CCC");
        RecursiveObjectWriter.setProperty(obj, "value3.3", "DDD");
        RecursiveObjectWriter.setProperty(obj, "value4.1", "EEE");
        
        Map<String, Object> values = RecursiveObjectReader.getProperties(obj);
        assertEquals(8, values.size());
        assertEquals("AAA", values.get("value1"));
        assertEquals("BBB", values.get("value2"));
        assertNull(values.get("value2.value21"));
        assertEquals(444, values.get("value3.0"));
        assertEquals(555, values.get("value3.1.value311"));
        assertEquals("CCC", values.get("value3.1.value312"));
        assertNull(values.get("value3.2"));
        assertEquals("DDD", values.get("value3.3"));
        assertEquals("EEE", values.get("value4.1"));
    }

    @Test
    public void testSetProperties() {
        Object obj = JsonConverter.toMap(
    		"{ \"value1\": 123, \"value2\": { \"value21\": 111, \"value22\": 222 }, \"value3\": [ 444, { \"value311\": 555 } ] }"
		);

        Map<String, Object> values = new HashMap<String, Object>() {
            {
                put("value1", "AAA");
                put("value2", "BBB");
                put("value3.1.value312", "CCC");
                put("value3.3", "DDD");
                put("value4.1", "EEE");
            }
        };
        RecursiveObjectWriter.setProperties(obj, values);
        
        values = RecursiveObjectReader.getProperties(obj);
        assertEquals(8, values.size());
        assertEquals("AAA", values.get("value1"));
        assertEquals("BBB", values.get("value2"));
        assertNull(values.get("value2.value21"));
        assertEquals(444, values.get("value3.0"));
        assertEquals(555, values.get("value3.1.value311"));
        assertEquals("CCC", values.get("value3.1.value312"));
        assertNull(values.get("value3.2"));
        assertEquals("DDD", values.get("value3.3"));
        assertEquals("EEE", values.get("value4.1"));
    }

}
