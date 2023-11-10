package org.pipservices4.observability.log;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.commons.errors.ErrorDescriptionFactory;
import org.pipservices4.commons.errors.InvocationException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract logger that caches captured log messages in memory and periodically dumps them.
 * Child classes implement saving cached messages to their specified destinations.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>level:             maximum log level to capture
 * <li>source:            source (context) name
 * <li>options:
 *   <ul>
 *   <li>interval:        interval in milliseconds to save log messages (default: 10 seconds)
 *   <li>max_cache_size:  maximum number of messages stored in this cache (default: 100)
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:context-info:*:*:1.0     (optional) {@link org.pipservices4.components.context.ContextInfo} to detect the context id and specify counters source
 * </ul>
 *
 * @see ILogger
 * @see Logger
 * @see LogMessage
 */
public abstract class CachedLogger extends Logger implements IReconfigurable {

    protected List<LogMessage> _cache = new ArrayList<>();
    protected boolean _updated = false;
    protected long _lastDumpTime = System.currentTimeMillis();
    protected long _interval = 10000;
    protected int _maxCacheSize = 100;
    protected final Object _lock = new Object();

    /**
     * Writes a log message to the logger destination.
     *
     * @param level         a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param ex            an error object associated with this message.
     * @param message       a human-readable message to log.
     */
    @Override
    protected void write(LogLevel level, IContext context, Exception ex, String message) {
        ErrorDescription error = ex != null ? ErrorDescriptionFactory.create(ex) : null;
        String source = getComputerName(); // Todo: add jar/exe name
        LogMessage logMessage = new LogMessage(level, source, context != null ? ContextResolver.getTraceId(context) : null, error, message);

        synchronized (_lock) {
            _cache.add(logMessage);
        }

        update();
    }

    private String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else return env.getOrDefault("HOSTNAME", "Unknown Computer");
    }

    /**
     * Saves log messages from the cache.
     *
     * @param messages a list with log messages
     * @throws InvocationException when error occured.
     */
    protected abstract void save(List<LogMessage> messages) throws InvocationException;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        super.configure(config);

        this._interval = config.getAsLongWithDefault("options.interval", _interval);
        this._maxCacheSize = config.getAsIntegerWithDefault("options.max_cache_size", _maxCacheSize);
    }

    /**
     * Clears (removes) all cached log messages.
     */
    public void clear() {
        synchronized (_lock) {
            _cache.clear();
            _updated = false;
        }
    }

    /**
     * Dumps (writes) the currently cached log messages.
     *
     * @see #write(LogLevel, IContext, Exception, String)
     */
    public void dump() {
        if (_updated) {
            synchronized (_lock) {
                if (!_updated)
                    return;

                List<LogMessage> messages = _cache;
                _cache = new ArrayList<>();

                try {
                    save(messages);
                } catch (InvocationException e) {
                    // Add messages back to the cache
                    messages.addAll(_cache);
                    _cache = messages;

                    // Truncate cache
                    int deleteCount = this._cache.size() - this._maxCacheSize;
                    if (deleteCount > 0)
                        this._cache = _cache.subList(deleteCount - 1, _cache.size());
                }

                _updated = false;
                _lastDumpTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Makes message cache as updated and dumps it when timeout expires.
     *
     * @see #dump()
     */
    protected void update() {
        _updated = true;
        if (System.currentTimeMillis() > _lastDumpTime + _interval)
            dump();
    }
}
