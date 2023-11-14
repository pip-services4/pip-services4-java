package org.pipservices4.rpc.commands;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.*;

import java.util.List;

/**
 * An interface for stackable command interceptors, which can extend
 * and modify the command call chain.
 * <p>
 * This mechanism can be used for authentication, logging, and other functions.
 *
 * @see ICommand
 * @see InterceptedCommand
 */
public interface ICommandInterceptor {
    /**
     * Gets the name of the wrapped command.
     * <p>
     * The interceptor can use this method to override the command name. Otherwise
     * it shall just delegate the call to the wrapped command.
     *
     * @param command the next command in the call chain.
     * @return the name of the wrapped command.
     */
    String getName(ICommand command);

    /**
     * Executes the wrapped command with specified arguments.
     * <p>
     * The interceptor can use this method to intercept and alter the command
     * execution. Otherwise it shall just delete the call to the wrapped command.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param command       the next command in the call chain that is to be
     *                      executed.
     * @param args          the parameters (arguments) to pass to the command for
     *                      execution.
     * @return execution result.
     * @throws ApplicationException when execution fails for whatever reason.
     * @see Parameters
     */
    Object execute(IContext context, ICommand command, Parameters args) throws ApplicationException;

    /**
     * Validates arguments of the wrapped command before its execution.
     * <p>
     * The interceptor can use this method to intercept and alter validation of the
     * command arguments. Otherwise it shall just delegate the call to the wrapped
     * command.
     *
     * @param command the next command in the call chain to be validated against.
     * @param args    the parameters (arguments) to validate.
     * @return an list of ValidationResults.
     * @see Parameters
     * @see ValidationResult
     */
    List<ValidationResult> validate(ICommand command, Parameters args);
}
