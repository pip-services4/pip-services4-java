package org.pipservices4.config.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.FileException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config reader that reads configuration from YAML file.
 * <p>
 * The reader supports parameterization using Handlebar template engine.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>path:          path to configuration file
 * <li>parameters:    this entire section is used as template parameters
 * <li>...
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ======== config.yml ======
 * key1: "{{KEY1_VALUE}}"
 * key2: "{{KEY2_VALUE}}"
 * ===========================
 * 
 * YamlConfigReader configReader = new YamlConfigReader("config.yml");
 * 
 * ConfigParams parameters = ConfigParams.fromTuples("KEY1_VALUE", 123, "KEY2_VALUE", "ABC");
 * configReader.readConfig("123", parameters);
 * }
 * </pre>
 */
public class YamlConfigReader extends FileConfigReader {
	private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
	private static final TypeReference<Object> typeRef = new TypeReference<>() {
	};

	/**
	 * Creates a new instance of the config reader.
	 */
	public YamlConfigReader() {
	}

	/**
	 * Creates a new instance of the config reader.
	 * 
	 * @param path (optional) a path to configuration file.
	 */
	public YamlConfigReader(String path) {
		super(path);
	}

	/**
	 * Reads configuration file, parameterizes its content and converts it into JSON
	 * object.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param parameters    values to parameters the configuration.
	 * @return a JSON object with configuration.
	 * @throws ApplicationException when error occured.
	 */
	public Object readObject(IContext context, ConfigParams parameters) throws ApplicationException {
		if (_path == null)
			throw new ConfigException(context != null ? ContextResolver.getTraceId(context) : null, "NO_PATH", "Missing config file path");

		try {
			Path path = Paths.get(_path);

			String yaml = new String(Files.readAllBytes(path));
			yaml = parameterize(yaml, parameters);

			return yamlMapper.readValue(yaml, typeRef);
		} catch (Exception ex) {
			throw new FileException(context != null ? ContextResolver.getTraceId(context) : null, "READ_FAILED", "Failed reading configuration " + _path + ": " + ex)
					.withDetails("path", _path).withCause(ex);
		}
	}

	/**
	 * Reads configuration and parameterize it with given values.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param parameters    values to parameters the configuration or null to skip
	 *                      parameterization.
	 * @return ConfigParams configuration.
	 * @throws ApplicationException when error occured.
	 */
	@Override
	public ConfigParams readConfig(IContext context, ConfigParams parameters) throws ApplicationException {
		Object value = readObject(context, parameters);
		return ConfigParams.fromValue(value);
	}

	/**
	 * Reads configuration file, parameterizes its content and converts it into JSON
	 * object.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param path          a path to configuration file.
	 * @param parameters    values to parameters the configuration.
	 * @return a JSON object with configuration.
	 * @throws ApplicationException when error occured.
	 */
	public static Object readObject(IContext context, String path, ConfigParams parameters)
			throws ApplicationException {
		return new YamlConfigReader(path).readObject(context, parameters);
	}

	/**
	 * Reads configuration from a file, parameterize it with given values and
	 * returns a new ConfigParams object.
	 * 
	 * @param context (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param path          a path to configuration file.
	 * @param parameters    values to parameters the configuration or null to skip
	 *                      parameterization.
	 * @return ConfigParams configuration.
	 * @throws ApplicationException when error occured.
	 */
	public static ConfigParams readConfig(IContext context, String path, ConfigParams parameters)
			throws ApplicationException {
		return new YamlConfigReader(path).readConfig(context, parameters);
	}
}
