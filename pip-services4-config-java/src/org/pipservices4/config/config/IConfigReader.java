package org.pipservices4.config.config;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.INotifiable;

/**
 * Interface for configuration readers that retrieve configuration from various sources
 * and make it available for other components.
 * <p>
 * Some IConfigReader implementations may support configuration parameterization.
 * The parameterization allows to use configuration as a template and inject there dynamic values.
 * The values may come from application command like arguments or environment variables.
 */
public interface IConfigReader {
	/**
	 * Reads configuration and parameterize it with given values.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param parameters    values to parameters the configuration or null to skip
	 *                      parameterization.
	 * @return ConfigParams configuration.
	 * @throws ApplicationException when error occurred.
	 */
	ConfigParams readConfig(IContext context, ConfigParams parameters) throws Exception;

	/**
	 * Adds a listener that will be notified when configuration is changed
	 * @param listener a listener to be added.
	 */
	void addChangeListener(INotifiable listener);

	/**
	 * Remove a previously added change listener.
	 * @param listener a listener to be removed.
	 */
	void removeChangeListener(INotifiable listener);
}
