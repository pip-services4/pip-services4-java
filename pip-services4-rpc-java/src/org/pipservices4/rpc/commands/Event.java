package org.pipservices4.rpc.commands;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;

/**
 * Concrete implementation of {@link IEvent} interface.
 * It allows to send asynchronous notifications to multiple subscribed listeners.
 * <p>
 * ### Example ###
 * <pre> 
 * {@code 
 * Event event = new Event("my_event");
 * event.addListener(myListener);
 * 
 * event.notify("123", Parameters.fromTuples(
 *   "param1", "ABC",
 *   "param2", 123
 * ));
 * }
 * </pre>
 * @see IEvent
 * @see IEventListener
 */
public class Event implements IEvent {
	private final String _name;
	private final List<IEventListener> _listeners = new ArrayList<>();

	/**
	 * Creates a new event and assigns its name.
	 * 
	 * @param name the name of the event that is to be created.
	 * @throws NullPointerException an Error if the name is null.
	 */
	public Event(String name) {
		if (name == null)
			throw new NullPointerException("Event name is not set");

		_name = name;
	}

	/**
	 * Gets the name of the event.
	 * 
	 * @return the name of this event.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Gets all listeners registered in this event.
	 * 
	 * @return a list of listeners.
	 */
	public List<IEventListener> getListeners() {
		return _listeners;
	}

	/**
	 * Adds a listener to receive notifications when this event is fired.
	 * 
	 * @param listener the listener reference to add.
	 */
	public void addListener(IEventListener listener) {
		_listeners.add(listener);
	}

	/**
	 * Removes a listener, so that it no longer receives notifications for this
	 * event.
	 * 
	 * @param listener the listener reference to remove.
	 */
	public void removeListener(IEventListener listener) {
		_listeners.remove(listener);
	}

	/**
	 * Fires this event and notifies all registered listeners.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param args          the parameters to raise this event with.
	 * @throws ApplicationException if the event fails to be raised.
	 */
	public void notify(IContext context, Parameters args) throws ApplicationException {
		for (IEventListener listener : _listeners) {
			try {
				listener.onEvent(context, this, args);
			} catch (Exception ex) {
				throw new InvocationException(context != null ? ContextResolver.getTraceId(context) : null,
						"EXEC_FAILED", "Rasing event " + _name + " failed: " + ex)
						.withDetails("event", _name).wrap(ex);
			}
		}
	}
}
