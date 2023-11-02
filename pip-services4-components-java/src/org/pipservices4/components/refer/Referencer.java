package org.pipservices4.components.refer;

import org.pipservices4.commons.errors.ConfigException;

/**
 * Helper class that sets and unsets references to components.
 *
 * @see IReferenceable
 * @see IUnreferenceable
 */
public class Referencer {
    /**
     * Sets references to specific component.
     * <p>
     * To set references components must implement IReferenceable interface. If they
     * don't the call to this method has no effect.
     *
     * @param references the references to be set.
     * @param component  the component to set references to.
     * @throws ReferenceException when no references found.
     * @throws ConfigException    when configuration is wrong.
     * @see IReferenceable
     */
    public static void setReferencesForOne(IReferences references, Object component)
            throws ReferenceException, ConfigException {

        if (component instanceof IReferenceable)
            ((IReferenceable) component).setReferences(references);
    }

    /**
     * Sets references to multiple components.
     * <p>
     * To set references components must implement IReferenceable interface. If they
     * don't the call to this method has no effect.
     *
     * @param references the references to be set.
     * @param components a list of components to set the references to.
     * @throws ReferenceException when no references found.
     * @throws ConfigException    when configuration is wrong.
     * @see IReferenceable
     */
    public static void setReferences(IReferences references, Iterable<Object> components)
            throws ReferenceException, ConfigException {

        for (Object component : components)
            setReferencesForOne(references, component);
    }

    /**
     * Unsets references in specific component.
     * <p>
     * To unset references components must implement IUnreferenceable interface. If
     * they don't the call to this method has no effect.
     *
     * @param component the component to unset references.
     * @see IUnreferenceable
     */
    public static void unsetReferencesForOne(Object component) {
        if (component instanceof IUnreferenceable)
            ((IUnreferenceable) component).unsetReferences();
    }

    /**
     * Unsets references in multiple components.
     * <p>
     * To unset references components must implement IUnreferenceable interface. If
     * they don't the call to this method has no effect.
     *
     * @param components the list of components, whose references must be cleared.
     * @see IUnreferenceable
     */
    public static void unsetReferences(Iterable<Object> components) {
        for (Object component : components)
            unsetReferencesForOne(component);
    }
}
