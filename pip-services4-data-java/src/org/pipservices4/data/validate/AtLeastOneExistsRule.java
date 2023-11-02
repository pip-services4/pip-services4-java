package org.pipservices4.data.validate;

import java.util.*;

import org.pipservices4.commons.reflect.*;

/**
 * Validation rule that check that at least one of the object properties is not null.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Schema schema = new Schema()
 *      .withRule(new AtLeastOneExistsRule("field1", "field2"));
 *
 * schema.validate({ field1: 1, field2: "A" });     // Result: no errors
 * schema.validate({ field1: 1 });                  // Result: no errors
 * schema.validate({ });                            // Result: at least one of properties field1, field2 must exist
 * }
 * </pre>
 *
 * @see IValidationRule
 */
public class AtLeastOneExistsRule implements IValidationRule {
    private final String[] _properties;

    /**
     * Creates a new validation rule and sets its values
     *
     * @param properties a list of property names where at least one property must
     *                   exist
     */
    public AtLeastOneExistsRule(String... properties) {
        _properties = properties;
    }

    /**
     * Validates a given value against this rule.
     *
     * @param path    a dot notation path to the value.
     * @param schema  a schema this rule is called from
     * @param value   a value to be validated.
     * @param results a list with validation results to add new results.
     */
    public void validate(String path, Schema schema, Object value, List<ValidationResult> results) {
        String name = path != null ? path : "value";
        List<String> found = new ArrayList<>();

        for (String property : _properties) {
            Object propertyValue = ObjectReader.getProperty(value, property);
            if (propertyValue != null)
                found.add(property);
        }

        if (found.isEmpty()) {
            results.add(new ValidationResult(path, ValidationResultType.Error, "VALUE_NULL",
                    name + " must have at least one property from " + Arrays.toString(_properties), _properties, null));
        }
    }
}
