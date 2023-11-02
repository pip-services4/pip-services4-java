package org.pipservices4.components.run;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Helper class that cleans stored object state.
 *
 * @see ICleanable
 */
public class Cleaner {
    /**
     * Clears state of specific component.
     * <p>
     * To be cleaned state components must implement ICleanable interface. If they
     * don't the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     the component that is to be cleaned.
     * @throws ApplicationException when errors occurred.
     * @see ICleanable
     */
    public static void clearOne(IContext context, Object component) throws ApplicationException {

        if (component instanceof ICleanable)
            ((ICleanable) component).clear(context);
    }

    /**
     * Clears state of multiple components.
     * <p>
     * To be cleaned state components must implement ICleanable interface. If they
     * don't the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param components    the list of components that are to be cleaned.
     * @throws ApplicationException when errors occurred.
     * @see Cleaner#clearOne(IContext, Object)
     * @see ICleanable
     */
    public static void clear(IContext context, Iterable<Object> components) throws ApplicationException {

        if (components == null)
            return;

        for (Object component : components)
            clearOne(context, component);
    }
}
