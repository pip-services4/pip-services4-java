package org.pipservices4.components.config;

/**
 * A helper class to parameters from <code>"options"</code> configuration section.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *   ...
 *   "options.param1", "ABC",
 *   "options.param2", 123
 * );
 *
 * ConfigParams options = OptionsResolver.resolve(config, false); // Result: param1=ABC;param2=123
 * }
 * </pre>
 */
public class OptionResolver {
    /**
     * Resolves an "options" configuration section from component configuration
     * parameters.
     *
     * @param config          configuration parameters
     * @param configAsDefault (optional) When set true the method returns the entire
     *                        parameter set when "options" section is not found.
     *                        Default: false
     * @return configuration parameters from "options" section
     */
    public static ConfigParams resolve(ConfigParams config, boolean configAsDefault) {
        ConfigParams options = config.getSection("options");

        if (options.isEmpty() && configAsDefault)
            options = config;

        return options;
    }
}
