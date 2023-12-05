package org.pipservices4.aws.controllers;

import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.rpc.commands.CommandSet;
import org.pipservices4.rpc.commands.ICommand;
import org.pipservices4.rpc.commands.ICommandable;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.rpc.trace.InstrumentTiming;

/**
 * Abstract controller that receives commands via AWS Lambda protocol
 * to operations automatically generated for commands defined in <a href="https://pip-services4-java.github.io/pip-services4-rpc-java/org/pipservices4/rpc/commands/ICommandable.html">ICommandable</a> components.
 * Each command is exposed as invoke method that receives command name and parameters.
 * <p>
 * Commandable services require only 3 lines of code to implement a robust external
 * HTTP-based remote interface.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>dependencies:
 *   <ul>
 *   <li>service:            override for Controller dependency
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0           (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyCommandableLambdaController extends CommandableLambdaController {
 *    public MyCommandableLambdaController() {
 *       super();
 *       this._dependencyResolver.put(
 *           "service",
 *           new Descriptor("mygroup","service","*","*","1.0")
 *       );
 *    }
 * }
 *
 * MyCommandableLambdaController controller = new MyCommandableLambdaController();
 * controller.configure(ConfigParams.fromTuples(
 *     "connection.protocol", "http",
 *     "connection.host", "localhost",
 *     "connection.port", 8080
 * ));
 * controller.setReferences(References.fromTuples(
 *    new Descriptor("mygroup","service","default","default","1.0"), service
 * ));
 *
 * controller.open("123");
 * System.out.println("The REST controller is running on port 8080");
 * }
 * </pre>
 *
 * @see LambdaController
 */
public class CommandableLambdaController extends LambdaController {
    protected CommandSet _commandSet;

    /**
     * Creates a new instance of the controller.
     *
     * @param name a controller name.
     */
    public CommandableLambdaController(String name) {
        super(name);
        _dependencyResolver.put("service", "none");
    }

    /**
     * Registers all actions in AWS Lambda function.
     */
    @Override
    public void register() throws ReferenceException {
        var service = (ICommandable)_dependencyResolver.getOneRequired("service");

        _commandSet = service.getCommandSet();
        var commands = _commandSet.getCommands();

        for (ICommand command : commands) {
            registerAction(command.getName(), null, (params) -> {
                var context = params != null ? Context.fromTraceId(params.get("trace_id").toString()) : null;
                var args = Parameters.fromValue(params);
                args.remove("trace_id");
                InstrumentTiming timing = this.instrument(context, command.getName());
                try {
                    var res = command.execute(context, args);
                    timing.endTiming();
                    return res;
                } catch (Exception ex) {
                    timing.endFailure(ex);
                    return ex;
                }
            });
        }
    }
}
