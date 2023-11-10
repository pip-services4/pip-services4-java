package org.pipservices4.config.config;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.exec.INotifiable;
import org.pipservices4.expressions.mustache.MustacheTemplate;
import org.pipservices4.components.context.IContext;

import java.io.IOException;
import java.util.HashMap;

/**
 * Abstract config reader that supports configuration parameterization.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>parameters:            this entire section is used as template parameters
 * <li>  ...
 * </ul>
 * <p>
 *  @see IConfigReader
 */
public abstract class ConfigReader implements IConfigurable, IConfigReader {
    private ConfigParams _parameters = new ConfigParams();

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        ConfigParams parameters = config.getSection("parameters");
        if (!parameters.isEmpty()) {
            _parameters = parameters;
        }
    }

    /**
     * Reads configuration and parameterize it with given values.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param parameters    values to parameters the configuration or null to skip
     *                      parameterization.
     * @return ConfigParams configuration.
     * @throws ApplicationException when error occured.
     */
    public abstract ConfigParams readConfig(IContext context, ConfigParams parameters) throws ApplicationException;

    /**
     * Parameterized configuration template given as string with dynamic parameters.
     * <p>
     * The method uses Handlebars template engine.
     *
     * @param config     a string with configuration template to be parameterized
     * @param parameters dynamic parameters to inject into the template
     * @return a parameterized configuration string.
     * @throws IOException when input/output error occured.
     */
    protected static String parameterize(String config, ConfigParams parameters) throws Exception {
        if (parameters == null) {
            return config;
        }

        var template = new MustacheTemplate(config);
        return template.evaluateWithVariables(new HashMap<>(parameters));
    }

    /**
     * Adds a listener that will be notified when configuration is changed
     *
     * @param listener a listener to be added.
     */
    @Override
    public void addChangeListener(INotifiable listener) {
        // Do nothing...
    }

    /**
     * Remove a previously added change listener.
     *
     * @param listener a listener to be removed.
     */
    @Override
    public void removeChangeListener(INotifiable listener) {
        // Do nothing...
    }
}