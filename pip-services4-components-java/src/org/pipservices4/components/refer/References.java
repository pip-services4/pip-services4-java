package org.pipservices4.components.refer;

import java.util.*;

/**
 * The most basic implementation of {@link IReferences} to store and locate component references.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyController implements IReferenceable {
 *     public IMyPersistence _persistence;
 *     ...
 *     public void setReferences(IReferences references) {
 *       this._persistence = (IMyPersistence)references.getOneRequired(
 *         new Descriptor("mygroup", "persistence", "*", "*", "1.0")
 *       );
 *     }
 *     ...
 *  }
 *
 *  MyMongoDbPersistence persistence = new MyMongoDbPersistence();
 *
 *  MyController controller = new MyController();
 *
 *  References references = References.fromTuples(
 *    new Descriptor("mygroup", "persistence", "mongodb", "default", "1.0"), persistence,
 *    new Descriptor("mygroup", "controller", "default", "default", "1.0"), controller
 *  );
 *  controller.setReferences(references);
 *  }
 *  </pre>
 *
 * @see IReferences
 */
public class References implements IReferences {
    protected List<Reference> _references = new ArrayList<>();
    protected final Object _lock = new Object();

    public References() {
    }

    /**
     * Creates a new instance of references and initializes it with references.
     *
     * @param tuples (optional) a list of values where odd elements are locators and
     *               the following even elements are component references
     */
    public References(Object[] tuples) {
        if (tuples != null) {
            for (int index = 0; index < tuples.length; index += 2) {
                if (index + 1 >= tuples.length)
                    break;

                put(tuples[index], tuples[index + 1]);
            }
        }
    }

    /**
     * Puts a new reference into this reference map.
     *
     * @param locator   a locator to find the reference by.
     * @param component a component reference to be added.
     */
    public void put(Object locator, Object component) throws NullPointerException {
        if (component == null)
            throw new NullPointerException("Reference cannot be null");

        synchronized (_lock) {
            // Add reference to the set
            _references.add(new Reference(locator, component));
        }
    }

    /**
     * Removes a previously added reference that matches specified locator. If many
     * references match the locator, it removes only the first one. When all
     * references shall be removed, use removeAll() method instead.
     *
     * @param locator a locator to remove reference
     * @return the removed component reference.
     * @see #removeAll(Object)
     */
    public Object remove(Object locator) {
        if (locator == null)
            return null;

        synchronized (_lock) {
            for (int index = _references.size() - 1; index >= 0; index--) {
                Reference reference = _references.get(index);
                if (reference.match(locator)) {
                    _references.remove(index);
                    return reference.getComponent();
                }
            }
        }

        return null;
    }

    /**
     * Removes all component references that match the specified locator.
     *
     * @param locator the locator to remove references by.
     * @return a list, containing all removed references.
     */
    public List<Object> removeAll(Object locator) {
        List<Object> components = new ArrayList<>();

        if (locator == null)
            return components;

        synchronized (_lock) {
            for (int index = _references.size() - 1; index >= 0; index--) {
                Reference reference = _references.get(index);
                if (reference.match(locator)) {
                    _references.remove(index);
                    components.add(reference.getComponent());
                }
            }
        }

        return components;
    }

    /**
     * Gets locators for all registered component references in this reference map.
     *
     * @return a list with component locators.
     */
    public List<Object> getAllLocators() {
        List<Object> locators = new ArrayList<>();

        synchronized (_lock) {
            for (Reference reference : _references)
                locators.add(reference.getLocator());
        }

        return locators;
    }

    /**
     * Gets all component references registered in this reference map.
     *
     * @return a list with component references.
     */
    public List<Object> getAll() {
        List<Object> components = new ArrayList<>();

        synchronized (_lock) {
            for (Reference reference : _references)
                components.add(reference.getComponent());
        }

        return components;
    }

    /**
     * Gets an optional component reference that matches specified locator.
     *
     * @param locator the locator to find references by.
     * @return a matching component reference or null if nothing was found.
     */
    public Object getOneOptional(Object locator) {
        try {
            List<Object> components = find(Object.class, locator, false);
            return !components.isEmpty() ? components.get(0) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets an optional component reference that matches specified locator and
     * matching to the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find references by.
     * @return a matching component reference or null if nothing was found.
     */
    public <T> T getOneOptional(Class<T> type, Object locator) {
        try {
            List<T> components = find(type, locator, false);
            return !components.isEmpty() ? components.get(0) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets a required component reference that matches specified locator.
     *
     * @param locator the locator to find a reference by.
     * @return a matching component reference.
     * @throws ReferenceException when no references found.
     */
    public Object getOneRequired(Object locator) throws ReferenceException {
        List<Object> components = find(Object.class, locator, true);
        return !components.isEmpty() ? components.get(0) : null;
    }

    /**
     * Gets a required component reference that matches specified locator and
     * matching to the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find a reference by.
     * @return a matching component reference.
     * @throws ReferenceException when no references found.
     */
    public <T> T getOneRequired(Class<T> type, Object locator) throws ReferenceException {
        List<T> components = find(type, locator, true);
        return !components.isEmpty() ? components.get(0) : null;
    }

    /**
     * Gets all component references that match specified locator.
     *
     * @param locator the locator to find references by.
     * @return a list with matching component references or empty list if nothing
     * was found.
     */
    public List<Object> getOptional(Object locator) {
        try {
            return find(Object.class, locator, false);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets all component references that match specified locator and matching to
     * the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find references by.
     * @return a list with matching component references or empty list if nothing
     * was found.
     */
    public <T> List<T> getOptional(Class<T> type, Object locator) {
        try {
            return find(type, locator, false);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets all component references that match specified locator. At least one
     * component reference must be present. If it doesn't the method throws an
     * error.
     *
     * @param locator the locator to find references by.
     * @return a list with matching component references.
     * @throws ReferenceException when no references found.
     */
    public List<Object> getRequired(Object locator) throws ReferenceException {
        return find(Object.class, locator, true);
    }

    /**
     * Gets all component references that match specified locator. At least one
     * component reference must be present and matching to the specified type.
     * <p>
     * If it doesn't the method throws an error.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find references by.
     * @return a list with matching component references.
     * @throws ReferenceException when no references found.
     */
    public <T> List<T> getRequired(Class<T> type, Object locator) throws ReferenceException {
        return find(type, locator, true);
    }

    /**
     * Gets all component references that match specified locator.
     *
     * @param locator  the locator to find a reference by.
     * @param required forces to raise an exception if no reference is found.
     * @return a list with matching component references.
     * @throws ReferenceException when required is set to true but no references
     *                            found.
     */
    public List<Object> find(Object locator, boolean required) throws ReferenceException {
        return find(Object.class, locator, required);
    }

    /**
     * Gets all component references that match specified locator and matching to
     * the specified type.
     *
     * @param type     the Class type that defined the type of the result.
     * @param locator  the locator to find a reference by.
     * @param required forces to raise an exception if no reference is found.
     * @return a list with matching component references.
     * @throws ReferenceException when required is set to true but no references
     *                            found.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> type, Object locator, boolean required) throws ReferenceException {
        if (type == null)
            throw new NullPointerException("Type cannot be null");
        if (locator == null)
            throw new NullPointerException("Locator cannot be null");

        List<T> components = new ArrayList<>();

        synchronized (_lock) {
            // Search all references
            for (int index = _references.size() - 1; index >= 0; index--) {
                Reference reference = _references.get(index);
                if (reference.match(locator)) {
                    Object component = reference.getComponent();
                    if (type.isInstance(component)) {
                        components.add((T) component);
                    }
                }
            }
        }

        if (components.isEmpty() && required) {
            throw new ReferenceException(null, locator);
        }

        return components;
    }

    /**
     * Clear the references list
     */
    public void clear() {
        synchronized (_lock) {
            _references.clear();
        }
    }

    /**
     * Creates a new References from a list of key-value pairs called tuples.
     *
     * @param tuples a list of values where odd elements are locators and the
     *               following even elements are component references
     * @return a newly created References.
     * @throws ReferenceException when no references found.
     */
    public static References fromTuples(Object... tuples) throws ReferenceException {
        return new References(tuples);
    }
}
