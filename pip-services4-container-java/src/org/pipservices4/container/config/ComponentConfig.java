package org.pipservices4.container.config;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.commons.reflect.*;

/**
 * Configuration of a component inside a container.
 * <p>
 * The configuration includes type information or descriptor,
 * and component configuration parameters.
 */
public class ComponentConfig {
    private Descriptor _descriptor;
    private TypeDescriptor _type;
    private ConfigParams _config;

    /**
     * Creates a new instance of the component configuration.
     */
    public ComponentConfig() {
    }

    /**
     * Creates a new instance of the component configuration.
     *
     * @param descriptor (optional) a components descriptor (locator).
     * @param type       (optional) a components type descriptor.
     * @param config     (optional) component configuration parameters.
     */
    public ComponentConfig(Descriptor descriptor, TypeDescriptor type, ConfigParams config) {
        _descriptor = descriptor;
        _type = type;
        _config = config;
    }

    public Descriptor getDescriptor() {
        return _descriptor;
    }

    public void setDescriptor(Descriptor value) {
        _descriptor = value;
    }

    public TypeDescriptor getType() {
        return _type;
    }

    public void setType(TypeDescriptor value) {
        _type = value;
    }

    public ConfigParams getConfig() {
        return _config;
    }

    public void setConfig(ConfigParams value) {
        _config = value;
    }

    /**
     * Creates a new instance of ComponentConfig based on section from container
     * configuration.
     *
     * @param config component parameters from container configuration
     * @return a newly created ComponentConfig
     * @throws ConfigException when neither component descriptor or type is found.
     */
    public static ComponentConfig fromConfig(ConfigParams config) throws ConfigException {
        Descriptor descriptor = Descriptor.fromString(config.getAsNullableString("descriptor"));
        TypeDescriptor type = TypeDescriptor.fromString(config.getAsNullableString("type"));

        if (descriptor == null && type == null)
            throw new ConfigException(null, "BAD_CONFIG", "Component configuration must have descriptor or type");

        return new ComponentConfig(descriptor, type, config);
    }
}
