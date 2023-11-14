package org.pipservices4.rpc.commands;

import java.util.*;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.IExecutable;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.data.validate.*;

/**
 * Concrete implementation of {@link ICommand} interface. Command allows to call a method
 * or function using Command pattern.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Command command = new Command("add", null, (args) -> {
 *     float param1 = args.getAsFloat("param1");
 *     float param2 = args.getAsFloat("param2");
 *     return param1 + param2;
 * });
 *
 * Object result = command.execute(
 *   "123",
 *   Parameters.fromTuples(
 *     "param1", 2,
 *     "param2", 2
 *   )
 * );
 *
 * System.out.println(result.toString());
 *
 * // Console output: 4
 * }
 * </pre>
 *
 * @see ICommand
 * @see CommandSet
 */

public class Command implements ICommand {
    private final String _name;
    private final Schema _schema;
    private final IExecutable _function;

    /**
     * Creates a new command object and assigns it's parameters.
     *
     * @param name     the name of the command
     * @param schema   a validation schema for command arguments
     * @param function an execution function to be wrapped into this command.
     */
    public Command(String name, Schema schema, IExecutable function) {
        if (name == null)
            throw new NullPointerException("Command name is not set");
        if (function == null)
            throw new NullPointerException("Command function is not set");

        _name = name;
        _schema = schema;
        _function = function;
    }

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Executes the command. Before execution is validates Parameters args using the
     * defined schema. The command execution intercepts ApplicationException raised
     * by the called function and throws them.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param args          the parameters (arguments) to pass to this command for
     *                      execution.
     * @return execution result.
     * @throws ApplicationException when execution fails for whatever reason.
     * @see Parameters
     */
    @Override
    public Object execute(IContext context, Parameters args) throws ApplicationException {
        if (_schema != null)
            _schema.validateAndThrowException(context != null ? ContextResolver.getTraceId(context) : null, args);

        try {
            return _function.execute(context, args);
        } catch (Throwable ex) {
            throw new InvocationException(context != null ? ContextResolver.getTraceId(context) : null, "EXEC_FAILED", "Execution " + _name + " failed: " + ex)
                    .withDetails("command", _name).wrap(ex);
        }
    }

    /**
     * Validates the command Parameters args before execution using the defined
     * schema.
     *
     * @param args the parameters (arguments) to validate using this command's
     *             schema.
     * @return a list ValidationResults or an empty list (if no schema is set).
     * @see Parameters
     * @see ValidationResult
     */
    @Override
    public List<ValidationResult> validate(Parameters args) {
        if (_schema != null)
            return _schema.validate(args);

        return new ArrayList<>();
    }
}
