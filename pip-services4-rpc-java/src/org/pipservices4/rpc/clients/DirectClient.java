package org.pipservices4.rpc.clients;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.ConnectionException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.DependencyResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;

/**
 * Abstract client that calls service directly in the same memory space.
 * <p>
 * It is used when multiple microservices are deployed in a single container (monolyth)
 * and communication between them can be done by direct calls rather then through
 * the network.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>dependencies:
 *   <ul>
 *   <li>service:            override service descriptor
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0       (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:tracer:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/trace/ITracer.html">ITracer</a> components to record traces
 * <li>*:service:*:*:1.0     service to call business methods
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyDirectClient extends DirectClient<IMyService> implements IMyClient {
 *
 *   public MyDirectClient() {
 *       super();
 *       this._dependencyResolver.put('service', new Descriptor(
 *           "mygroup", "service", "*", "*", "*"));
 *    }
 *    ...
 *
 *    public MyData getData(IContext context, String id) {
 *        Timing timing = this.instrument(context, 'myclient.get_data');
 *        MyData result = this._service.getData(context, id);
 *        timing.endTiming();
 *        return result;
 *    }
 *    ...
 * }
 *
 * MyDirectClient client = new MyDirectClient();
 * client.setReferences(References.fromTuples(
 *     new Descriptor("mygroup","service","default","default","1.0"), service
 * ));
 *
 * MyData data = client.getData("123", "1");
 * ...
 * }
 * </pre>
 */
public abstract class DirectClient<T> implements IConfigurable, IOpenable, IReferenceable {
    /**
     * The service reference.
     */
    protected T _service;
    /**
     * The logger.
     */
    protected CompositeLogger _logger = new CompositeLogger();
    /**
     * The performance counters
     */
    protected CompositeCounters _counters = new CompositeCounters();
    /**
     * The dependency resolver to get service reference.
     */
    protected DependencyResolver _dependencyResolver = new DependencyResolver();
    /**
     * The open flag.
     */
    protected boolean _opened = false;
    /**
     * The tracer.
     */
    protected CompositeTracer _tracer = new CompositeTracer();

    /**
     * Creates a new instance of the client.
     */
    public DirectClient() {
        _dependencyResolver.put("service", "none");
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException when configuration is wrong.
     */
    public void configure(ConfigParams config) throws ConfigException {
        _dependencyResolver.configure(config);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     */
    @SuppressWarnings("unchecked")
    public void setReferences(IReferences references) throws ReferenceException {
        _logger.setReferences(references);
        _counters.setReferences(references);
        _tracer.setReferences(references);
        _dependencyResolver.setReferences(references);
        _service = (T) this._dependencyResolver.getOneRequired("service");
    }

    /**
     * Adds instrumentation to log calls and measure call time. It returns a Timing
     * object that is used to end the time measurement.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param name    a method name.
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
    public boolean isOpen() {
        return _opened;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ConnectionException when service reference is missing.
     */
    public void open(IContext context) throws ConnectionException {
        if (isOpen())
            return;

        if (_service == null) {
            throw new ConnectionException(context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_SERVICE", "Service reference is missing");
        }

        _logger.info(context, "Opened Direct client {0}", this.getClass().getName());

        _opened = true;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void close(IContext context) {
        if (isOpen()) {
            _logger.debug(context, "Closed Direct client {0}", this.getClass().getName());
        }

        _opened = false;
    }
}
