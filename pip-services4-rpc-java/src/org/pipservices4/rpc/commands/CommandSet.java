package org.pipservices4.rpc.commands;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.*;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.data.validate.*;

/**
 * Contains a set of commands and events supported by a {@link ICommandable} commandable object.
 * The CommandSet supports command interceptors to extend and the command call chain.
 * <p>
 * CommandSets can be used as alternative commandable interface to a business object.
 * It can be used to auto generate multiple external services for the business object
 * without writing much code.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * public class MyDataCommandSet extends CommandSet {
 *    private IMyDataService _service;
 * 
 *    public MyDataCommandSet(IMyDataService service) { // Any data service interface
 *      super();
 *      this._service = service;
 *      this.addCommand(this.makeGetMyDataCommand());
 *    }   
 * 
 *    private ICommand makeGetMyDataCommand() {
 *      return new Command(
 *        'get_mydata',
 *        null,
 *        (context, args) -> {
 *          String param = args.getAsString('param');
 *          return this._service.getMyData(context, param);
 *        }
 *      );
 *    }
 * }
 * }
 * </pre>
 * 
 * @see Command
 * @see Event
 * @see ICommandable
 */
public class CommandSet {
	private final List<ICommand> _commands = new ArrayList<>();
	private final List<IEvent> _events = new ArrayList<>();
	private final List<ICommandInterceptor> _interceptors = new ArrayList<>();
	private final Map<String, ICommand> _commandsByName = new HashMap<>();
	private final Map<String, IEvent> _eventsByName = new HashMap<>();


	/**
	 * Creates an empty CommandSet object.
	 */
	public CommandSet() {
	}

	/**
	 * Gets all commands registered in this command set.
	 * 
	 * @return a list of commands.
	 * 
	 * @see ICommand
	 */
	public List<ICommand> getCommands() {
		return _commands;
	}

	/**
	 * Gets all events registered in this command set.
	 * 
	 * @return a list of events.
	 * 
	 * 
	 */
	public List<IEvent> getEvents() {
		return _events;
	}

	/**
	 * Searches for a command by its name.
	 * 
	 * @param commandName the name of the command to search for.
	 * @return the command, whose name matches the provided name.
	 * 
	 * @see ICommand
	 */
	public ICommand findCommand(String commandName) {
		return _commandsByName.get(commandName);
	}

	/**
	 * Searches for an event by its name in this command set.
	 * 
	 * @param eventName the name of the event to search for.
	 * @return the event, whose name matches the provided name.
	 * 
	 * @see IEvent
	 */
	public IEvent findEvent(String eventName) {
		return _eventsByName.get(eventName);
	}

	/**
	 * Builds execution chain including all interceptors and the specified command.
	 * 
	 * @param command the command to build a chain.
	 */
	private void buildCommandChain(ICommand command) {
		ICommand next = command;
		for (int i = _interceptors.size() - 1; i >= 0; i--)
			next = new InterceptedCommand(_interceptors.get(i), next);

		_commandsByName.put(next.getName(), next);
	}

	/**
	 * Rebuilds execution chain for all registered commands. This method is
	 * typically called when interceptors are changed. Because of that it is more
	 * efficient to register interceptors before registering commands (typically it
	 * will be done in abstract classes). However, that performance penalty will be
	 * only once during creation time.
	 */
	private void rebuildAllCommandChains() {
		_commandsByName.clear();
		for (ICommand command : _commands)
			buildCommandChain(command);
	}

	/**
	 * Adds a ICommand command to this command set.
	 * 
	 * @param command the command to add.
	 * 
	 * @see ICommand
	 */
	public void addCommand(ICommand command) {
		_commands.add(command);
		buildCommandChain(command);
	}

	/**
	 * Adds multiple ICommand commands to this command set.
	 * 
	 * @param commands the array of commands to add.
	 * 
	 * @see ICommand
	 */
	public void addCommands(List<ICommand> commands) {
		for (ICommand command : commands)
			addCommand(command);
	}

	/**
	 * Adds an IEvent event to this command set.
	 * 
	 * @param event the event to add.
	 * 
	 * @see IEvent
	 */
	public void addEvent(IEvent event) {
		_events.add(event);
		_eventsByName.put(event.getName(), event);
	}

	/**
	 * Adds multiple IEvent events to this command set.
	 * 
	 * @param events the array of events to add.
	 * 
	 * @see IEvent
	 */
	public void addEvents(List<IEvent> events) {
		for (IEvent event : events) {
			addEvent(event);
		}
	}

	/**
	 * Adds all of the commands and events from specified CommandSet command set
	 * into this one.
	 * 
	 * @param commandSet the CommandSet to add.
	 */
	public void addCommandSet(CommandSet commandSet) {
		for (ICommand command : commandSet.getCommands()) {
			addCommand(command);
		}

		for (IEvent event : commandSet.getEvents()) {
			addEvent(event);
		}
	}

	/**
	 * Adds a IEventListener listener to receive notifications on fired events.
	 * 
	 * @param listener the listener to add.
	 * 
	 * @see IEventListener
	 */
	public void addListener(IEventListener listener) {
		for (IEvent event : _events) {
			event.addListener(listener);
		}
	}

	/**
	 * Removes previosly added IEventListener listener.
	 * 
	 * @param listener the listener to remove.
	 * 
	 * @see IEventListener
	 */
	public void removeListener(IEventListener listener) {
		for (IEvent event : _events) {
			event.removeListener(listener);
		}
	}

	/**
	 * Adds a ICommandInterceptor command interceptor to this command set.
	 * 
	 * @param interceptor the interceptor to add.
	 * 
	 * @see ICommandInterceptor
	 */
	public void addInterceptor(ICommandInterceptor interceptor) {
		_interceptors.add(interceptor);
		rebuildAllCommandChains();
	}

	/**
	 * Executes a ICommand command specified by its name.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param commandName   the name of that command that is to be executed.
	 * @param args          the parameters (arguments) to pass to the command for
	 *                      execution.
	 * @return the execution result.
	 * @throws ApplicationException when execution fails for any reason.
	 * 
	 * @see ICommand
	 * @see Parameters
	 */
	public Object execute(IContext context, String commandName, Parameters args) throws ApplicationException {
		ICommand cref = findCommand(commandName);
		String traceId = ContextResolver.getTraceId(context);
		if (cref == null) {
			throw new BadRequestException(traceId, "CMD_NOT_FOUND", "Requested command does not exist")
					.withDetails("command", commandName);
		}

		if (traceId == null)
			traceId = IdGenerator.nextShort();

		List<ValidationResult> results = cref.validate(args);
		ValidationException.throwExceptionIfNeeded(traceId, results, false);

		return cref.execute(context != null ? context : Context.fromTraceId(traceId), args);
	}

	/**
	 * Validates Parameters args for command specified by its name using defined
	 * schema. If validation schema is not defined than the methods returns no
	 * errors. It returns validation error if the command is not found.
	 * 
	 * @param commandName the name of the command for which the 'args' must be
	 *                    validated.
	 * @param args        the parameters (arguments) to validate.
	 * @return an array of ValidationResults. If no command is found by the given
	 *          name, then the returned array of ValidationResults will contain a
	 *          single entry, whose type will be ValidationResultType.Error.
	 * 
	 * @see Command
	 * @see Parameters
	 * @see ValidationResult
	 */
	public List<ValidationResult> validate(String commandName, Parameters args) {
		ICommand cref = findCommand(commandName);
		if (cref == null) {
			List<ValidationResult> results = new ArrayList<>();
			results.add(new ValidationResult(null, ValidationResultType.Error, "CMD_NOT_FOUND",
					"Requested command does not exist", null, null));
			return results;
		}
		return cref.validate(args);
	}

	/**
	 * Fires event specified by its name and notifies all registered IEventListener
	 * listeners
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param eventName     the name of the event that is to be fired.
	 * @param args          the event arguments (parameters).
	 * 
	 * @throws ApplicationException when execution fails for any reason.
	 */
	public void notify(IContext context, String eventName, Parameters args) throws ApplicationException {
		IEvent e = findEvent(eventName);
		if (e != null) {
			e.notify(context, args);
		}
	}

}
