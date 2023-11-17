package org.pipservices4.http.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
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
 * Abstract service that receives remove calls via HTTP/REST protocol
 * to operations automatically generated for commands defined in <a href="https://pip-services4-java.github.io/pip-services4-rpc-java/org/pipservices4/rpc/commands/ICommandable.html">ICommandable</a> components.
 * Each command is exposed as POST operation that receives all parameters in body object.
 * <p>
 * Commandable services require only 3 lines of code to implement a robust external
 * HTTP-based remote interface.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>base_route:              base route for remote URI
 * <li>dependencies:
 *   <ul>
 *   <li>endpoint:              override for HTTP Endpoint dependency
 *   <li>controller:            override for Controller dependency
 *   </ul>
 * <li>connection(s):
 *   <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>protocol:              connection protocol: http or https
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0           (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:discovery:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * <li>*:endpoint:http:*:1.0          (optional) {@link HttpEndpoint} reference
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyCommandableHttpController extends CommandableHttpController {
 *    public MyCommandableHttpController() {
 *       super();
 *       this._dependencyResolver.put(
 *           "service",
 *           new Descriptor("mygroup","service","*","*","1.0")
 *       );
 *    }
 * }
 *
 * MyCommandableHttpController controller = new MyCommandableHttpController();
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
 * @see RestController
 */
public class CommandableHttpController extends RestController {
    protected CommandSet _commandSet;
    protected boolean _swaggerAuto = true;

    /**
     * Creates a new instance of the service.
     *
     * @param baseRoute a service base route.
     */
    public CommandableHttpController(String baseRoute) {
        this._baseRoute = baseRoute;
        _dependencyResolver.put("service", "none");
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) throws ConfigException {
        super.configure(config);

        this._swaggerAuto = config.getAsBooleanWithDefault("swagger.auto", this._swaggerAuto);
    }

    /**
     * Registers all service routes in HTTP endpoint.
     */
    @Override
    public void register() throws ReferenceException {
        var service = (ICommandable)_dependencyResolver.getOneRequired("service");

        _commandSet = service.getCommandSet();
        var commands = _commandSet.getCommands();

        for (ICommand command : commands) {
            registerRoute(HttpMethod.POST, command.getName(), new Inflector<ContainerRequestContext, Response>() {
                @Override
                public Response apply(ContainerRequestContext request) {
                    return executeCommand(command, request);
                }
            });
        }

        if (this._swaggerAuto) {
            var swaggerConfig = this._config.getSection("swagger");
            var doc = new CommandableSwaggerDocument(this._baseRoute, swaggerConfig, commands);
            this.registerOpenApiSpec(doc.toString());
        }
    }

    private Response executeCommand(ICommand command, ContainerRequestContext request) {
        var traceId = this.getTraceId(request);
        var context = traceId != null ? Context.fromTraceId(traceId) : null;
        InstrumentTiming timing = instrument(context, _baseRoute + '.' + command.getName());

        try {
            String json = getBodyAsString(request);

            Parameters parameters = json == null ? new Parameters() : Parameters.fromJson(json);

            Object result = command.execute(context, parameters);
            timing.endTiming();
            return sendResult(result);
        } catch (Exception ex) {
            timing.endFailure(ex);
            return sendError(ex);
        }
    }

}
