package org.pipservices4.components.run;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Helper class that closes previously opened components.
 *
 * @see IClosable
 */
public class Closer {
    /**
     * Closes specific component.
     * <p>
     * To be closed components must implement ICloseable interface. If they don't
     * the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     the component that is to be closed.
     * @throws ApplicationException when error or null no errors occurred.
     * @see IClosable
     */
    public static void closeOne(IContext context, Object component) throws ApplicationException {
        if (component instanceof IClosable)
            ((IClosable) component).close(context);
    }

    /**
     * Closes multiple components.
     * <p>
     * To be closed components must implement ICloseable interface. If they
     * don't the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param components    the list of components that are to be closed.
     * @throws ApplicationException when error or null no errors occurred.
     * @see Closer#closeOne(IContext, Object)
     * @see IClosable
     */
    public static void close(IContext context, Iterable<Object> components) throws ApplicationException {
        if (components == null)
            return;

        for (Object component : components)
            closeOne(context, component);
    }
}
