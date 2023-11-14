package org.pipservices4.container.config;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.config.config.*;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

/**
 * Helper class that reads container configuration from JSON or YAML file.
 */
public class ContainerConfigReader {

    /**
     * Reads container configuration from JSON or YAML file. The type of the file is
     * determined by file extension.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param path          a path to component configuration file.
     * @param parameters    values to parameters the configuration or null to skip
     *                      parameterization.
     * @return the read container configuration
     * @throws ApplicationException when error occured.
     */
    public static ContainerConfig readFromFile(IContext context, String path, ConfigParams parameters)
            throws ApplicationException {
        if (path == null)
            throw new ConfigException(context != null ? ContextResolver.getTraceId(context) : null, "NO_PATH", "Missing config file path");

        int index = path.lastIndexOf('.');
        String ext = index > 0 ? path.substring(index + 1).toLowerCase() : "";

        if (ext.equals("json"))
            return readFromJsonFile(context, path, parameters);
        else if (ext.equals("yaml") || ext.equals("yml"))
            return readFromYamlFile(context, path, parameters);

        // By default read as JSON
        return readFromJsonFile(context, path, parameters);
    }

    /**
     * Reads container configuration from JSON file.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param path          a path to component configuration file.
     * @param parameters    values to parameters the configuration or null to skip
     *                      parameterization.
     * @return the read container configuration.
     * @throws ApplicationException when error occured.
     */
    public static ContainerConfig readFromJsonFile(IContext context, String path, ConfigParams parameters)
            throws ApplicationException {
        ConfigParams config = JsonConfigReader.readConfig(context, path, parameters);
        return ContainerConfig.fromConfig(config);
    }

    /**
     * Reads container configuration from YAML file.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param path          a path to component configuration file.
     * @param parameters    values to parameters the configuration or null to skip
     *                      parameterization.
     * @return the read container configuration.
     * @throws ApplicationException when error occured.
     */
    public static ContainerConfig readFromYamlFile(IContext context, String path, ConfigParams parameters)
            throws ApplicationException {
        ConfigParams config = YamlConfigReader.readConfig(context, path, parameters);
        return ContainerConfig.fromConfig(config);
    }
}
