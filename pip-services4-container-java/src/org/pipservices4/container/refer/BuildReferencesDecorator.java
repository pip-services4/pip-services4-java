package org.pipservices4.container.refer;

import java.util.*;

import org.pipservices4.components.build.*;
import org.pipservices4.components.refer.*;

/**
 * References decorator that automatically creates missing components using
 * available component factories upon component retrival.
 */
public class BuildReferencesDecorator extends ReferencesDecorator {

    /**
     * Creates a new instance of the decorator.
     */
    public BuildReferencesDecorator() {
        super();
    }

    /**
     * Creates a new instance of the decorator.
     *
     * @param baseReferences the next references or decorator in the chain.
     */
    public BuildReferencesDecorator(IReferences baseReferences) {
        super(baseReferences);
    }

    /**
     * Creates a new instance of the decorator.
     *
     * @param baseReferences   the next references or decorator in the chain.
     * @param parentReferences the decorator at the top of the chain.
     */
    public BuildReferencesDecorator(IReferences baseReferences, IReferences parentReferences) {
        super(baseReferences, parentReferences);
    }


    /**
     * Finds a factory capable creating component by given descriptor from the
     * components registered in the references.
     *
     * @param locator a locator of component to be created.
     * @return found factory or null if factory was not found.
     */
    public IFactory findFactory(Object locator) {
        for (Object component : getAll()) {
            if (component instanceof IFactory factory) {
                if (factory.canCreate(locator) != null)
                    return factory;
            }
        }

        return null;
    }

    /**
     * Creates a component identified by given locator.
     *
     * @param locator a locator to identify component to be created.
     * @return the created component.
     * @see #findFactory(Object)
     */
    public Object create(Object locator) {
        // Find factory
        IFactory factory = findFactory(locator);
        if (factory == null)
            return null;

        try {
            // Create component
            return factory.create(locator);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Clarifies a component locator by merging two descriptors into one to replace
     * missing fields. That allows to get a more complete descriptor that includes
     * all possible fields.
     *
     * @param locator a component locator to clarify.
     * @param factory a factory that shall create the component.
     * @return clarified component descriptor (locator)
     */
    public Object clarifyLocator(Object locator, IFactory factory) {
        if (factory == null)
            return locator;
        if (!(locator instanceof Descriptor descriptor))
            return locator;

        Object anotherLocator = factory.canCreate(locator);
        if (anotherLocator == null)
            return locator;
        if (!(anotherLocator instanceof Descriptor anotherDescriptor))
            return locator;

        return new Descriptor(descriptor.getGroup() != null ? descriptor.getGroup() : anotherDescriptor.getGroup(),
                descriptor.getType() != null ? descriptor.getType() : anotherDescriptor.getType(),
                descriptor.getKind() != null ? descriptor.getKind() : anotherDescriptor.getKind(),
                descriptor.getName() != null ? descriptor.getName() : anotherDescriptor.getName(),
                descriptor.getVersion() != null ? descriptor.getVersion() : anotherDescriptor.getVersion());
    }

    /**
     * Gets all component references that match specified locator.
     *
     * @param type     the class type.
     * @param locator  the locator to find a reference by.
     * @param required forces to raise an exception if no reference is found.
     * @return a list with matching component references.
     * @throws ReferenceException when required is set to true but no references
     *                            found.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> find(Class<T> type, Object locator, boolean required) throws ReferenceException {
        List<T> components = super.find(type, locator, false);

        // Try to create component
        if (components.isEmpty() && required) {
            Object component = create(locator);
            if (type.isInstance(component)) {
                try {
                    getTopReferences().put(locator, component);
                    components.add((T) component);
                } catch (Exception ex) {
                    // Ignore exception
                }
            }
        }

        // Throw exception is no required components found
        if (required && components.isEmpty())
            throw new ReferenceException(locator);

        return components;
    }
}
