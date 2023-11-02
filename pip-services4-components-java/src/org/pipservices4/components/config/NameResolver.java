package org.pipservices4.components.config;

import org.pipservices4.components.refer.*;

/**
 * A helper class that allows to extract component name from configuration parameters.
 * The name can be defined in <code>"id"</code>, <code>"name"</code> parameters or inside a component descriptor.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *   "descriptor", "myservice:connector:aws:connector1:1.0",
 *   "param1", "ABC",
 *   "param2", 123
 * );
 *
 * String name = NameResolver.resolve(config); // Result: connector1
 * }
 * </pre>
 */
public class NameResolver {

    /**
     * Resolves a component name from configuration parameters. The name can be
     * stored in "id", "name" fields or inside a component descriptor. If name
     * cannot be determined it returns a defaultName.
     *
     * @param config      configuration parameters that may contain a component
     *                    name.
     * @param defaultName (optional) a default component name.
     * @return resolved name or default name if the name cannot be determined.
     */
    public static String resolve(ConfigParams config, String defaultName) {
        // If name is not defined get is from name property
        String name = config.getAsNullableString("name");
        name = name != null ? name : config.getAsNullableString("id");

        // Or get name from descriptor
        if (name == null) {
            String descriptorStr = config.getAsNullableString("descriptor");
            try {
                Descriptor descriptor = Descriptor.fromString(descriptorStr);
                name = descriptor != null ? descriptor.getName() : null;
            } catch (Exception ex) {
                // Ignore...
            }
        }

        return name != null ? name : defaultName;
    }
}
