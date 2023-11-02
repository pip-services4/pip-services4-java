package org.pipservices4.components.context;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;

/**
 * Creates information components by their descriptors.
 *
 * @see Factory
 * @see ContextInfo
 */
public class DefaultContextFactory extends Factory {
    public final static Descriptor ContextInfoDescriptor = new Descriptor("pip-services", "context-info", "default", "*", "1.0");
    public final static Descriptor ContainerInfoDescriptor = new Descriptor("pip-services", "container-info", "default", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultContextFactory() {
        registerAsType(ContextInfoDescriptor, ContextInfo.class);
        registerAsType(ContainerInfoDescriptor, ContextInfo.class);
    }

}
