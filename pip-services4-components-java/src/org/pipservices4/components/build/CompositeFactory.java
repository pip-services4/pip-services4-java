package org.pipservices4.components.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates multiple factories into a single factory component.
 * When a new component is requested, it iterates through
 * factories to locate the one able to create the requested component.
 * <p>
 * This component is used to conveniently keep all supported factories in a single place.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * CompositeFactory factory = new CompositeFactory();
 * factory.add(new DefaultLoggerFactory());
 * factory.add(new DefaultCountersFactory());
 *
 * Descriptor loggerLocator = new Descriptor("*", "logger", "*", "*", "1.0");
 * factory.canCreate(loggerLocator);        // Result: Descriptor("pip-service", "logger", "null", "default", "1.0")
 * factory.create(loggerLocator);            // Result: created NullLogger
 * }
 * </pre>
 */
public class CompositeFactory implements IFactory {
    private final List<IFactory> _factories = new ArrayList<>();

    /**
     * Creates a new instance of the factory.
     */
    public CompositeFactory() {
    }

    /**
     * Creates a new instance of the factory.
     *
     * @param factories a list of factories to embed into this factory.
     */
    public CompositeFactory(IFactory... factories) {
        if (factories != null) {
            Collections.addAll(_factories, factories);
        }
    }

    /**
     * Adds a factory into the list of embedded factories.
     *
     * @param factory a factory to be added.
     */
    public void add(IFactory factory) {
        if (factory == null)
            throw new NullPointerException("Factory cannot be null");

        _factories.add(factory);
    }

    /**
     * Removes a factory from the list of embedded factories.
     *
     * @param factory the factory to remove.
     */
    public void remove(IFactory factory) {
        _factories.remove(factory);
    }

    /**
     * Checks if this factory is able to create component by given locator.
     * <p>
     * This method searches for all registered components and returns a locator for
     * component it is able to create that matches the given locator. If the factory
     * is not able to create a requested component is returns null.
     *
     * @param locator a locator to identify component to be created.
     * @return a locator for a component that the factory is able to create.
     */
    public Object canCreate(Object locator) {
        if (locator == null)
            throw new NullPointerException("Locator cannot be null");

        // Iterate from the latest factories
        for (int index = _factories.size() - 1; index >= 0; index--) {
            Object thisLocator = _factories.get(index).canCreate(locator);
            if (thisLocator != null)
                return thisLocator;
        }

        return null;
    }

    /**
     * Creates a component identified by given locator.
     *
     * @param locator a locator to identify component to be created.
     * @return the created component.
     * @throws CreateException if the factory is not able to create the component.
     */
    public Object create(Object locator) throws CreateException {
        if (locator == null)
            throw new NullPointerException("Locator cannot be null");

        // Iterate from the latest factories
        for (int index = _factories.size() - 1; index >= 0; index--) {
            IFactory factory = _factories.get(index);
            if (factory.canCreate(locator) != null)
                return factory.create(locator);
        }

        throw new CreateException(null, locator);
    }

}
