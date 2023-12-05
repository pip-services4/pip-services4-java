package org.pipservices4.aws.containers;

import org.pipservices4.aws.clients.LambdaClient;
import org.pipservices4.aws.controllers.ILambdaController;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.UnknownException;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.rpc.commands.CommandSet;
import org.pipservices4.rpc.commands.ICommandable;
import org.pipservices4.rpc.trace.InstrumentTiming;

/**
 * Abstract AWS Lambda function, that acts as a container to instantiate and run components
 * and expose them via external entry point. All actions are automatically generated for commands
 * defined in <a href="https://pip-services4-java.github.io/pip-services4-rpc-java/org/pipservices4/rpc/commands/ICommandable.html">ICommandable</a> components.
 * <p>
 * Container configuration for this Lambda function is stored in <code>"./config/config.yml"</code> file.
 * But this path can be overriden by <code>CONFIG_PATH</code> environment variable.
 *
 * Note: This component has been deprecated. Use LambdaController instead.
 * <p>
 * ### References ###
 * <p>
 * <ul>
 * <li>*:logger:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a>
 * components to pass log messages
 * <li>*:counters:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a>
 * components to pass collected measurements
 * <li>*:controller:awslambda:\*:1.0</code>       (optional) {@link ILambdaController} controllers to handle action requests
 * <li>*:controller:commandable-awslambda:\*:1.0</code> (optional) {@link ILambdaController} controllers to handle action requests
 * </ul>
 *
 * @see LambdaClient
 *
 * ### Example ###
 * <p>
 *     class MyLambdaFunction extends CommandableLambdaFunction {
 *         private IMyService service;
 *         public MyLambdaFunction() {
 *             super("mygroup", "MyGroup lambda function");
 *             this._dependencyResolver.put(
 *                 "service",
 *                 new Descriptor("mygroup","service","*","*","1.0")
 *             );
 *         }
 *     }
 * <p>
 *     var lambda = new MyLambdaFunction();
 * <p>
 *     lambda.run();
 *     console.log("MyLambdaFunction is started");
 */
public class CommandableLambdaFunction extends LambdaFunction {

    /**
     * Creates a new instance of this lambda function.
     *
     * @param name        (optional) a container name (accessible via ContextInfo)
     * @param description (optional) a container description (accessible via ContextInfo)
     */
    public CommandableLambdaFunction(String name, String description) {
        super(name, description);
    }

    private void registerCommandSet(CommandSet commandSet) throws UnknownException {
        var commands = commandSet.getCommands();
        for (var index = 0; index < commands.size(); index++) {
            var command = commands.get(index);

            this.registerAction(command.getName(), null, (params) -> {
                var context = params != null ? Context.fromTraceId(params.get("trace_id").toString()) : null;
                var args = Parameters.fromValue(params);
                InstrumentTiming timing = this.instrument(context, this._info.getName() + '.' + command.getName());

            try {
                var result = command.execute(context, args);
                timing.endTiming();
                return result;
            } catch (Exception ex) {
                timing.endTiming(ex);
                return ex;
            }
            });
        }
    }

    /**
     * Registers all actions in this lambda function.
     */
    public void register() throws ReferenceException {
        ICommandable service = this._dependencyResolver.getOneRequired(ICommandable.class, "service");
        var commandSet = service.getCommandSet();
        try {
            registerCommandSet(commandSet);
        } catch (UnknownException e) {
            throw new RuntimeException(e);
        }
    }
}
