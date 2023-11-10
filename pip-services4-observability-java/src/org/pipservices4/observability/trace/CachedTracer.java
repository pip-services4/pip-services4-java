package org.pipservices4.observability.trace;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.commons.errors.ErrorDescriptionFactory;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.context.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract tracer that caches recorded traces in memory and periodically dumps them.
 * Child classes implement saving cached traces to their specified destinations.
 * <p>
 * ### Configuration parameters ###
 *
 * <ul>
 * <li> - source:            source (context) name
 * <li> - options:
 * <ul>
 * <li> - interval:        interval in milliseconds to save log messages (default: 10 seconds)
 * <li> - max_cache_size:  maximum number of messages stored in this cache (default: 100)
 * </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:context-info:*:*:1.0         (optional) {@link ContextInfo} to detect the context id and specify counters source
 * </ul>
 * <p>
 *
 * @see ITracer
 * @see OperationTrace
 */
public abstract class CachedTracer implements ITracer, IReconfigurable, IReferenceable {
    protected String _source = null;
    protected List<OperationTrace> _cache = new ArrayList<>();
    protected boolean _updated = false;
    protected long _lastDumpTime = System.currentTimeMillis();
    protected int _maxCacheSize = 100;
    protected long _interval = 10000;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._interval = config.getAsLongWithDefault("options.interval", this._interval);
        this._maxCacheSize = config.getAsIntegerWithDefault("options.max_cache_size", this._maxCacheSize);
        this._source = config.getAsStringWithDefault("source", this._source);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException
     * @throws ConfigException
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        ContextInfo contextInfo = (ContextInfo) references.getOneOptional(
                new Descriptor("pip-services", "context-info", "*", "*", "1.0"));
        if (contextInfo != null && this._source == null) {
            this._source = contextInfo.getName();
        }
    }

    /**
     * Writes a log message to the logger destination.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param error         an error object associated with this trace.
     * @param duration      execution duration in milliseconds.
     */
    public void write(IContext context, String component, String operation, Exception error, long duration) {
        ErrorDescription errorDesc = error != null ? ErrorDescriptionFactory.create(error) : null;
        // Account for cases when component and operation are combined in component.
        if (operation == null || operation.isEmpty()) {
            if (component != null && !component.isEmpty()) {
                int pos = component.lastIndexOf(".");
                if (pos > 0) {
                    operation = component.substring(pos + 1);
                    component = component.substring(0, pos);
                }
            }
        }
        OperationTrace trace = new OperationTrace(
                ZonedDateTime.now(ZoneId.of("UTC")),
                _source,
                component,
                operation,
                context != null ? ContextResolver.getTraceId(context) : null,
                duration,
                errorDesc
        );

        _cache.add(trace);

        update();
    }

    /**
     * Records an operation trace with its name and duration
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param duration      execution duration in milliseconds.
     */
    @Override
    public void trace(IContext context, String component, String operation, Long duration) {
        this.write(context, component, operation, null, duration);
    }

    /**
     * Records an operation failure with its name, duration and error
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param error         an error object associated with this trace.
     * @param duration      execution duration in milliseconds.
     */
    @Override
    public void failure(IContext context, String component, String operation, Exception error, long duration) {
        this.write(context, component, operation, error, duration);
    }

    /**
     * Begings recording an operation trace
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @return a trace timing object.
     */
    @Override
    public TraceTiming beginTrace(IContext context, String component, String operation) {
        return new TraceTiming(context, component, operation, this);
    }

    /**
     * Saves log messages from the cache.
     *
     * @param messages a list with log messages
     */
    public abstract void save(List<OperationTrace> messages);

    /**
     * Clears (removes) all cached log messages.
     */
    public void clear() {
        _cache = new ArrayList<>();
        _updated = false;
    }

    /**
     * Dumps (writes) the currently cached log messages.
     *
     * @see #write
     */
    public void dump() {
        if (_updated) {

            var traces = _cache;
            _cache = new ArrayList<>();

            try {
                save(traces);
            } catch (Exception e) {
                // Add traces back to the cache
                traces.addAll(this._cache);
                this._cache = traces;

                // Truncate cache
                int deleteCount = this._cache.size() - this._maxCacheSize;
                if (deleteCount > 0)
                    this._cache = this._cache.subList(deleteCount, _cache.size());
            }
        }
    }

    /**
     * Makes trace cache as updated
     * and dumps it when timeout expires.
     *
     * @see #dump
     */
    protected void update() {
        _updated = true;
        if (System.currentTimeMillis() > _lastDumpTime + _interval) {
            try {
                dump();
            } catch (Exception ignore) {
                // Todo: decide what to do
            }
        }
    }

}
