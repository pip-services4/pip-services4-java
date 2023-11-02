package org.pipservices4.components.refer;

import java.util.*;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.convert.*;
import org.pipservices4.commons.errors.*;

/**
 * Helper class for resolving component dependencies.
 * <p>
 * The resolver is configured to resolve named dependencies by specific locator.
 * During deployment the dependency locator can be changed.
 * <p>
 * This mechanism can be used to clarify specific dependency among several alternatives.
 * Typically, components are configured to retrieve the first dependency that matches
 * logical group, type and version. But if container contains more than one instance
 * and resolution has to be specific about those instances, they can be given a unique
 * name and dependency resolvers can be reconfigured to retrieve dependencies by their name.
 * <p>
 * Configuration parameters:
 * <ul>
 *   <li>dependencies:
 *   <ul>
 *     <li>[dependency name 1]: [dependency 1 locator (descriptor)]
 *     <li>...
 *     <li>[dependency name N]: [dependency N locator (descriptor)]
 *   </ul>
 *
 *   <li>References:
 *   <ul>
 *   <li>[references that match configured dependencies]
 *   </ul>
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyComponent implements IConfigurable, IReferenceable {
 *   private DependencyResolver _dependencyResolver = new DependencyResolver();
 *   private IMyPersistence _persistence;
 *   ...
 *
 *   public MyComponent() {
 *     this._dependencyResolver.put("persistence", new Descriptor("mygroup", "persistence", "*", "*", "1.0"));
 *     }
 *
 *   public void configure(ConfigParams config) {
 *     this._dependencyResolver.configure(config);
 *   }
 *
 *   public void setReferences(IReferences references) {
 *     this._dependencyResolver.setReferences(references);
 *     this._persistence = (IMyPersistence)this._dependencyResolver.getOneRequired("persistence");
 *   }
 * }
 *
 * // Create mycomponent and set specific dependency out of many
 * MyComponent component = new MyComponent();
 * component.configure(ConfigParams.fromTuples(
 *   "dependencies.persistence", "mygroup:persistence:*:persistence2:1.0" // Override default persistence dependency
 * ));
 * component.setReferences(References.fromTuples(
 *   new Descriptor("mygroup","persistence","*","persistence1","1.0"), new MyPersistence(),
 *   new Descriptor("mygroup","persistence","*","persistence2","1.0"), new MyPersistence()  // This dependency shall be set
 * ));
 * }
 * </pre>
 *
 * @see IReferences
 */
public class DependencyResolver implements IReferenceable, IReconfigurable {
    private final Map<String, Object> _dependencies = new HashMap<>();
    private IReferences _references;

    /**
     * Creates a new instance of the dependency resolver.
     */
    public DependencyResolver() {

    }

    /**
     * Creates a new instance of the dependency resolver.
     *
     * @param config (optional) default configuration where key is dependency name
     *               and value is locator (descriptor)
     * @see ConfigParams
     * @see #configure(ConfigParams)
     * @see IReferences
     * @see #setReferences(IReferences)
     */
    public DependencyResolver(ConfigParams config) {
        try {
            configure(config);
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the component with specified parameters.
     *
     * @param config configuration parameters to set.
     * @throws ConfigException when configuration is wrong.
     * @see ConfigParams
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        ConfigParams dependencies = config.getSection("dependencies");
        for (String name : dependencies.keySet()) {
            String locator = dependencies.get(name);
            if (locator == null)
                continue;

            try {
                Descriptor descriptor = Descriptor.fromString(locator);
                _dependencies.put(name, Objects.requireNonNullElse(descriptor, locator));
            } catch (Exception ex) {
                _dependencies.put(name, locator);
            }
        }
    }

    /**
     * Sets the component references
     *
     * @param references references to set.
     */
    @Override
    public void setReferences(IReferences references) {
        _references = references;
    }

    /**
     * Adds a new dependency into this resolver.
     *
     * @param name    the dependency's name.
     * @param locator the locator to find the dependency by.
     */
    public void put(String name, Object locator) {
        _dependencies.put(name, locator);
    }

    /**
     * Gets a dependency locator by its name.
     *
     * @param name the name of the dependency to locate.
     * @return the dependency locator or null if locator was not configured.
     */
    private Object find(String name) {
        if (name == null)
            throw new NullPointerException("Dependency name cannot be null");
        if (_references == null)
            throw new NullPointerException("References shall be set");

        return _dependencies.get(name);
    }

    /**
     * Gets all optional dependencies by their name.
     *
     * @param name the dependency name to locate.
     * @return a list with found dependencies or empty list of no dependencies was
     * found.
     */
    public List<Object> getOptional(String name) {
        Object locator = find(name);
        return locator != null ? _references.getOptional(locator) : null;
    }

    /**
     * Gets all optional dependencies by their name.
     *
     * @param type the Class type that defined the type of the result.
     * @param name the dependency name to locate.
     * @return a list with found dependencies or empty list of no dependencies was
     * found.
     */
    public <T> List<T> getOptional(Class<T> type, String name) {
        Object locator = find(name);
        return locator != null ? _references.getOptional(type, locator) : null;
    }

    /**
     * Gets all required dependencies by their name. At least one dependency must
     * present. If no dependencies was found it throws a ReferenceException
     *
     * @param name the dependency name to locate.
     * @return a list with found dependencies.
     * @throws ReferenceException when no single component reference is found
     */
    public List<Object> getRequired(String name) throws ReferenceException {
        Object locator = find(name);
        if (locator == null)
            throw new ReferenceException(null, name);

        return _references.getRequired(locator);
    }

    /**
     * Gets all required dependencies by their name. At least one dependency must
     * present. If no dependencies was found it throws a ReferenceException
     *
     * @param type the Class type that defined the type of the result.
     * @param name the dependency name to locate.
     * @return a list with found dependencies.
     * @throws ReferenceException when no single component reference is found
     */
    public <T> List<T> getRequired(Class<T> type, String name) throws ReferenceException {
        Object locator = find(name);
        if (locator == null)
            throw new ReferenceException(null, name);

        return _references.getRequired(type, locator);
    }

    /**
     * Gets one optional dependency by its name.
     *
     * @param name the dependency name to locate.
     * @return a dependency reference or null of the dependency was not found
     */
    public Object getOneOptional(String name) {
        Object locator = find(name);
        return locator != null ? _references.getOneOptional(locator) : null;
    }

    /**
     * Gets one optional dependency by its name and matching to the specified type.
     *
     * @param type the Class type that defined the type of the result.
     * @param name the dependency name to locate.
     * @return a dependency reference or null of the dependency was not found
     */
    public <T> T getOneOptional(Class<T> type, String name) {
        Object locator = find(name);
        return locator != null ? _references.getOneOptional(type, locator) : null;
    }

    /**
     * Gets one required dependency by its name. At least one dependency must
     * present. If the dependency was found it throws a ReferenceException
     *
     * @param name the dependency name to locate.
     * @return a dependency reference
     * @throws ReferenceException if dependency was not found.
     */
    public Object getOneRequired(String name) throws ReferenceException {
        Object locator = find(name);
        if (locator == null)
            throw new ReferenceException(null, name);

        return _references.getOneRequired(locator);
    }

    /**
     * Gets one required dependency by its name and matching to the specified type.
     * At least one dependency must present. If the dependency was found it throws a
     * ReferenceException
     *
     * @param type the Class type that defined the type of the result.
     * @param name the dependency name to locate.
     * @return a dependency reference
     * @throws ReferenceException if dependency was not found.
     */
    public <T> T getOneRequired(Class<T> type, String name) throws ReferenceException {
        Object locator = find(name);
        if (locator == null)
            throw new ReferenceException(null, name);

        return _references.getOneRequired(type, locator);
    }

    /**
     * Finds all matching dependencies by their name.
     *
     * @param name     the dependency name to locate.
     * @param required true to raise an exception when no dependencies are found.
     * @return a list of found dependencies
     * @throws ReferenceException of required is true and no dependencies found.
     */
    public List<Object> find(String name, boolean required) throws ReferenceException {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException(name);
        }

        Object locator = find(name);
        if (locator == null) {
            if (required) {
                throw new ReferenceException(null, name);
            }
            return null;
        }
        return _references.find(locator, required);
    }

    /**
     * Finds all matching dependencies by their name and matching to the specified
     * type.
     *
     * @param type     the Class type that defined the type of the result.
     * @param name     the dependency name to locate.
     * @param required true to raise an exception when no dependencies are found.
     * @return a list of found dependencies
     * @throws ReferenceException of required is true and no dependencies found.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> type, String name, boolean required) throws ReferenceException {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException(name);
        }

        Object locator = find(name);
        if (locator == null) {
            if (required) {
                throw new ReferenceException(null, name);
            }
            return null;
        }
        return (List<T>) _references.find(locator, required);
    }

    /**
     * Creates a new DependencyResolver from a list of key-value pairs called tuples
     * where key is dependency name and value the dependency locator (descriptor).
     *
     * @param tuples a list of values where odd elements are dependency name and the
     *               following even elements are dependency locator (descriptor)
     * @return a newly created DependencyResolver.
     */
    public static DependencyResolver fromTuples(Object... tuples) {
        DependencyResolver result = new DependencyResolver();
        if (tuples == null || tuples.length == 0)
            return result;

        for (int index = 0; index < tuples.length; index += 2) {
            if (index + 1 >= tuples.length)
                break;

            String name = StringConverter.toString(tuples[index]);
            Object locator = tuples[index + 1];

            result.put(name, locator);
        }

        return result;
    }

    private Object locate(String name) throws Exception {
        if (name == null)
            throw new Exception("Dependency name cannot be null");
        if (this._references == null)
            throw new Exception("References shall be set");

        return this._dependencies.get(name);
    }

    /**
     * Creates a new DependencyResolver from a list of key-value pairs called tuples
     * where key is dependency name and value the dependency locator (descriptor).
     *
     * @param tuples a list of values where odd elements are dependency name and the following even elements are dependency locator (descriptor)
     * @return a newly created DependencyResolver.
     */
    @SafeVarargs
    public static DependencyResolver fromTuples(List<Object>... tuples) {
        DependencyResolver result = new DependencyResolver();
        if (tuples == null || tuples.length == 0)
            return result;

        for (int index = 0; index < tuples.length; index += 2) {
            if (index + 1 >= tuples.length) break;

            String name = StringConverter.toString(tuples[index]);
            Object locator = tuples[index + 1];

            result.put(name, locator);
        }

        return result;
    }
//	
//	public static boolean isNullOrWhiteSpace(String s) {
//        return s == null || isWhitespace(s);
//    }
//    
//    private static boolean isWhitespace(String s) {
//        int length = s.length();
//        if (length > 0) {
//            for (int i = 0; i < length; i++) {
//                if (!Character.isWhitespace(s.charAt(i))) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
}
