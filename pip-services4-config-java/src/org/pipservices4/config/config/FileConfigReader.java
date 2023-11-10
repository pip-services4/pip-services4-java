package org.pipservices4.config.config;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;

/**
 * Abstract config reader that reads configuration from a file.
 * Child classes add support for config files in their specific format
 * like JSON, YAML or property files.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>path:          path to configuration file
 * <li>parameters:            this entire section is used as template parameters
 * <li>...
 * </ul>
 *
 * @see IConfigReader
 * @see ConfigReader
 */
public abstract class FileConfigReader extends ConfigReader implements IConfigurable {
    protected String _path;

    /**
     * Creates a new instance of the config reader.
     */
    public FileConfigReader() {
    }

    /**
     * Creates a new instance of the config reader.
     *
     * @param path (optional) a path to configuration file.
     */
    public FileConfigReader(String path) {
        _path = path;
    }

    /**
     * Get the path to configuration file..
     *
     * @return the path to configuration file.
     */
    public String getPath() {
        return _path;
    }

    /**
     * Set the path to configuration file.
     *
     * @param value a new path to configuration file.
     */
    public void setPath(String value) {
        _path = value;
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        super.configure(config);
        _path = config.getAsString("path");
    }
}
