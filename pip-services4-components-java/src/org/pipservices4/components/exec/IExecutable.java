package org.pipservices4.components.exec;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.IContext;

/**
 * Interface for components that can be called to execute work.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class EchoComponent implements IExecutable {
 *   ...
 *   public void execute(IContext context, Parameters args) {
 *     Object result = args.getAsObject("message");
 *   }
 * }
 *
 * EchoComponent echo = new EchoComponent();
 * String message = "Test";
 * echo.execute("123", Parameters.fromTuples("message", message));
 * }
 * </pre>
 *
 * @see Executor
 * @see INotifiable
 * @see Parameters
 */
public interface IExecutable {
    /**
     * Executes component with arguments and receives execution result.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param args          execution arguments.
     * @return execution result.
     * @throws ApplicationException when errors occurred.
     */
    Object execute(IContext context, Parameters args) throws ApplicationException;
}
