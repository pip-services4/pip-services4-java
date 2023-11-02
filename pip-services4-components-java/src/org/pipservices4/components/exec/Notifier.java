package org.pipservices4.components.exec;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Helper class that notifies components.
 *
 * @see INotifiable
 */
public class Notifier {
    /**
     * Notifies specific component.
     * <p>
     * To be notified components must implement INotifiable interface. If they don't
     * the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     the component that is to be notified.
     * @param args          notification arguments.
     * @throws ApplicationException when errors occurred.
     * @see INotifiable
     */
    public static void notifyOne(IContext context, Object component, Parameters args) throws ApplicationException {

        if (component instanceof INotifiable)
            ((INotifiable) component).notify(context, args);
    }

    /**
     * Notifies multiple components.
     * <p>
     * To be notified components must implement INotifiable interface. If they don't
     * the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param components    a list of components that are to be notified.
     * @param args          notification arguments.
     * @throws ApplicationException when errors occurred.
     * @see Notifier#notifyOne(IContext, Object, Parameters)
     * @see INotifiable
     */
    public static void notify(IContext context, Iterable<Object> components, Parameters args)
            throws ApplicationException {

        if (components == null)
            return;

        for (Object component : components)
            notifyOne(context, component, args);
    }
}
