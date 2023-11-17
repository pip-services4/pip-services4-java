package org.pipservices4.http.clients;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.*;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.config.connect.ConnectionParams;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.config.connect.HttpConnectionResolver;
import org.pipservices4.rpc.trace.InstrumentTiming;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;

/**
 * Abstract client that calls remove endpoints using HTTP/REST protocol.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>base_route:              base route for remote URI
 * <li>connection(s):
 *   <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>protocol:              connection protocol: http or https
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 *   </ul>
 * <li>options:
 *   <ul>
 *   <li>retries:               number of retries (default: 3)
 *   <li>connect_timeout:       connection timeout in milliseconds (default: 10 sec)
 *   <li>timeout:               invocation timeout in milliseconds (default: 10 sec)
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0           (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:tracer:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/trace/ITracer.html">ITracer</a> components to record traces
 * <li>*:discovery:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyRestClient extends RestClient implements IMyClient {
 *    ...
 *
 *    public MyData getData(IContext context, String id) {
 *        Timing timing = this.instrument(context, 'myclient.get_data');
 *        MyData result = this.execute(MyData.class, context, HttpMethod.POST, "/get_data", new MyData(id));
 *        timing.endTiming();
 *        return result;
 *    }
 *    ...
 * }
 *
 * MyRestClient client = new MyRestClient();
 * client.configure(ConfigParams.fromTuples(
 *     "connection.protocol", "http",
 *     "connection.host", "localhost",
 *     "connection.port", 8080
 * ));
 *
 * MyData data = client.getData("123", "1");
 * ...
 * }
 * </pre>
 */
public class RestClient implements IOpenable, IConfigurable, IReferenceable {

    private final static ConfigParams _defaultConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "0.0.0.0",
            "connection.port", 3000,

            "options.request_max_size", 1024 * 1024,
            "options.connect_timeout", 10000,
            "options.timeout", 10000,
            "options.retries", 3,
            "options.debug", true
    );

    /**
     * The connection resolver.
     */
    protected HttpConnectionResolver _connectionResolver = new HttpConnectionResolver();
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

    /**
     * The configuration options.
     */
    protected ConfigParams _options = new ConfigParams();
    /**
     * The base route.
     */
    protected String _baseRoute;
    /**
     * The number of retries.
     */
    protected int _retries = 1;
    /**
     * The connection timeout in milliseconds.
     */
    protected long _connectTimeout = 10000;
    /**
     * The invocation timeout in milliseconds.
     */
    protected long _timeout = 10000;
    /**
     * The default headers to be added to every request.
     */
    protected MultivaluedMap<String, Object> _headers = new MultivaluedHashMap<>();
    protected String _contextLocation = "query";

    /**
     * The remote service uri which is calculated on open.
     */
    protected String _url;
    /**
     * The HTTP client.
     */
    protected Client _client;

    /**
     * Creates a new instance of the client.
     */
    protected RestClient() {
        this(null);
    }

    /**
     * Creates a new instance of the client.
     *
     * @param baseRoute a base route for remote service.
     */
    protected RestClient(String baseRoute) {
        _baseRoute = baseRoute;
    }

    /**
     * Client retry strategy
     */
    private RetryPolicy<Object> _retryPolicy;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        config = config.setDefaults(RestClient._defaultConfig);
        this._connectionResolver.configure(config);
        this._options = this._options.override(config.getSection("options"));

        this._retries = config.getAsIntegerWithDefault("options.retries", this._retries);
        this._connectTimeout = config.getAsLongWithDefault("options.connect_timeout", this._connectTimeout);
        this._timeout = config.getAsLongWithDefault("options.timeout", this._timeout);

        this._baseRoute = config.getAsStringWithDefault("base_route", this._baseRoute);
        this._contextLocation = config.getAsStringWithDefault("options.trace_id_place", this._contextLocation);
        this._contextLocation = config.getAsStringWithDefault("options.trace_id", this._contextLocation);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException {
        _logger.setReferences(references);
        _counters.setReferences(references);
        _tracer.setReferences(references);
        _connectionResolver.setReferences(references);
    }

    /**
     * Adds instrumentation to log calls and measure call time. It returns a Timing
     * object that is used to end the time measurement.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param name          a method name.
     * @return Timing object to end the time measurement.
     */
    protected InstrumentTiming instrument(IContext context, String name) {
        this._logger.trace(context, "Calling %s method", name);
        this._counters.incrementOne(name + ".call_count");

        var counterTiming = this._counters.beginTiming(name + ".call_time");
        var traceTiming = this._tracer.beginTrace(context, name, null);
        return new InstrumentTiming(context, name, "call",
                this._logger, this._counters, counterTiming, traceTiming);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _client != null;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        // Skip if already opened
        if (_client != null)
            return;

        ConnectionParams connection = _connectionResolver.resolve(context);

        String protocol = connection.getProtocolWithDefault("http");
        String host = connection.getHost();
        int port = connection.getPort();
        _url = protocol + "://" + host + ":" + port;

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, _connectTimeout);
        clientConfig.property(ClientProperties.READ_TIMEOUT, _timeout);

        _retryPolicy = new RetryPolicy<>()
                .withDelay(_timeout, _timeout * 10, ChronoUnit.MILLIS)
                .withMaxRetries(_retries);

        clientConfig.register(new JacksonFeature());

        _client = ClientBuilder.newClient(clientConfig);

        _logger.debug(context, "Connected via REST to %s", _url);
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void close(IContext context) {
        if (_client == null)
            return;

        _client.close();
        _client = null;

        _logger.debug(context, "Disconnected from %s", _url);

        _url = null;
    }

    private URI createRequestUri(String route) {
        StringBuilder builder = new StringBuilder(_url);

        if (_baseRoute != null && !_baseRoute.trim().isEmpty()) {
            if (_baseRoute.charAt(0) != '/')
                builder.append('/');
            builder.append(_baseRoute);
        }

        if (route.charAt(0) != '/')
            builder.append('/');
        builder.append(route);

        String uri = builder.toString();

        return UriBuilder.fromUri(uri).build();
    }

    private String addQueryParameter(String query, String name, String value) {
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        value = value != null ? URLEncoder.encode(value, StandardCharsets.UTF_8) : "";

        int pos = query.indexOf('?');
        String path = pos >= 0 ? query.substring(0, pos) : query;
        String parameters = pos >= 0 ? query.substring(pos) : "";
        return path + "?" + (parameters.isEmpty() ? "" : "&") + name + "=" + value;
    }

    /**
     * Adds a trace id (trace_id) to invocation parameter map.
     *
     * @param route         invocation parameters.
     * @param context     (optional) a context to trace execution through call chain.
     * @return invocation parameters with added trace id.
     */
    protected String addTraceId(String route, IContext context) {
        if (context == null) {
            return route;
        }
        return addQueryParameter(route, "trace_id", ContextResolver.getTraceId(context));
    }

    /**
     * Adds filter parameters (with the same name as they defined) to invocation
     * parameter map.
     *
     * @param route  invocation parameters.
     * @param filter (optional) filter parameters
     * @return invocation parameters with added filter parameters.
     */
    protected String addFilterParams(String route, FilterParams filter) {
        for (String key : filter.keySet()) {
            route = addQueryParameter(route, key, filter.get(key));
        }
        return route;
    }

    /**
     * Adds paging parameters (skip, take, total) to invocation parameter map.
     *
     * @param route  invocation parameters.
     * @param paging (optional) paging parameters
     * @return invocation parameters with added paging parameters.
     */
    protected String addPagingParams(String route, PagingParams paging) {
        if (paging.getSkip() != null)
            route = addQueryParameter(route, "skip", paging.getSkip().toString());
        if (paging.getTake() != null)
            route = addQueryParameter(route, "take", paging.getTake().toString());
        if (paging.hasTotal())
            route = addQueryParameter(route, "total", paging.getTake().toString());
        return route;
    }

    protected Response executeRequest(IContext context, String method, URI uri, String mediaType, Entity<?> body)
            throws ApplicationException {

        if (_client == null) {
            throw new InvalidStateException(context != null ? ContextResolver.getTraceId(context) : null, "NOT_OPENED", "Client is not opened");
        }

        Response response;
        response = Failsafe.with(_retryPolicy).get(
                () -> _client.target(uri).request(mediaType).headers(_headers).method(method, body));

        if (response == null) {
            throw new UnknownException(context != null ? ContextResolver.getTraceId(context) : null, "NO_RESPONSE",
                    "Unable to get a result from " + method + " " + uri);
        }

        if (response.getStatus() >= 400) {
            ErrorDescription errorObject;
            try {
                errorObject = response.readEntity(ErrorDescription.class);
            } catch (Exception ex) {
                // Todo: This may not work as expected. Find another way to get content string
//                String res = response.readEntity(String.class);
                Object res = response.readEntity(Object.class);
                String responseContent = res.toString();
                throw new UnknownException(context != null ? ContextResolver.getTraceId(context) : null, "UNKNOWN_ERROR", responseContent);
            }

            if (errorObject != null)
                throw ApplicationExceptionFactory.create(errorObject);
        }

        return response;
    }

    private Response executeJsonRequest(IContext context, String method, String route, Object requestEntity)
            throws ApplicationException {

        route = addTraceId(route, context);
        URI uri = createRequestUri(route);

        Entity<?> body = Entity.entity(requestEntity, MediaType.APPLICATION_JSON);
        return executeRequest(context, method, uri, MediaType.APPLICATION_JSON, body);
    }

    /**
     * Executes a remote method via HTTP/REST protocol.
     *
     * @param type          the class type of data.
     * @param context     (optional) a context to trace execution through call chain.
     * @param method        HTTP method: "get", "head", "post", "put", "delete"
     * @param route         a command route. Base route will be added to this route
     * @param requestEntity request body object.
     * @return result object.
     * @throws ApplicationException when error occured.
     */
    protected <T> T call(Class<T> type, IContext context, String method, String route, Object requestEntity)
            throws ApplicationException {

        try (Response response = executeJsonRequest(context, method, route, requestEntity)) {
            return response.readEntity(type);
        }
//        catch (Throwable ex) {
//            throw new InvocationException(context, "SERIALIZATION_FAILED", "Failed to deserialize HTTP response")
//                    .withCause(ex);
//        }
    }

    /**
     * Executes a remote method via HTTP/REST protocol.
     *
     * @param type          the generic class type of data.
     * @param context     (optional) a context to trace execution through call chain.
     * @param method        HTTP method: "get", "head", "post", "put", "delete"
     * @param route         a command route. Base route will be added to this route
     * @param requestEntity request body object.
     * @return result object.
     * @throws ApplicationException when error occured.
     */
    protected <T> T call(GenericType<T> type, IContext context, String method, String route,
                         Object requestEntity) throws ApplicationException {

        try (Response response = executeJsonRequest(context, method, route, requestEntity)) {
            return response.readEntity(type);
        }
//        } catch (Throwable ex) {
//            throw new InvocationException(context, "SERIALIZATION_FAILED", "Failed to deserialize HTTP response")
//                    .withCause(ex);
//        }
    }

}
