package org.pipservices4.components.config;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.pipservices4.commons.data.AnyValueArray;
import org.pipservices4.commons.data.AnyValueMap;

public class ConfigParamsTest {

	@Test
	public void testConfigSections() {
        ConfigParams config = ConfigParams.fromTuples(
            "Section1.Key1", "Value1",
            "Section1.Key2", "Value2",
            "Section1.Key3", "Value3"
        );
        assertEquals(3, config.size());
        assertEquals("Value1", config.get("Section1.Key1"));
        assertEquals("Value2", config.get("Section1.Key2"));
        assertEquals("Value3", config.get("Section1.Key3"));
        assertNull(config.get("Section1.Key4"));

        ConfigParams section2 = ConfigParams.fromTuples(
            "Key1", "ValueA",
            "Key2", "ValueB"
        );
        config.addSection("Section2", section2);
        assertEquals(5, config.size());
        assertEquals("ValueA", config.get("Section2.Key1"));
        assertEquals("ValueB", config.get("Section2.Key2"));

        List<String> sections = config.getSectionNames();
        assertEquals(2, sections.size());
        assertTrue(sections.contains("Section1"));
        assertTrue(sections.contains("Section2"));
        
        ConfigParams section1 = config.getSection("section1");
        assertEquals(3, section1.size());
        assertEquals("Value1", section1.get("Key1"));
        assertEquals("Value2", section1.get("Key2"));
        assertEquals("Value3", section1.get("Key3"));		
	}
	
	@Test
	public void testConfigFromString() {
		ConfigParams config = ConfigParams.fromString("Queue=TestQueue;Endpoint=sb://cvctestbus.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=K70UpCUXN1Q5RFykll6/gz4Et14iJrYFnGPlwiFBlow=");
		assertEquals(4, config.size());
		assertEquals("TestQueue", config.get("Queue"));
	}

	@Test
	public void testConfigFromObject() {
		Object value = AnyValueMap.fromTuples(
			"field1", ConfigParams.fromString("field11=123;field12=ABC"),
			"field2", AnyValueArray.fromValues(
				123, "ABC", ConfigParams.fromString("field21=543;field22=XYZ")
			),
			"field3", true
		);
		
		ConfigParams config = ConfigParams.fromValue(value);
		assertEquals(7, config.size());
		assertEquals(123, config.getAsInteger("field1.field11"));
		assertEquals("ABC", config.getAsString("field1.field12"));
		assertEquals(123, config.getAsInteger("field2.0"));
		assertEquals("ABC", config.getAsString("field2.1"));
		assertEquals(543, config.getAsInteger("field2.2.field21"));
		assertEquals("XYZ", config.getAsString("field2.2.field22"));
		assertTrue(config.getAsBoolean("field3"));
	}

}
