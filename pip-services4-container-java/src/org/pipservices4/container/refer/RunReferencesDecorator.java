package org.pipservices4.container.refer;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.*;
import org.pipservices4.components.context.IContext;

/**
 * References decorator that automatically opens to newly added components
 * that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/run/IOpenable.html">IOpenable</a> interface and closes removed components
 * that implement <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/run/IClosable.html">IClosable</a> interface.
 */
public class RunReferencesDecorator extends ReferencesDecorator implements IOpenable {
    private boolean _opened = false;

    /**
     * Creates a new instance of the decorator.
     */
    public RunReferencesDecorator() {
        super();
    }

    /**
     * Creates a new instance of the decorator.
     *
     * @param baseReferences the next references or decorator in the chain.
     */
    public RunReferencesDecorator(IReferences baseReferences) {
        super(baseReferences);
    }

    /**
     * Creates a new instance of the decorator.
     *
     * @param nextReferences the next references or decorator in the chain.
     * @param topReferences  the decorator at the top of the chain.
     */
    public RunReferencesDecorator(IReferences nextReferences, IReferences topReferences) {
        super(nextReferences, topReferences);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _opened;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        if (!_opened) {
            List<Object> components = super.getAll();
            Opener.open(context, components);
            _opened = true;
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void close(IContext context) throws ApplicationException {
        if (_opened) {
            List<Object> components = super.getAll();
            Closer.close(context, components);
            _opened = false;
        }
    }

    /**
     * Puts a new reference into this reference map.
     *
     * @param locator   a locator to find the reference by.
     * @param component a component reference to be added.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void put(Object locator, Object component) throws ApplicationException {
        super.put(locator, component);

        if (_opened)
            Opener.openOne(null, component);
    }

    /**
     * Removes a previously added reference that matches specified locator. If many
     * references match the locator, it removes only the first one. When all
     * references shall be removed, use removeAll() method instead.
     *
     * @param locator a locator to remove reference
     * @return the removed component reference.
     * @throws ApplicationException when error occured.
     * @see #removeAll(Object)
     */
    @Override
    public Object remove(Object locator) throws ApplicationException {
        Object component = super.remove(locator);

        if (_opened)
            Closer.closeOne(null, component);

        return component;
    }

    /**
     * Removes all component references that match the specified locator.
     *
     * @param locator the locator to remove references by.
     * @return a list, containing all removed references.
     * @throws ApplicationException when error occured.
     */
    @Override
    public List<Object> removeAll(Object locator) throws ApplicationException {
        List<Object> components = super.removeAll(locator);

        if (_opened)
            Closer.close(null, components);

        return components;
    }
}
