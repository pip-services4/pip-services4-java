package org.pipservices4.container.config;

import java.io.Serial;
import java.util.*;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;

/**
 * Container configuration defined as a list of component configurations.
 * 
 * @see ComponentConfig
 */
public class ContainerConfig extends ArrayList<ComponentConfig> {
	@Serial
	private static final long serialVersionUID = -1686520964953606299L;

	/**
	 * Creates a new instance of container configuration.
	 */
	public ContainerConfig() {
	}

	/**
	 * Creates a new instance of container configuration.
	 * 
	 * @param components (optional) a list of component configurations.
	 */
	public ContainerConfig(Collection<ComponentConfig> components) {
		if (components != null)
			super.addAll(components);
	}

	/**
	 * Creates a new ContainerConfig object filled with key-value pairs from
	 * specified object. The value is converted into ConfigParams object which is
	 * used to create the object.
	 * 
	 * @param value an object with key-value pairs used to initialize a new
	 *              ContainerConfig.
	 * @return a new ContainerConfig object.
	 * @throws ConfigException when configuration is wrong.
	 * 
	 * @see #fromConfig(ConfigParams)
	 */
	public static ContainerConfig fromValue(Object value) throws ConfigException {
		ConfigParams config = ConfigParams.fromValue(value);
		return fromConfig(config);
	}

	/**
	 * Creates a new ContainerConfig object based on configuration parameters. Each
	 * section in the configuration parameters is converted into a component
	 * configuration.
	 * 
	 * @param config an object with key-value pairs used to initialize a new
	 *              ContainerConfig.
	 * @return a new ContainerConfig object.
	 * @throws ConfigException when configuration is wrong.
	 */
	public static ContainerConfig fromConfig(ConfigParams config) throws ConfigException {
		ContainerConfig result = new ContainerConfig();
		if (config == null)
			return result;

		for (String section : config.getSectionNames()) {
			ConfigParams componentConfig = config.getSection(section);
			result.add(ComponentConfig.fromConfig(componentConfig));
		}

		return result;
	}
}
