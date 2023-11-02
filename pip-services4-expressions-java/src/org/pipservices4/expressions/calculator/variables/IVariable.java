package org.pipservices4.expressions.calculator.variables;

import org.pipservices4.expressions.variants.Variant;

/**
 * Defines a variable interface.
 */
public interface IVariable {
    /**
     * Get the variable name.
     */
    String getName();

    /**
     * Set the variable name.
     */
    void setName(String value);

    /**
     * Get the variable value.
     */
    Variant getValue();

    /**
     * Set the variable value.
     */
    void setValue(Variant value);


}
