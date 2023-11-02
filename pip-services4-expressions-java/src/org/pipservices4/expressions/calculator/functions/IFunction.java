package org.pipservices4.expressions.calculator.functions;

import org.pipservices4.expressions.calculator.ExpressionException;
import org.pipservices4.expressions.variants.IVariantOperations;
import org.pipservices4.expressions.variants.Variant;

import java.util.List;

/**
 * Defines an interface for expression function.
 */
public interface IFunction {
    /**
     * The function name.
     */
    void setName(String value);

    /**
     * The function name.
     */
    String getName();

    /**
     * The function calculation method.
     *
     * @param params            an array with function parameters.
     * @param variantOperations Variants operations manager.
     * @return the function result.
     */
    Variant calculate(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException;
}
