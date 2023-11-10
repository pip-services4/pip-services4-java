package org.pipservices4.config.config;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

public abstract class CachedConfigReader implements IConfigReader, IReconfigurable {
    private long _lastRead = 0;
    private ConfigParams _config;
    private long _timeout = 60000;

    public CachedConfigReader() {}
        
    public long getTimeout() { return _timeout; }
    public void setTimeout(long value) { _timeout = value; }

    public void configure(ConfigParams config) {
        _timeout = config.getAsLongWithDefault("timeout", _timeout);
    }

    protected abstract ConfigParams performReadConfig(IContext context, ConfigParams parameters) throws ApplicationException;

    public ConfigParams readConfig(IContext context, ConfigParams parameters) throws ApplicationException {
        if (_config != null && System.currentTimeMillis() < _lastRead + _timeout)
            return _config;

        _config = performReadConfig(context, parameters);
        _lastRead = System.currentTimeMillis();

        return _config;
    }

    public ConfigParams readConfigSection(IContext context, ConfigParams parameters, String section) throws ApplicationException {
        ConfigParams config = readConfig(context, parameters);
        return config != null ? config.getSection(section) : null;
    }
}
