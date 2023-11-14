package org.pipservices4.rpc.commands;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.ValidationResult;

/**
 * Implements a {@link ICommand} command wrapped by an interceptor.
 * It allows to build command call chains. The interceptor can alter execution
 * and delegate calls to a next command, which can be intercepted or concrete.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * public class CommandLogger implements ICommandInterceptor {
 *
 *   public String getName(ICommand command) {
 *     return command.getName();
 *   }
 *
 *   public Object execute(IContext context, ICommand command, Parameters args) {
 *     System.out.println("Executed command " + command.getName());
 *     return command.execute(context, args);
 *   }
 *
 *   private List<ValidationResult> validate(ICommand command, Parameters args) {
 *     return command.validate(args);
 *   }
 * }
 *
 * CommandLogger logger = new CommandLogger();
 * InterceptedCommand loggedCommand = new InterceptedCommand(logger, command);
 *
 * // Each called command will output: Executed command <command name>
 * }
 * </pre>
 *
 * @see ICommand
 * @see ICommandInterceptor
 */
public class InterceptedCommand implements ICommand {
    private final ICommandInterceptor _interceptor;
    private final ICommand _next;

    /**
     * Creates a new InterceptedCommand, which serves as a link in an execution
     * chain. Contains information about the interceptor that is being used and the
     * next command in the chain.
     *
     * @param interceptor the interceptor that is intercepting the command.
     * @param next        (link to) the next command in the command's execution
     *                    chain.
     */
    public InterceptedCommand(ICommandInterceptor interceptor, ICommand next) {
        _interceptor = interceptor;
        _next = next;
    }

    /**
     * Gets the command name.
     *
     * @return the name of the command that is being intercepted.
     */
    public String getName() {
        return _interceptor.getName(_next);
    }

    /**
     * Executes the next command in the execution chain using the given Parameters
     * parameters (arguments).
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param args          the parameters (arguments) to pass to the command for
     *                      execution.
     * @return execution result.
     * @throws ApplicationException when execution fails for whatever reason.
     * @see Parameters
     */
    public Object execute(IContext context, Parameters args) throws ApplicationException {
        return _interceptor.execute(context, _next, args);
    }

    /**
     * Validates the Parameters args that are to be passed to the command that is
     * next in the execution chain.
     *
     * @param args the parameters (arguments) to validate for the next command.
     * @return an list of ValidationResults.
     * @see Parameters
     * @see ValidationResult
     */
    public List<ValidationResult> validate(Parameters args) {
        return _interceptor.validate(_next, args);
    }
}
