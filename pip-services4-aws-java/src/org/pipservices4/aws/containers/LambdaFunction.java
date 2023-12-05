package org.pipservices4.aws.containers;

import org.pipservices4.aws.clients.LambdaClient;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.BadRequestException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.UnknownException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.DependencyResolver;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.container.containers.Container;
import org.pipservices4.data.validate.Schema;
import org.pipservices4.data.validate.ValidationException;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.ConsoleLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;
import org.pipservices4.aws.controllers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * Abstract AWS Lambda function, that acts as a container to instantiate and run components
 * and expose them via external entry point.
 * <p>
 * When handling calls "cmd" parameter determines which what action shall be called, while
 * other parameters are passed to the action itself.
 * <p>
 * Container configuration for this Lambda function is stored in <code>"./config/config.yml"</code> file.
 * But this path can be overriden by <code>CONFIG_PATH</code> environment variable.
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
 *     class MyLambdaFunction extends LambdaFunction {
 *         public MyLambdaFunction() {
 *             super("mygroup", "MyGroup lambda function");
 *         }
 *     }
 * <p>
 *     var lambda = new MyLambdaFunction();
 * <p>
 *     lambda.run();
 *     console.log("MyLambdaFunction is started");
 */
public abstract class LambdaFunction extends Container {
    protected String _configPath = "./config/config.yml";
    private final Semaphore _exitEvent = new Semaphore(0);

    /**
     * The performanc counters.
     */
    protected CompositeCounters _counters = new CompositeCounters();
    /**
     * The tracer.
     */
    protected CompositeTracer _tracer = new CompositeTracer();
    /**
     * The dependency resolver.
     */
    protected DependencyResolver _dependencyResolver = new DependencyResolver();
    /**
     * The map of registred validation schemas.
     */
    protected Map<String, Schema> _schemas = new HashMap<>();
    /**
     * The map of registered actions.
     */
    protected Map<String, Function<Map<String, Object>, ?>> _actions = new HashMap<>();

    /**
     * Creates a new instance of this lambda function.
     *
     * @param name          (optional) a container name (accessible via ContextInfo)
     * @param description   (optional) a container description (accessible via ContextInfo)
     */
    public LambdaFunction(String name, String description) {
        super(name, description);

        this._logger = new ConsoleLogger();
    }

    private String getConfigPath() {
        return System.getenv("CONFIG_PATH") != null
                && !System.getenv("CONFIG_PATH").isEmpty()
                ? System.getenv("CONFIG_PATH") : this._configPath;
    }

    private ConfigParams getParameters() {
        return ConfigParams.fromValue(System.getenv());
    }

    private void captureErrors(IContext context) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (ex instanceof Exception)
                    _logger.fatal(context, (Exception) ex, "Process is terminated");
                else
                    _logger.fatal(context, "Process is terminated");

                _exitEvent.release();
            }
        });
    }

    private void captureExit(IContext context) {
        _logger.info(null, "Press Control-C to stop the microservice...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                _logger.info(context, "Goodbye!");

                _exitEvent.release();

                // Runtime.getRuntime().exit(1);
            }
        });

        // Wait and close
        try {
            _exitEvent.acquire();
        } catch (InterruptedException ex) {
            // Ignore...
        }
    }

    /**
     * Sets references to dependent components.
     *
     * @param references 	references to locate the component dependencies.
     */
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);
        this._counters.setReferences(references);
        this._dependencyResolver.setReferences(references);

        this.register();
    }

    /**
     * Opens the component.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     */
    public void open(IContext context) throws ApplicationException {
        if (this.isOpen()) return;

        super.open(context);
        this.registerControllers();
    }

    /**
     * Adds instrumentation to log calls and measure call time.
     * It returns a InstrumentTiming object that is used to end the time measurement.
     * <p>
     * Note: This method has been deprecated. Use LambdaController instead.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param name              a method name.
     * @return {@link InstrumentTiming} object to end the time measurement.
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
     * Runs this lambda function, loads container configuration,
     * instantiate components and manage their lifecycle,
     * makes this function ready to access action calls.
     *
     */
    public void run() throws ApplicationException {
        IContext context = Context.fromTraceId(_info.getName());
        String path = getConfigPath();
        ConfigParams parameters = getParameters();
        readConfigFromFile(context, path, parameters);

        captureErrors(context);
        open(context);
        captureExit(context);
        close(context);
    }

    /**
     * Registers all actions in this lambda function.
     * <p>
     * Note: Overloading of this method has been deprecated. Use LambdaController instead.
     */
    protected void register() throws ReferenceException {
        //
    }

    /**
     * Registers all lambda controllers in the container.
     */
    protected void registerControllers() throws UnknownException {
        // Extract regular and commandable Lambda controllers from references
        var awsControllers = this._references.getOneOptional(ILambdaController.class,
                new Descriptor("*", "controller", "awslambda", "*", "*")
        );
        var cmdControllers = this._references.getOneOptional(ILambdaController.class,
                new Descriptor("*", "controller", "commandable-awslambda", "*", "*")
        );
        var controllers = new ArrayList<ILambdaController>();
        if (awsControllers != null) controllers.add(awsControllers);
        if (cmdControllers != null) controllers.add(cmdControllers);


        // Register actions defined in those controllers
        for (var controller : controllers) {
            var actions = controller.getActions();
            for (var action : actions) {
                registerAction(action.getCmd(), action.getSchema(), action.getAction());
            }
        }
    }

    /**
     * Registers an action in this lambda function.
     * <p>
     * Note: This method has been deprecated. Use LambdaController instead.
     *
     * @param cmd           a action/command name.
     * @param schema        a validation schema to validate received parameters.
     * @param action        an action function that is called when action is invoked.
     */
    protected void registerAction(String cmd, Schema schema,
                             Function<Map<String, Object>, ?> action) throws UnknownException {
        if (cmd.isEmpty()) {
            throw new UnknownException(null, "NO_COMMAND", "Missing command");
        }

        if (action == null) {
            throw new UnknownException(null, "NO_ACTION", "Missing action");
        }

        // Hack!!! Wrapping action to preserve prototyping context
        Function<Map<String, Object>, ?> actionCurl = (Map<String, Object> params) -> {
            // Perform validation
            if (schema != null) {
                String traceId = params.get("trace_id").toString();
                try {
                    schema.validateAndThrowException(traceId, params, false);
                } catch (ValidationException ex) {
                    return ex;
                }
            }

            // Todo: perform verification?
            return action.apply(params);
        };

        this._actions.put(cmd, actionCurl);
    }

    /**
     * Executes this AWS Lambda function and returns the result.
     * This method can be overloaded in child classes
     * if they need to change the default behavior
     *
     * @param params the event parameters (or function arguments)
     * @return the result of the function execution.
     */
    protected Object execute(Map<String, Object> params) throws ApplicationException {
        String cmd = params.get("cmd").toString();
        String traceId = params.get("trace_id").toString();

        if (cmd == null) {
            throw new BadRequestException(
                    traceId,
                    "NO_COMMAND",
                    "Cmd parameter is missing"
            );
        }

        var action= _actions.get(cmd);
        if (action == null) {
            throw new BadRequestException(
                    traceId,
                    "NO_ACTION",
                    "Action " + cmd + " was not found"
            )
                    .withDetails("command", cmd);
        }

        return action.apply(params);
    }

    private Object handler(Map<String, Object> params) throws ApplicationException {
        // If already started then execute
        if (this.isOpen()) {
            return this.execute(params);
        }
        // Start before execute
        run();
        return this.execute(params);
    }

    /**
     * Gets entry point into this lambda function.
     */
    public Function<Map<String, Object>, ?> getHandler() {
        return params -> {
            try {
                return handler(params);
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Calls registered action in this lambda function.
     * "cmd" parameter in the action parameters determin
     * what action shall be called.
     * <p>
     * This method shall only be used in testing.
     *
     * @param params action parameters.
     */
    public Object act(Map<String, Object> params) {
        return getHandler().apply(params);
    }
}
