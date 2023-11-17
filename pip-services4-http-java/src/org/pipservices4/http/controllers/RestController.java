package org.pipservices4.http.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.commons.errors.InvocationException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.data.validate.Schema;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;
import org.pipservices4.components.context.IContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Abstract service that receives remove calls via HTTP/REST protocol.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>base_route: base route for remote URI
 * <li>dependencies:
 * <ul>
 * <li>endpoint: override for HTTP Endpoint dependency
 * <li>controller: override for Controller dependency
 * </ul>
 * <li>connection(s):
 * <ul>
 * <li>discovery_key: (optional) a key to retrieve the connection from <a href=
 * "https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 * <li>protocol: connection protocol: http or https
 * <li>host: host name or IP address
 * <li>port: port number
 * <li>uri: resource URI or connection string with all parameters in it
 * </ul>
 * <li>credential - the HTTPS credentials:
 * <ul>
 * <li>"credential.ssl_key_file" - the SSL private key in PEM
 * <li>"credential.ssl_crt_file" - the SSL certificate in PEM
 * <li>"credential.ssl_ca_file" - the certificate authorities (root
 * cerfiticates) in PEM
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
 * <li>*:tracer:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/trace/ITracer.html">ITracer</a>
 * components to record traces
 * <li>*:discovery:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 * services to resolve connection
 * <li>*:endpoint:http:*:1.0 (optional) {@link HttpEndpoint} reference
 * </ul>
 * <p>
 * ### Example ###
 * 
 * <pre>
 * {@code
 * class MyRestController extends RestController {
 *    private IMyService _service;
 *    ...
 *    public MyRestController() {
 *       super();
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
 *    this.registerRoute(
 *                 HttpMethod.GET, "/get_mydata",
 *                 null,
 *                 this::getData
 *         );
 *        ...
 *    }
 *
 *    private Response getData(ContainerRequestContext req) {
 *         ...
 *        }
 * }
 *
 * MyRestController controller = new MyRestController();
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
 */
public abstract class RestController
        implements IOpenable, IConfigurable, IReferenceable, IUnreferenceable, IRegisterable {

    private static final ConfigParams _defaultConfig = ConfigParams.fromTuples(
            "base_route", "",
            "dependencies.endpoint", "*:endpoint:http:*:1.0",
            "dependencies.swagger", "*:swagger-service:*:*:1.0");

    protected ConfigParams _config;
    private IReferences _references;
    private boolean _localEndpoint;
    private boolean _opened;

    /**
     * The base route.
     */
    protected String _baseRoute;
    /**
     * The HTTP endpoint that exposes this service.
     */
    protected HttpEndpoint _endpoint;
    /**
     * The dependency resolver.
     */
    protected DependencyResolver _dependencyResolver = new DependencyResolver(_defaultConfig);
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

    protected String _url;

    protected ISwaggerController _swaggerController;
    protected boolean _swaggerEnable = false;
    protected String _swaggerRoute = "swagger";

    protected RestController() {
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException when configuration is wrong.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        _config = config.setDefaults(_defaultConfig);

        _dependencyResolver.configure(config);

        _baseRoute = config.getAsStringWithDefault("base_route", _baseRoute);

        this._swaggerEnable = config.getAsBooleanWithDefault("swagger.enable", this._swaggerEnable);
        this._swaggerRoute = config.getAsStringWithDefault("swagger.route", this._swaggerRoute);
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

        _references = references;

        // Get endpoint
        _endpoint = (HttpEndpoint) _dependencyResolver.getOneOptional("endpoint");

        // Or create a local one
        if (_endpoint == null) {
            _endpoint = createLocalEndpoint();
            _localEndpoint = true;
        } else {
            _localEndpoint = false;
        }

        // Add registration callback to the endpoint
        _endpoint.register(this);

        this._swaggerController = this._dependencyResolver.getOneOptional(ISwaggerController.class, "swagger");
    }

    /**
     * Unsets (clears) previously set references to dependent components.
     */
    public void unsetReferences() {
        // Remove registration callback from endpoint
        if (_endpoint != null) {
            _endpoint.unregister(this);
            _endpoint = null;
        }
        this._swaggerController = null;
    }

    private HttpEndpoint createLocalEndpoint() throws ConfigException, ReferenceException {
        HttpEndpoint endpoint = new HttpEndpoint();

        if (_config != null)
            endpoint.configure(_config);

        if (_references != null)
            endpoint.setReferences(_references);

        return endpoint;
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

        if (_endpoint == null) {
            _endpoint = createLocalEndpoint();
            _endpoint.register(this);
            _localEndpoint = true;
        }

        if (_localEndpoint)
            _endpoint.open(context);

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

        if (_endpoint == null) {
            throw new InvalidStateException(context != null ? ContextResolver.getTraceId(context) : null, "NO_ENDPOINT", "HTTP endpoint is missing");
        }

        if (_localEndpoint) {
            _endpoint.close(context);
        }

        _opened = false;
    }

    /**
     * Sends error serialized as ErrorDescription object and appropriate HTTP status
     * code. If status code is not defined, it uses 500 status code.
     *
     * @param ex an error object to be sent.
     * @return HTTP response status
     */
    protected Response sendError(Exception ex) {
        return HttpResponseSender.sendError(ex);
    }

    /**
     * Creates a callback function that sends result as JSON object. That callack
     * function call be called directly or passed as a parameter to business logic
     * components.
     * <p>
     * If object is not null it returns 200 status code. For null results it returns
     * 204 status code. If error occur it sends ErrorDescription with approproate
     * status code.
     *
     * @param result a body object to result.
     * @return execution result.
     */
    protected Response sendResult(Object result) {
        return HttpResponseSender.sendResult(result);
    }

    /**
     * Creates a callback function that sends an empty result with 204 status code.
     * If error occur it sends ErrorDescription with approproate status code.
     *
     * @return HTTP response status with no content.
     */
    protected Response sendEmptyResult() {
        return HttpResponseSender.sendEmptyResult();
    }

    /**
     * Creates a callback function that sends newly created object as JSON. That
     * callack function call be called directly or passed as a parameter to business
     * logic components.
     * <p>
     * If object is not null it returns 201 status code. For null results it returns
     * 204 status code. If error occur it sends ErrorDescription with approproate
     * status code.
     *
     * @param result a body object to created result
     * @return execution result.
     */
    protected Response sendCreatedResult(Object result) {
        return HttpResponseSender.sendCreatedResult(result);
    }

    /**
     * Creates a callback function that sends deleted object as JSON. That callack
     * function call be called directly or passed as a parameter to business logic
     * components.
     * <p>
     * If object is not null it returns 200 status code. For null results it returns
     * 204 status code. If error occur it sends ErrorDescription with approproate
     * status code.
     *
     * @param result a body object to deleted result
     * @return execution result.
     */
    protected Response sendDeletedResult(Object result) {
        return HttpResponseSender.sendDeletedResult(result);
    }

    protected String getQueryParameter(ContainerRequestContext request, String name) {
        String value = null;
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        if (request.getUriInfo().getQueryParameters().containsKey(name)) {
            value = request.getUriInfo().getQueryParameters().getFirst(name);
            value = value != null ? URLDecoder.decode(value, StandardCharsets.UTF_8) : null;
        }

        return value;
    }

    /**
     * Gets string value of request body.
     *
     * @param request HTTP request
     * @return string value of data.
     * @throws ApplicationException when error occured.
     */
    protected String getBodyAsString(ContainerRequestContext request) throws ApplicationException {
        try {
            InputStream streamReader = request.getEntityStream();
            byte[] data = new byte[streamReader.available()];
            streamReader.read(data, 0, data.length);
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new InvocationException(null, "READ_ERROR", "Cannot read input stream").wrap(ex);
        }
    }

    /**
     * Gets request body from json string.
     *
     * @param type    the class type of result object.
     * @param request HTTP request
     * @return converted object value
     * @throws ApplicationException when error occured.
     */
    protected <T> T getBodyAsJson(Class<T> type, ContainerRequestContext request) throws ApplicationException {
        if (!request.getMediaType().toString().contains(MediaType.APPLICATION_JSON)) {
            throw new InvocationException(null, "EXPECTED_JSON", "Expected application/json media type");
        }

        String json = getBodyAsString(request);

        try {
            return JsonConverter.fromJson(type, json);
        } catch (IOException ex) {
            throw new InvocationException(null, "READ_ERROR", "Failed to deserialize request from JSON").wrap(ex);
        }
    }

    private String appendBaseRoute(String route) {
        route = route == null ? "/" : route;

        if (this._baseRoute != null && !this._baseRoute.isEmpty()) {
            var baseRoute = this._baseRoute;
            if (route.isEmpty())
                route = "/";
            if (route.charAt(0) != '/')
                route = "/" + route;
            if (baseRoute.charAt(0) != '/')
                baseRoute = '/' + baseRoute;
            route = baseRoute + route;
        }

        return route;
    }

    /**
     * Registers a route in HTTP endpoint.
     *
     * @param method HTTP method: "get", "head", "post", "put", "delete"
     * @param route  a command route. Base route will be added to this route
     * @param action an action function that is called when operation is invoked.
     */
    protected void registerRoute(String method, String route, Inflector<ContainerRequestContext, Response> action) {
        if (_endpoint == null)
            return;

        route = appendBaseRoute(route);

        _endpoint.registerRoute(method, route, action);
    }

    /**
     * Registers a route in HTTP endpoint.
     *
     * @param method HTTP method: "get", "head", "post", "put", "delete"
     * @param route  a command route. Base route will be added to this route
     * @param schema a validation schema to validate received parameters.
     * @param action an action function that is called when operation is invoked.
     */
    protected void registerRoute(String method, String route, Schema schema,
            Inflector<ContainerRequestContext, Response> action) {
        if (_endpoint == null)
            return;

        route = appendBaseRoute(route);

        _endpoint.registerRoute(method.toUpperCase(), route, schema, action);
    }

    /**
     * Registers a route with authorization in HTTP endpoint.
     *
     * @param method    HTTP method: "get", "head", "post", "put", "delete"
     * @param route     a command route. Base route will be added to this route
     * @param schema    a validation schema to validate received parameters.
     * @param authorize an authorization interceptor
     * @param action    an action function that is called when operation is invoked.
     */
    protected void registerRouteWithAuth(String method, String route, Schema schema,
                                         AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> authorize,
                                         Inflector<ContainerRequestContext, Response> action) {
        if (this._endpoint == null)
            return;
        route = this.appendBaseRoute(route);
        this._endpoint.registerRouteWithAuth(method.toUpperCase(), route, schema, authorize, action);
    }

    /**
     * Registers a middleware for a given route in HTTP endpoint.
     *
     * @param route  a command route. Base route will be added to this route
     * @param action an action function that is called when middleware is invoked.
     */
    protected void registerInterceptor(String route, Function<ContainerRequestContext, ?> action) {
        if (this._endpoint == null)
            return;

        route = this.appendBaseRoute(route);

        this._endpoint.registerInterceptor(route, action);
    }

    protected void registerOpenApiSpecFromFile(String path) {
        try (var fs = new FileInputStream(path)) {
            var content = new String(fs.readAllBytes(), StandardCharsets.UTF_8);
            this.registerOpenApiSpec(content);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void registerOpenApiSpec(String content) {
        if (!this._swaggerEnable)
            return;

        this.registerRoute(HttpMethod.GET, this._swaggerRoute, null,
                new Inflector<ContainerRequestContext, Response>() {
                    @Override
                    public Response apply(ContainerRequestContext req) {
                        return Response.status(200)
                                .entity(content)
                                .header("Content-Length", content.length())
                                .header("Content-Type", "application/x-yaml")
                                .type(MediaType.APPLICATION_XML_TYPE)
                                .build();
                    }
                });

        if (this._swaggerController != null)
            this._swaggerController.registerOpenApiSpec(this._baseRoute, this._swaggerRoute);
    }

    /**
     * Returns traceId from request
     *
     * @param req -  http request
     * @return Returns traceId from request
     */
    protected String getTraceId(ContainerRequestContext req) {
        var traceId = getQueryParameter(req, "trace_id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = req.getHeaderString("trace_id") != null && !req.getHeaderString("trace_id").isEmpty() ?
                    req.getHeaderString("trace_id") :
                    req.getHeaderString("correlation_id");
        }
        return traceId;
    }

    /**
     * Registers all service routes in HTTP endpoint.
     * <p>
     * This method is called by the service and must be overriden
     * in child classes.
     */
    @Override
    public abstract void register() throws ReferenceException;
}
