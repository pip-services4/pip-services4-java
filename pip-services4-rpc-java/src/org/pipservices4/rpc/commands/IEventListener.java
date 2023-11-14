package org.pipservices4.rpc.commands;


import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;

/**
 * An interface for listener objects that receive notifications on fired events.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *  public class MyListener implements IEventListener {
 *     private void onEvent(IContext context, IEvent event,Parameters args) {
 *       System.out.println("Fired event " + event.getName());
 *     }
 *  }
 *
 *  Event event = new Event("myevent");
 *  event.addListener(new MyListener());
 *  event.notify("123", Parameters.fromTuples("param1", "ABC"));
 *
 *  // Console output: Fired event myevent
 * }
 * </pre>
 *
 * @see IEvent
 * @see Event
 */
public interface IEventListener {
    /**
     * A method called when events this listener is subscrubed to are fired.
     *
     * @param event         a fired event
     * @param context     (optional) a context to trace execution through call chain.
     * @param args          event arguments.
     */
    void onEvent(IContext context, IEvent event, Parameters args);
}