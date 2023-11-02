package org.pipservices4.expressions.calculator.functions;

import org.pipservices4.expressions.calculator.ExpressionException;
import org.pipservices4.expressions.variants.IVariantOperations;
import org.pipservices4.expressions.variants.Variant;

import java.util.List;

public class DelegatedFunction implements IFunction {
    private final String _name;
    private final FunctionCalculator _calculator;

    /**
     * Constructs this function class with specified parameters.
     *
     * @param name       The name of this function.
     * @param calculator The function calculator delegate.
     */
    public DelegatedFunction(String name, FunctionCalculator calculator) {
        if (name == null)
            throw new NullPointerException("Name parameter cannot be null");
        if (calculator == null)
            throw new NullPointerException("Calculator parameter cannot be null");

        this._name = name;
        this._calculator = calculator;
    }

    /**
     * The function name.
     * Can't set.
     */
    @Override
    public void setName(String value) {

    }

    /**
     * The function name.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * The function calculation method.
     *
     * @param params            an array with function parameters.
     * @param variantOperations Variants operations manager.
     */
    @Override
    public Variant calculate(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        return this._calculator.apply(params, variantOperations);
    }
}
