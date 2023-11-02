package org.pipservices4.components.config;

import org.pipservices4.commons.errors.*;

/**
 * An interface to set configuration parameters to an object.
 * <p>
 * It can be added to any existing class by implementing a single <code>configure()</code> method.
 * <p>
 * If you need to emphasis the fact that <code>configure()</code> method can be called multiple times
 * to change object configuration in runtime, use {@link IReconfigurable} interface instead.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * public class MyClass implements IConfigurable {
 *    private String _myParam = "default value";
 *
 *    public void configure(ConfigParams config) {
 *      this._myParam = config.getAsStringWithDefault("options.param", myParam);
 *             ...
 *    }
 * }
 * }
 * </pre>
 *
 * @see ConfigParams
 */
public interface IConfigurable {
    /**
     * Configures object by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     * @throws ConfigException when configuration is wrong
     */
    void configure(ConfigParams config) throws ConfigException;
}
