package org.pipservices4.container.test;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;

/**
 * Creates test components by their descriptors.
 *
 * @see Factory
 * @see Shutdown
 */
public class DefaultTestFactory extends Factory {
    private static final Descriptor ShutdownDescriptor = new Descriptor("pip-services", "shutdown", "*", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultTestFactory() {
        this.registerAsType(DefaultTestFactory.ShutdownDescriptor, Shutdown.class);
    }
}
