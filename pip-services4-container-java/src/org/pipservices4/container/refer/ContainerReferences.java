package org.pipservices4.container.refer;

import org.pipservices4.components.build.*;
import org.pipservices4.components.config.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.commons.reflect.*;
import org.pipservices4.container.config.*;

/**
 * Container managed references that can be created from container configuration.
 *
 * @see ManagedReferences
 */
public class ContainerReferences extends ManagedReferences {
    public ContainerReferences() {
    }

    private Object createStatically(Object locator) throws ReferenceException {
        Object component = _builder.create(locator);
        if (component == null)
            throw new ReferenceException(null, locator);
        return component;
    }

    /**
     * Puts components into the references from container configuration.
     *
     * @param config a container configuration with information of components to be
     *               added.
     * @throws ReferenceException when no found references.
     */
    public void putFromConfig(ContainerConfig config) throws ReferenceException {
        for (ComponentConfig componentConfig : config) {
            Object component = null;
            Object locator = null;

            try {
                // Create component dynamically
                if (componentConfig.getType() != null) {
                    locator = componentConfig.getType();
                    component = TypeReflector.createInstanceByDescriptor(componentConfig.getType());
                }
                // Or create component statically
                else if (componentConfig.getDescriptor() != null) {
                    locator = componentConfig.getDescriptor();
                    component = createStatically(componentConfig.getDescriptor());
                }

                // Check that component was created
                if (component == null) {
                    throw (CreateException) new CreateException("CANNOT_CREATE_COMPONENT", "Cannot create component")
                            .withDetails("config", config);
                }

                // Add component to the list
                _references.put(locator, component);

                // Configure component
                if (component instanceof IConfigurable)
                    ((IConfigurable) component).configure(componentConfig.getConfig());

                // Set references to factories
                if (component instanceof IFactory) {
                    ((IReferenceable) component).setReferences(this);
                }
            } catch (Exception ex) {
                throw (ReferenceException) new ReferenceException(null, locator).withCause(ex);
            }
        }
    }
}
