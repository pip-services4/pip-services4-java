package org.pipservices4.prometheus.count;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.count.CachedCounters;
import org.pipservices4.observability.count.Counter;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.config.connect.HttpConnectionResolver;
import org.pipservices4.prometheus.controllers.PrometheusMetricsController;

import java.net.InetAddress;
import java.util.List;

/**
 * Performance counters that send their metrics to Prometheus controller.
 * <p>
 * The component is normally used in passive mode conjunction with {@link PrometheusMetricsController}.
 * Alternatively when connection parameters are set it can push metrics to Prometheus PushGateway.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * <ul>
 * <li>connection(s):
 * <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>protocol:              connection protocol: http or https
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 * </ul>
 * <li>options:
 * <ul>
 *   <li>retries:               number of retries (default: 3)
 *   <li>connect_timeout:       connection timeout in milliseconds (default: 10 sec)
 *   <li>timeout:               invocation timeout in milliseconds (default: 10 sec)
 * </ul>
 * </ul>
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
 * <li>*:discovery:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 * services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <p>
 *     var counters = new PrometheusCounters();
 *     counters.configure(ConfigParams.fromTuples(
 *         "connection.protocol", "http",
 *         "connection.host", "localhost",
 *         "connection.port", 8080
 *     ));
 * <p>
 *     counters.open("123");
 * <p>
 *     counters.increment("mycomponent.mymethod.calls");
 *     var timing = counters.beginTiming("mycomponent.mymethod.exec_time");
 *     try {
 *         ...
 *     } finally {
 *         timing.endTiming();
 *     }
 * <p>
 *     counters.dump();
 */
public class PrometheusCounters extends CachedCounters implements IReferenceable, IOpenable, IConfigurable {
    private String _baseRoute;
    private final CompositeLogger _logger = new CompositeLogger();
    private final HttpConnectionResolver _connectionResolver = new HttpConnectionResolver();
    private boolean _opened = false;
    private String _source;
    private String _instance;
    private boolean _pushEnabled;
    private Client _client;
    private String _requestRoute;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        super.configure(config);

        this._connectionResolver.configure(config);
        this._source = config.getAsStringWithDefault("source", this._source);
        this._instance = config.getAsStringWithDefault("instance", this._instance);
        this._pushEnabled = config.getAsBooleanWithDefault("push_enabled", true);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        this._logger.setReferences(references);
        this._connectionResolver.setReferences(references);

        var contextInfo = references.getOneOptional(ContextInfo.class,
                new Descriptor("pip-services", "context-info", "default", "*", "1.0"));

        if (contextInfo != null && this._source == null)
            this._source = contextInfo.getName();

        if (contextInfo != null && this._instance == null)
            this._instance = contextInfo.getContextId();
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return this._opened;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) {
        if (this._opened)
            return;

        if (!this._pushEnabled)
            return;

        this._opened = true;

        try {
            var connection = this._connectionResolver.resolve(context);
            _baseRoute = connection.getAsString("uri");

            var job = this._source != null ? _source : "unknown";
            var instance = _instance != null ? _instance : InetAddress.getLocalHost().getHostName();
            this._requestRoute = "/metrics/job/" + job + "/instance/" + instance;

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(new JacksonFeature());

            _client = ClientBuilder.newClient(clientConfig);
        } catch (Exception ex) {
            this._client = null;
            this._logger.warn(context, "Connection to Prometheus server is not configured: " + ex);
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) {
        if (_client != null)
            _client.close();

        this._opened = false;
        this._client = null;
        this._requestRoute = null;
    }

    /**
     * Saves the current counters measurements.
     *
     * @param counters current counters measurements to be saves.
     */
    @Override
    protected void save(List<Counter> counters) {
        if (this._client == null || !this._pushEnabled) return;

        String body = PrometheusCounterConverter.toString(counters, null, null);

        try (Response response = _client.target(_baseRoute + _requestRoute).request(MediaType.APPLICATION_JSON).put(Entity.entity(body, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() >= 400)
                this._logger.error(Context.fromTraceId("prometheus-counters"), response.readEntity(String.class), "Failed to push metrics to prometheus");
        }
    }
}
