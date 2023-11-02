package org.pipservices4.components.config;

import java.io.Serial;
import java.util.*;

import org.pipservices4.commons.data.StringValueMap;
import org.pipservices4.commons.reflect.*;

/**
 * Contains a key-value map with configuration parameters.
 * All values stored as strings and can be serialized as JSON or string forms.
 * When retrieved the values can be automatically converted on read using <code>getAsXXX()</code> methods.
 * <p>
 * The keys are case-sensitive, so it is recommended to use consistent C-style as: "my_param"
 * <p>
 * Configuration parameters can be broken into sections and subsections using dot notation as:
 * <code>"section1.subsection1.param1"</code>. Using <code>getSection()</code> method all parameters from specified section
 * can be extracted from a ConfigMap.
 * <p>
 * The ConfigParams supports serialization from/to plain strings as:
 * <code>"key1=123;key2=ABC;key3=2016-09-16T00:00:00.00Z"</code>
 * <p>
 * ConfigParams are used to pass configurations to {@link IConfigurable} objects.
 * They also serve as a basis for more concrete configurations such as <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/ConnectionParams.html">ConnectionParams</a>
 * or <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/CredentialParams.html">CredentialParams</a>.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *   "section1.key1", "AAA",
 *   "section1.key2", 123,
 *   "section2.key1", true
 * );
 *
 * config.getAsString("section1.key1"); // Result: AAA
 * config.getAsInteger("section1.key1"); // Result: 0
 *
 * ConfigParams section1 = config.getSection("section1");
 * section1.toString(); // Result: key1=AAA;key2=123
 * }
 * </pre>
 *
 * @see IConfigurable
 * @see StringValueMap
 */
public class ConfigParams extends StringValueMap {
    @Serial
    private static final long serialVersionUID = 671946442626850877L;

    public ConfigParams() {
    }

    /**
     * Creates a new ConfigParams and fills it with values.
     *
     * @param values (optional) an object to be converted into key-value pairs to
     *               initialize this config map.
     * @see StringValueMap
     */
    public ConfigParams(Map<?, ?> values) {
        super(values);
    }

    /**
     * Gets a list with all 1st level section names.
     *
     * @return a list of section names stored in this ConfigMap.
     */
    public List<String> getSectionNames() {
        List<String> sections = new ArrayList<>();

        for (Map.Entry<String, String> entry : this.entrySet()) {
            String key = entry.getKey();
            int pos = key.indexOf('.');
            if (pos > 0)
                key = key.substring(0, pos);

            // Perform case-sensitive search
            boolean found = false;
            for (String section : sections) {
                if (section.equalsIgnoreCase(key)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                sections.add(key);
        }

        return sections;
    }

    /**
     * Gets parameters from specific section stored in this ConfigMap. The section
     * name is removed from parameter keys.
     *
     * @param section name of the section to retrieve configuration parameters from.
     * @return all configuration parameters that belong to the section named
     * 'section'.
     */
    public ConfigParams getSection(String section) {
        ConfigParams result = new ConfigParams();
        String prefix = section + ".";

        for (Map.Entry<String, String> entry : this.entrySet()) {
            String key = entry.getKey();

            // Prevents exception on the next line
            if (key.length() < prefix.length())
                continue;

            // Perform case-sensitive match
            String keyPrefix = key.substring(0, prefix.length());
            if (keyPrefix.equalsIgnoreCase(prefix)) {
                key = key.substring(prefix.length());
                result.put(key, entry.getValue());
            }
        }

        return result;
    }

    /**
     * Adds parameters into this ConfigParams under specified section. Keys for the
     * new parameters are appended with section dot prefix.
     *
     * @param section       name of the section where add new parameters
     * @param sectionParams new parameters to be added.
     */
    public void addSection(String section, ConfigParams sectionParams) {
        if (section == null)
            throw new NullPointerException("Section name cannot be null");

        if (sectionParams != null) {
            for (Map.Entry<String, String> entry : sectionParams.entrySet()) {
                String key = entry.getKey();

                if (!key.isEmpty() && !section.isEmpty())
                    key = section + "." + key;
                else if (key.isEmpty())
                    key = section;

                String value = entry.getValue();

                put(key, value);
            }
        }
    }

    /**
     * Overrides parameters with new values from specified ConfigParams and returns
     * a new ConfigParams object.
     *
     * @param configParams ConfigMap with parameters to override the current values.
     * @return a new ConfigParams object.
     * @see ConfigParams#setDefaults(ConfigParams)
     */
    public ConfigParams override(ConfigParams configParams) {
        StringValueMap map = StringValueMap.fromMaps(this, configParams);
        return new ConfigParams(map);
    }

    /**
     * Set default values from specified ConfigParams and returns a new ConfigParams
     * object.
     *
     * @param defaultConfigParams ConfigMap with default parameter values.
     * @return a new ConfigParams object.
     * @see ConfigParams#override(ConfigParams)
     */
    public ConfigParams setDefaults(ConfigParams defaultConfigParams) {
        StringValueMap map = StringValueMap.fromMaps(defaultConfigParams, this);
        return new ConfigParams(map);
    }

    /**
     * Creates a new ConfigParams object filled with key-value pairs from specified
     * object.
     *
     * @param value an object with key-value pairs used to initialize a new
     *              ConfigParams.
     * @return a new ConfigParams object.
     * @see RecursiveObjectReader#getProperties(Object)
     */
    public static ConfigParams fromValue(Object value) {
        Map<String, Object> map = RecursiveObjectReader.getProperties(value);
        return new ConfigParams(map);
    }

    /**
     * Creates a new ConfigParams object filled with provided key-value pairs called
     * tuples. Tuples parameters contain a sequence of key1, value1, key2, value2,
     * ... pairs.
     *
     * @param tuples the tuples to fill a new ConfigParams object.
     * @return a new ConfigParams object.
     * @see StringValueMap#fromTuplesArray(Object[])
     */
    public static ConfigParams fromTuples(Object... tuples) {
        StringValueMap map = StringValueMap.fromTuplesArray(tuples);
        return new ConfigParams(map);
    }

    /**
     * Creates a new ConfigParams object filled with key-value pairs serialized as a
     * string.
     *
     * @param line a string with serialized key-value pairs as
     *             "key1=value1;key2=value2;..." Example:
     *             "Key1=123;Key2=ABC;Key3=2016-09-16T00:00:00.00Z"
     * @return a new ConfigParams object.
     * @see StringValueMap#fromString(String)
     */
    public static ConfigParams fromString(String line) {
        StringValueMap map = StringValueMap.fromString(line);
        return new ConfigParams(map);
    }

    /**
     * Merges two or more ConfigParams into one. The following ConfigParams override
     * previously defined parameters.
     *
     * @param configs a list of ConfigParams objects to be merged.
     * @return a new ConfigParams object.
     * @see StringValueMap#fromMaps(Map...)
     */
    public static ConfigParams mergeConfigs(ConfigParams... configs) {
        StringValueMap map = StringValueMap.fromMaps(configs);
        return new ConfigParams(map);
    }
}
