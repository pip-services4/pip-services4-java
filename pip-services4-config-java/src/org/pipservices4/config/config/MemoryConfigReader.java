package org.pipservices4.config.config;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.INotifiable;
import org.pipservices4.expressions.mustache.MustacheTemplate;

import java.util.HashMap;

/**
 * Config reader that stores configuration in memory.
 * <p>
 * The reader supports parameterization using Handlebars
 * template engine.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * The configuration parameters are the configuration template
 * <p>
 * ### Example ####
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *      "connection.host", "{{SERVICE_HOST}}",
 *      "connection.port", "{{SERVICE_PORT}}{{^SERVICE_PORT}}8080{{/SERVICE_PORT}}"
 * );
 *
 * MemoryConfigReader configReader = new MemoryConfigReader();
 * configReader.configure(config);
 *
 * ConfigParams parameters = ConfigParams.fromValue(process.env);
 *
 * configReader.readConfig("123", parameters);
 * }
 * </pre>
 *
 * @see IConfigReader
 */
public class MemoryConfigReader implements IConfigReader, IReconfigurable {
    protected ConfigParams _config = new ConfigParams();

    /**
     * Creates a new instance of config reader.
     */
    public MemoryConfigReader() {
    }

    /**
     * Creates a new instance of config reader.
     *
     * @param config (optional) component configuration parameters
     */
    public MemoryConfigReader(ConfigParams config) {
        _config = config != null ? config : new ConfigParams();
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        _config = config;
    }

    /**
     * Reads configuration and parameterize it with given values.
     *
     * @param context (optional) transaction id to trace execution through call chain.
     * @param parameters    values to parameters the configuration or null to skip parameterization.
     * @return ConfigParams configuration.
     */
    public ConfigParams readConfig(IContext context, ConfigParams parameters) throws Exception {
        if (parameters != null) {
            var config = new ConfigParams(this._config).toString();
            var template = new MustacheTemplate(config);
            config = template.evaluateWithVariables(new HashMap<>(parameters));
            return ConfigParams.fromString(config);
        } else {
            return new ConfigParams(this._config);
        }
    }

    public ConfigParams readConfigSection(IContext context, String section) {
        return _config != null ? _config.getSection(section) : null;
    }

    /**
     * Adds a listener that will be notified when configuration is changed
     *
     * @param listener a listener to be added.
     */
    @Override
    public void addChangeListener(INotifiable listener) {

    }

    /**
     * Remove a previously added change listener.
     *
     * @param listener a listener to be removed.
     */
    @Override
    public void removeChangeListener(INotifiable listener) {

    }
}
