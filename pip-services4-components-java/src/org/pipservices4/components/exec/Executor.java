package org.pipservices4.components.exec;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Helper class that executes components.
 *
 * @see IExecutable
 */
public class Executor {
    /**
     * Executes specific component.
     * <p>
     * To be executed components must implement IExecutable interface. If they don't
     * the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     the component that is to be executed.
     * @param args          execution arguments.
     * @return execution result.
     * @throws ApplicationException when errors occurred.
     * @see IExecutable
     * @see Parameters
     */
    public static Object executeOne(IContext context, Object component, Parameters args)
            throws ApplicationException {

        if (component instanceof IExecutable)
            return ((IExecutable) component).execute(context, args);
        else
            return null;
    }

    /**
     * Executes multiple components.
     * <p>
     * To be executed components must implement IExecutable interface. If they don't
     * the call to this method has no effect.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param components    a list of components that are to be executed.
     * @param args          execution arguments.
     * @return execution result.
     * @throws ApplicationException when errors occurred.
     * @see Executor#executeOne(IContext, Object, Parameters)
     * @see IExecutable
     * @see Parameters
     */
    public static List<Object> execute(IContext context, Iterable<Object> components, Parameters args)
            throws ApplicationException {

        List<Object> results = new ArrayList<Object>();
        if (components == null)
            return results;

        for (Object component : components) {
            if (component instanceof IExecutable)
                results.add(executeOne(context, component, args));
        }

        return results;
    }
}
