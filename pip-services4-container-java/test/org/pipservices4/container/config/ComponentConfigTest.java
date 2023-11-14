package org.pipservices4.container.config;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.commons.reflect.TypeDescriptor;

public class ComponentConfigTest {
	@Test
	public void testType() {
		ComponentConfig componentConfig = new ComponentConfig();
		assertNull(componentConfig.getType());
		
		TypeDescriptor type = new TypeDescriptor("new name", null);
		componentConfig.setType(type);
		assertEquals(componentConfig.getType(), type);
	}
	
	@Test
	public void testDescriptor() {
		ComponentConfig componentConfig = new ComponentConfig();
		assertNull(componentConfig.getDescriptor());
		
		Descriptor descriptor = new Descriptor("group", "type", "kind", "id", "version");
		componentConfig.setDescriptor(descriptor);
		assertEquals(componentConfig.getDescriptor(), descriptor);
	}
	
	@Test
	public void testConfigParams() {
		ComponentConfig componentConfig = new ComponentConfig();
		assertNull(componentConfig.getConfig());
		
		ConfigParams config = ConfigParams.fromTuples(
		        "config.key", "key",
		        "config.key2", "key2"
			);
		componentConfig.setConfig(config);
		assertEquals(componentConfig.getConfig(), config);
	}

	@Test
	public void testFromEmptyConfig() throws ConfigException {
		ConfigParams config = ConfigParams.fromTuples();

		try {
			ComponentConfig componentConfig = ComponentConfig.fromConfig(config);
		} catch (ApplicationException ex) {
			assertEquals(ex.getMessage(), "Component configuration must have descriptor or type");
		}
	}
	
	@Test
	public void testFromConfig() throws ConfigException {
		ConfigParams config = ConfigParams.fromTuples();
		ComponentConfig componentConfig;
		try {
			componentConfig = ComponentConfig.fromConfig(config);
		} catch (ConfigException e) {
			assertEquals(e.getMessage(), "Component configuration must have descriptor or type");
		}
		
		config = ConfigParams.fromTuples(
			"descriptor", "descriptor_name",
			"type", "type",
		    "config.key", "key",
		    "config.key2", "key2"
		);
		try {
			componentConfig = ComponentConfig.fromConfig(config);
		} catch (ConfigException e) {
			assertEquals(e.getMessage(), "Descriptor descriptor_name is in wrong format");
		}
		
		Descriptor descriptor = new Descriptor("group", "type", "kind", "id", "version");
		TypeDescriptor type = new TypeDescriptor("type", null);
		config = ConfigParams.fromTuples(
			"descriptor", "group:type:kind:id:version",
			"type", "type",
			"config.key", "key",
			"config.key2", "key2"
		);
		componentConfig = ComponentConfig.fromConfig(config);
		assertEquals(componentConfig.getDescriptor(), descriptor);
		assertEquals(componentConfig.getType(), type);
		
	}
}
