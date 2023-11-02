package org.pipservices4.expressions.calculator;

import org.pipservices4.expressions.calculator.functions.DefaultFunctionCollection;
import org.pipservices4.expressions.calculator.functions.IFunctionCollection;
import org.pipservices4.expressions.calculator.parsers.ExpressionParser;
import org.pipservices4.expressions.calculator.parsers.ExpressionToken;
import org.pipservices4.expressions.calculator.parsers.ExpressionTokenType;
import org.pipservices4.expressions.calculator.variables.IVariableCollection;
import org.pipservices4.expressions.calculator.variables.Variable;
import org.pipservices4.expressions.calculator.variables.VariableCollection;
import org.pipservices4.expressions.tokenizers.Token;
import org.pipservices4.expressions.variants.IVariantOperations;
import org.pipservices4.expressions.variants.TypeUnsafeVariantOperations;
import org.pipservices4.expressions.variants.Variant;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCalculator {

    private final IVariableCollection _defaultVariables = new VariableCollection();
    private final IFunctionCollection _defaultFunctions = new DefaultFunctionCollection();
    private IVariantOperations _variantOperations = new TypeUnsafeVariantOperations();
    private final ExpressionParser _parser = new ExpressionParser();
    private boolean _autoVariables = true;

    public ExpressionCalculator() throws Exception {
    }

    /**
     * Constructs this class and assigns expression string.
     *
     * @param expression The expression string.
     */
    public ExpressionCalculator(String expression) throws Exception {
        if (expression != null)
            this.setExpression(expression);

    }

    /**
     * The expression string.
     */
    public String getExpression() {
        return this._parser.getExpression();
    }

    /**
     * The expression string.
     */
    public void setExpression(String value) throws Exception {
        this._parser.setExpression(value);
        if (this._autoVariables)
            this.createVariables(this._defaultVariables);

    }

    public List<Token> getOriginalTokens() {
        return this._parser.getOriginalTokens();
    }

    public void setOriginalTokens(List<Token> value) throws SyntaxException {
        this._parser.setOriginalTokens(value);
        if (this._autoVariables)
            this.createVariables(this._defaultVariables);

    }

    /**
     * Gets the flag to turn on auto creation of variables for specified expression.
     */
    public boolean getAutoVariables() {
        return this._autoVariables;
    }

    /**
     * Sets the flag to turn on auto creation of variables for specified expression.
     */
    public void setAutoVariables(boolean value) {
        this._autoVariables = value;
    }

    /**
     * Gets the manager for operations on variant values.
     */
    public IVariantOperations getVariantOperations() {
        return this._variantOperations;
    }

    /**
     * Sets the manager for operations on variant values.
     */
    public void setVariantOperations(IVariantOperations value) {
        this._variantOperations = value;
    }

    /**
     * The list with default variables.
     */
    public IVariableCollection getDefaultVariables() {
        return this._defaultVariables;
    }

    /**
     * The list with default functions.
     */
    public IFunctionCollection getDefaultFunctions() {
        return this._defaultFunctions;
    }

    /**
     * The list of original expression tokens.
     */
    public List<ExpressionToken> getInitialTokens() {
        return this._parser.getInitialTokens();
    }

    /**
     * The list of processed expression tokens.
     */
    public List<ExpressionToken> getResultTokens() {
        return this._parser.getResultTokens();
    }

    /**
     * Populates the specified variables list with variables from parsed expression.
     *
     * @param variables The list of variables to be populated.
     */
    public void createVariables(IVariableCollection variables) {
        for (var variableName : this._parser.getVariableNames()) {
            if (variables.findByName(variableName) == null)
                variables.add(new Variable(variableName));
        }
    }

    /**
     * Cleans up this calculator from all data.
     */
    public void clear() {
        this._parser.clear();
        this._defaultVariables.clear();
    }

    /**
     * Evaluates this expression using default variables and functions.
     *
     * @return the evaluation result.
     */
    public Variant evaluate() throws Exception {
        return this.evaluateWithVariablesAndFunctions(null, null);
    }

    /**
     * Evaluates this expression using specified variables.
     *
     * @param variables The list of variables
     * @return the evaluation result
     */
    public Variant evaluateWithVariables(IVariableCollection variables) throws Exception {
        return this.evaluateWithVariablesAndFunctions(variables, null);
    }

    /**
     * Evaluates this expression using specified variables and functions.
     *
     * @param variables The list of variables
     * @param functions The list of functions
     * @return the evaluation result
     */
    public Variant evaluateWithVariablesAndFunctions(IVariableCollection variables, IFunctionCollection functions) throws Exception {
        var stack = new CalculationStack();
        variables = variables != null ? variables : this._defaultVariables;
        functions = functions != null ? functions : this._defaultFunctions;

        for (var token : this.getResultTokens()) {
            if (this.evaluateConstant(token, stack))
                continue;

            if (this.evaluateVariable(token, stack, variables))
                continue;

            if (this.evaluateFunction(token, stack, functions))
                continue;

            if (this.evaluateLogical(token, stack))
                continue;

            if (this.evaluateArithmetical(token, stack))
                continue;

            if (this.evaluateBoolean(token, stack))
                continue;

            if (this.evaluateOther(token, stack))
                continue;

            throw new ExpressionException(null, "INTERNAL", "Internal error", token.getLine(), token.getColumn());
        }

        if (stack.length() != 1) {
            throw new ExpressionException(null, "INTERNAL", "Internal error", 0, 0);
        }

        return stack.pop();
    }

    private boolean evaluateConstant(ExpressionToken token, CalculationStack stack) {
        if (token.getType() != ExpressionTokenType.Constant)
            return false;

        stack.push(token.getValue());
        return true;
    }

    private boolean evaluateVariable(ExpressionToken token, CalculationStack stack,
                                     IVariableCollection variables) throws ExpressionException {

        if (token.getType() != ExpressionTokenType.Variable)
            return false;

        var variable = variables.findByName(token.getValue().getAsString());
        if (variable == null) {
            throw new ExpressionException(
                    null,
                    "VAR_NOT_FOUND",
                    "Variable " + token.getValue().getAsString() + " was not found",
                    token.getLine(), token.getColumn()
            );
        }

        stack.push(variable.getValue());
        return true;
    }

    private boolean evaluateFunction(ExpressionToken token, CalculationStack stack,
                                     IFunctionCollection functions) throws ExpressionException {

        if (token.getType() != ExpressionTokenType.Function)
            return false;

        var func = functions.findByName(token.getValue().getAsString());
        if (func == null) {
            throw new ExpressionException(
                    null,
                    "FUNC_NOT_FOUND",
                    "Function " + token.getValue().getAsString() + " was not found",
                    token.getLine(), token.getColumn()
            );
        }

        // Retrieve function parameters
        var params = new ArrayList<Variant>();
        var paramCount = stack.pop().getAsInteger();
        while (paramCount > 0) {
            params.add(0, stack.pop());
            paramCount--;
        }

        var functionResult = func.calculate(params, this._variantOperations);
        stack.push(functionResult);
        return true;
    }

    private boolean evaluateLogical(ExpressionToken token, CalculationStack stack) {
        switch (token.getType()) {
            case And: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.and(value1, value2));
                return true;
            }
            case Or: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.or(value1, value2));
                return true;
            }
            case Xor: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.xor(value1, value2));
                return true;
            }
            case Not: {
                stack.push(this._variantOperations.not(stack.pop()));
                return true;
            }
            default:
                return false;
        }
    }

    private boolean evaluateArithmetical(ExpressionToken token, CalculationStack stack) {
        switch (token.getType()) {
            case Plus: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.add(value1, value2));
                return true;
            }
            case Minus: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.sub(value1, value2));
                return true;
            }
            case Star: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.mul(value1, value2));
                return true;
            }
            case Slash: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.div(value1, value2));
                return true;
            }
            case Procent: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.mod(value1, value2));
                return true;
            }
            case Power: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.pow(value1, value2));
                return true;
            }
            case Unary: {
                stack.push(this._variantOperations.negative(stack.pop()));
                return true;
            }
            case ShiftLeft: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.lsh(value1, value2));
                return true;
            }
            case ShiftRight: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.rsh(value1, value2));
                return true;
            }
            default:
                return false;
        }
    }

    private boolean evaluateBoolean(ExpressionToken token, CalculationStack stack) {
        switch (token.getType()) {
            case Equal: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.equal(value1, value2));
                return true;
            }
            case NotEqual: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.notEqual(value1, value2));
                return true;
            }
            case More: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.more(value1, value2));
                return true;
            }
            case Less: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.less(value1, value2));
                return true;
            }
            case EqualMore: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.moreEqual(value1, value2));
                return true;
            }
            case EqualLess: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                stack.push(this._variantOperations.lessEqual(value1, value2));
                return true;
            }
            default:
                return false;
        }
    }

    private boolean evaluateOther(ExpressionToken token, CalculationStack stack) throws Exception {
        switch (token.getType()) {
            case In: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                var rvalue = this._variantOperations.in(value2, value1);
                stack.push(rvalue);
                return true;
            }
            case NotIn: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                var rvalue = this._variantOperations.in(value2, value1);
                rvalue = Variant.fromBoolean(!rvalue.getAsBoolean());
                stack.push(rvalue);
                return true;
            }
            case Element: {
                var value2 = stack.pop();
                var value1 = stack.pop();
                var rvalue = this._variantOperations.getElement(value1, value2);
                stack.push(rvalue);
                return true;
            }
            case IsNull: {
                var rvalue = new Variant(stack.pop().isNull());
                stack.push(rvalue);
                return true;
            }
            case IsNotNull: {
                var rvalue = new Variant(!stack.pop().isNull());
                stack.push(rvalue);
                return true;
            }
            default:
                return false;
        }
    }
}
