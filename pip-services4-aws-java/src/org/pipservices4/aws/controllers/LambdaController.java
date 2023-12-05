package org.pipservices4.aws.controllers;

import org.pipservices4.commons.errors.BadRequestException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.data.validate.Schema;
import org.pipservices4.data.validate.ValidationException;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;
import org.pipservices4.components.context.IContext;
import org.pipservices4.http.controllers.AuthorizeFunction;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Abstract controller that receives remove calls via AWS Lambda protocol.
 * <p>
 * This controller is intended to work inside LambdaFunction container that
 * exploses registered actions externally.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>dependencies:
 * <ul>
 * <li>controller:            override for Controller dependency
 * </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a>
 * components to pass log messages
 * <li>*:counters:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a>
 * components to pass collected measurements
 * </ul>
 * <p>
 * ### Example ###
 *
 * <pre>
 * {@code
 * class MyLambdaController extends LambdaController {
 *    private IMyService _service;
 *    ...
 *    public MyRestController() {
 *       super("v1.service");
 *       this._dependencyResolver.put(
 *           "service",
 *           new Descriptor("mygroup","service","*","*","1.0")
 *       );
 *    }
 *
 *    public void setReferences(IReferences references) {
 *       super.setReferences(references);
 *       this._service = (IMyService)this._dependencyResolver.getRequired("service");
 *    }
 *
 *    public void register() {
 *    this.registerAction("/get_mydata",
 *                 null,
 *                 this::getData
 *         );
 *        ...
 *    }
 * }
 *
 * MyLambdaController controller = new MyLambdaController();
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
 * System.out.println("The Lambda controller is running on port 8080");
 * }
 * </pre>
 */
public abstract class LambdaController
        implements ILambdaController, IOpenable, IConfigurable,
        IReferenceable {
    private boolean _opened;
    protected String _name;
    private List<LambdaAction> _actions = new ArrayList<>();
    private List<AuthorizeFunction<Map<String, Object>, Function<Map<String, Object>, ?>, ?>> _interceptors = new ArrayList<>();

    /**
     * The dependency resolver.
     */
    protected DependencyResolver _dependencyResolver = new DependencyResolver();
    /**
     * The logger.
     */
    protected CompositeLogger _logger = new CompositeLogger();
    /**
     * The performance counters.
     */
    protected CompositeCounters _counters = new CompositeCounters();

    /**
     * The tracer.
     */
    protected CompositeTracer _tracer = new CompositeTracer();


    public LambdaController(String name) {
        _name = name;
    }

    public LambdaController() {
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException when configuration is wrong.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        _dependencyResolver.configure(config);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     * @throws ConfigException    when configuration is wrong.
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        _logger.setReferences(references);
        _counters.setReferences(references);
        _tracer.setReferences(references);
        _dependencyResolver.setReferences(references);
    }

    /**
     * Get all actions supported by the controller.
     *
     * @return an array with supported actions.
     */
    public List<LambdaAction> getActions() {
        return this._actions;
    }

    /**
     * Adds instrumentation to log calls and measure call time.
     * It returns a Timing object that is used to end the time measurement.
     *
     * @param context (optional) a context to trace execution through call chain.
     * @param name    a method name.
     * @return Timing object to end the time measurement.
     */
    protected InstrumentTiming instrument(IContext context, String name) {
        this._logger.trace(context, "Executing %s method", name);
        this._counters.incrementOne(name + ".exec_count");

        var counterTiming = this._counters.beginTiming(name + ".exec_time");
        var traceTiming = this._tracer.beginTrace(context, name, null);
        return new InstrumentTiming(context, name, "exec",
                this._logger, this._counters, counterTiming, traceTiming);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _opened;
    }

    /**
     * Opens the component.
     *
     * @param context (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        if (isOpen())
            return;

        this.register();

        _opened = true;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void close(IContext context) throws ApplicationException {
        if (!_opened)
            return;

        _opened = false;
        _actions = new ArrayList<>();
        _interceptors = new ArrayList<>();
    }

    protected Function<Map<String, Object>, ?> applyValidation(Schema schema, Function<Map<String, Object>, ?> action) {
        // Create an action function
        Function<Map<String, Object>, ?> actionWrapper = (params) -> {
            // Validate object
            if (schema != null && params != null) {
                // Perform validation
                var traceId = params.get("trace_id").toString();
                try {
                    schema.validateAndThrowException(traceId, params, false);
                } catch (ValidationException ex) {
                    return ex;
                }
            }
            return action.apply(params);
        };

        return actionWrapper;
    }

    protected Function<Map<String, Object>, ?> applyInterceptors(Function<Map<String, Object>, ?> action) {
        var actionWrapper = action;

        for (var index = _interceptors.size() - 1; index >= 0; index--) {
            var interceptor = _interceptors.get(index);

            Function<Function<Map<String, Object>, ?>, Function<Map<String, Object>, ?>> func = (act) ->
                    (Map<String, Object> params) ->
                            interceptor.apply(params, act);

            actionWrapper = func.apply(actionWrapper);
        }

        return actionWrapper;
    }


    protected String generateActionCmd(String name) {
        String cmd = name;
        if (this._name != null) {
            cmd = this._name + "." + cmd;
        }
        return cmd;
    }

    /**
     * Registers a action in AWS Lambda function.
     *
     * @param name   an action name
     * @param schema a validation schema to validate received parameters.
     * @param action an action function that is called when operation is invoked.
     */
    protected void registerAction(String name, Schema schema, Function<Map<String, Object>, ?> action) {
        var actionWrapper = this.applyValidation(schema, action);
        actionWrapper = this.applyInterceptors(actionWrapper);

        LambdaAction registeredAction = new LambdaAction(this.generateActionCmd(name),
                schema, actionWrapper);
        this._actions.add(registeredAction);
    }

    /**
     * Registers an action with authorization.
     *
     * @param name      an action name
     * @param schema    a validation schema to validate received parameters.
     * @param authorize an authorization interceptor
     * @param action    an action function that is called when operation is invoked.
     */
    protected void registerActionWithAuth(String name, Schema schema,
                                          AuthorizeFunction<Map<String, Object>, Function<Map<String, Object>, ?>, ?> authorize,
                                          Function<Map<String, Object>, ?> action) {
        var actionWrapper = this.applyValidation(schema, action);
        // Add authorization just before validation
        Function<Map<String, Object>, ?> finalActionWrapper = actionWrapper;
        actionWrapper = (call) -> authorize.apply(call, finalActionWrapper);
        actionWrapper = this.applyInterceptors(actionWrapper);

        LambdaAction registeredAction = new LambdaAction(
                this.generateActionCmd(name),
                schema,
                actionWrapper
        );
        this._actions.add(registeredAction);
    }

    /**
     * Registers a middleware for actions in AWS Lambda controller.
     *
     * @param action an action function that is called when middleware is invoked.
     */
    protected void registerInterceptor(AuthorizeFunction<Map<String, Object>, Function<Map<String, Object>, ?>, ?> action) {
        _interceptors.add(action);
    }

    /**
     * Registers all actions in AWS Lambda function.
     */
    public abstract void register() throws ReferenceException;

    /**
     * Calls registered action in this lambda function.
     * "cmd" parameter in the action parameters determin
     * what action shall be called.
     * <p>
     * This method shall only be used in testing.
     *
     * @param params action parameters.
     */
    public Object act(Map<String, Object> params) throws ApplicationException {
        String cmd = params.get("cmd").toString();
        String context = params.get("trace_id").toString();

        if (cmd == null) {
            throw new BadRequestException(
                    context,
                    "NO_COMMAND",
                    "Cmd parameter is missing"
            );
        }

        LambdaAction action = this._actions.stream().filter(a -> a.getCmd().equals(cmd)).findFirst().orElse(null);
        if (action == null) {
            throw new BadRequestException(
                    context,
                    "NO_ACTION",
                    "Action " + cmd + " was not found"
            )
                    .withDetails("command", cmd);
        }

        return action.getAction().apply(params);
    }
}
