package org.pipservices4.components.refer;

import java.util.*;

import org.pipservices4.commons.errors.*;

/**
 * Interface for a map that holds component references and passes them to components
 * to establish dependencies with each other.
 * <p>
 * Together with {@link IReferenceable} and {@link IUnreferenceable} interfaces it implements
 * a Locator pattern that is used by PipServices toolkit for Inversion of Control
 * to assign external dependencies to components.
 * <p>
 * The {@link IReferences} object is a simple map, where keys are locators
 * and values are component references. It allows to add, remove and find components
 * by their locators. Locators can be any values like integers, strings or component types.
 * But most often PipServices toolkit uses {@link Descriptor} as locators that match
 * by 5 fields: group, type, kind, name and version.
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
 *    }
 * </pre>
 *
 * @see Descriptor
 * @see References
 */
public interface IReferences {
    /**
     * Puts a new reference into this reference map.
     *
     * @param locator   a locator to find the reference by.
     * @param component a component reference to be added.
     * @throws ApplicationException when errors occurred.
     */
    void put(Object locator, Object component) throws ApplicationException;

    /**
     * Removes a previously added reference that matches specified locator. If many
     * references match the locator, it removes only the first one. When all
     * references shall be removed, use removeAll() method instead.
     *
     * @param locator a locator to remove reference
     * @return the removed component reference.
     * @throws ApplicationException when errors occurred.
     * @see #removeAll(Object)
     */
    Object remove(Object locator) throws ApplicationException;

    /**
     * Removes all component references that match the specified locator.
     *
     * @param locator the locator to remove references by.
     * @return a list, containing all removed references.
     * @throws ApplicationException when errors occurred.
     */
    List<Object> removeAll(Object locator) throws ApplicationException;

    /**
     * Gets locators for all registered component references in this reference map.
     *
     * @return a list with component locators.
     */
    List<Object> getAllLocators();

    /**
     * Gets all component references registered in this reference map.
     *
     * @return a list with component references.
     */
    List<Object> getAll();

    /**
     * Gets all component references that match specified locator.
     *
     * @param locator the locator to find references by.
     * @return a list with matching component references or empty list if nothing
     * was found.
     */
    List<Object> getOptional(Object locator);

    /**
     * Gets all component references that match specified locator and matching to
     * the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find references by.
     * @return a list with matching component references or empty list if nothing
     * was found.
     */
    <T> List<T> getOptional(Class<T> type, Object locator);

    /**
     * Gets all component references that match specified locator. At least one
     * component reference must be present. If it doesn't the method throws an
     * error.
     *
     * @param locator the locator to find references by.
     * @return a list with matching component references.
     * @throws ReferenceException when no references found.
     */
    List<Object> getRequired(Object locator) throws ReferenceException;

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
    <T> List<T> getRequired(Class<T> type, Object locator) throws ReferenceException;

    /**
     * Gets an optional component reference that matches specified locator.
     *
     * @param locator the locator to find references by.
     * @return a matching component reference or null if nothing was found.
     */
    Object getOneOptional(Object locator);

    /**
     * Gets an optional component reference that matches specified locator and
     * matching to the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find references by.
     * @return a matching component reference or null if nothing was found.
     */
    <T> T getOneOptional(Class<T> type, Object locator);

    /**
     * Gets a required component reference that matches specified locator.
     *
     * @param locator the locator to find a reference by.
     * @return a matching component reference.
     * @throws ReferenceException when no references found.
     */
    Object getOneRequired(Object locator) throws ReferenceException;

    /**
     * Gets a required component reference that matches specified locator and
     * matching to the specified type.
     *
     * @param type    the Class type that defined the type of the result.
     * @param locator the locator to find a reference by.
     * @return a matching component reference.
     * @throws ReferenceException when no references found.
     */
    <T> T getOneRequired(Class<T> type, Object locator) throws ReferenceException;

    /**
     * Gets all component references that match specified locator.
     *
     * @param locator  the locator to find a reference by.
     * @param required forces to raise an exception if no reference is found.
     * @return a list with matching component references.
     * @throws ReferenceException when required is set to true but no references
     *                            found.
     */
    List<Object> find(Object locator, boolean required) throws ReferenceException;

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
    <T> List<T> find(Class<T> type, Object locator, boolean required) throws ReferenceException;
}
