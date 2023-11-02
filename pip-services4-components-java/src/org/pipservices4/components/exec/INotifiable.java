package org.pipservices4.components.exec;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Interface for components that can be asynchronously notified.
 * The notification may include optional argument that describe
 * the occurred event.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyComponent implements INotifable {
 *   ...
 *   public void notify(IContext context, Parameters args) {
 *     System.out.println("Occurred event " + args.getAsString("event"));
 *   }
 * }
 *
 * MyComponent myComponent = new MyComponent();
 *
 * myComponent.notify("123", Parameters.fromTuples("event", "Test Event"));
 * }
 * </pre>
 *
 * @see Notifier
 * @see IExecutable
 */
public interface INotifiable {
    /**
     * Notifies the component about occurred event.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param args          notification arguments.
     * @throws ApplicationException when errors occurred.
     */
    void notify(IContext context, Parameters args) throws ApplicationException;
}
