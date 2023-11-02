package org.pipservices4.expressions.calculator.functions;

import org.pipservices4.expressions.calculator.ExpressionException;
import org.pipservices4.expressions.variants.IVariantOperations;
import org.pipservices4.expressions.variants.Variant;
import org.pipservices4.expressions.variants.VariantType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Implements a list filled with standard functions.
 */
public class DefaultFunctionCollection extends FunctionCollection {
    /**
     * Constructs this list and fills it with the standard functions.
     */
    public DefaultFunctionCollection() {
        super();

        this.add(new DelegatedFunction("Ticks", this::ticksFunctionCalculator));
        this.add(new DelegatedFunction("TimeSpan", this::timeSpanFunctionCalculator));
        this.add(new DelegatedFunction("Now", this::nowFunctionCalculator));
        this.add(new DelegatedFunction("Date", this::dateFunctionCalculator));
        this.add(new DelegatedFunction("DayOfWeek", this::dayOfWeekFunctionCalculator));
        this.add(new DelegatedFunction("Min", this::minFunctionCalculator));
        this.add(new DelegatedFunction("Max", this::maxFunctionCalculator));
        this.add(new DelegatedFunction("Sum", this::sumFunctionCalculator));
        this.add(new DelegatedFunction("If", this::ifFunctionCalculator));
        this.add(new DelegatedFunction("Choose", this::chooseFunctionCalculator));
        this.add(new DelegatedFunction("E", this::eFunctionCalculator));
        this.add(new DelegatedFunction("Pi", this::piFunctionCalculator));
        this.add(new DelegatedFunction("Rnd", this::rndFunctionCalculator));
        this.add(new DelegatedFunction("Random", this::rndFunctionCalculator));
        this.add(new DelegatedFunction("Abs", this::absFunctionCalculator));
        this.add(new DelegatedFunction("Acos", this::acosFunctionCalculator));
        this.add(new DelegatedFunction("Asin", this::asinFunctionCalculator));
        this.add(new DelegatedFunction("Atan", this::atanFunctionCalculator));
        this.add(new DelegatedFunction("Exp", this::expFunctionCalculator));
        this.add(new DelegatedFunction("Log", this::logFunctionCalculator));
        this.add(new DelegatedFunction("Ln", this::logFunctionCalculator));
        this.add(new DelegatedFunction("Log10", this::log10FunctionCalculator));
        this.add(new DelegatedFunction("Ceil", this::ceilFunctionCalculator));
        this.add(new DelegatedFunction("Ceiling", this::ceilFunctionCalculator));
        this.add(new DelegatedFunction("Floor", this::floorFunctionCalculator));
        this.add(new DelegatedFunction("Round", this::roundFunctionCalculator));
        this.add(new DelegatedFunction("Trunc", this::truncFunctionCalculator));
        this.add(new DelegatedFunction("Truncate", this::truncFunctionCalculator));
        this.add(new DelegatedFunction("Cos", this::cosFunctionCalculator));
        this.add(new DelegatedFunction("Sin", this::sinFunctionCalculator));
        this.add(new DelegatedFunction("Tan", this::tanFunctionCalculator));
        this.add(new DelegatedFunction("Sqr", this::sqrtFunctionCalculator));
        this.add(new DelegatedFunction("Sqrt", this::sqrtFunctionCalculator));
        this.add(new DelegatedFunction("Empty", this::emptyFunctionCalculator));
        this.add(new DelegatedFunction("Null", this::nullFunctionCalculator));
        this.add(new DelegatedFunction("Contains", this::containsFunctionCalculator));
        this.add(new DelegatedFunction("Array", this::arrayFunctionCalculator));
    }

    /**
     * Checks if params contains the correct number of function parameters (must be stored on the top of the params).
     *
     * @param params             A list of function parameters.
     * @param expectedParamCount The expected number of function parameters.
     */
    protected void checkParamCount(List<Variant> params, int expectedParamCount) throws ExpressionException {
        int paramCount = params.size();
        if (expectedParamCount != paramCount) {
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected " + expectedParamCount
                            + " parameters but was found " + paramCount);
        }
    }

    /**
     * Gets function parameter by it's index.
     *
     * @param params     A list of function parameters.
     * @param paramIndex Index for the function parameter (0 for the first parameter).
     * @return Function parameter value.
     */
    protected Variant getParameter(List<Variant> params, int paramIndex) {
        return params.get(paramIndex);
    }

    private Variant ticksFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return Variant.fromLong(System.currentTimeMillis());
    }

    private Variant timeSpanFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        int paramCount = params.size();
        if (paramCount != 1 && paramCount != 3 && paramCount != 4 && paramCount != 5)
            throw new ExpressionException(null, "WRONG_PARAM_COUNT", "Expected 1, 3, 4 or 5 parameters");

        Variant result = new Variant();

        if (paramCount == 1) {
            var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Long);
            result.setAsTimeSpan(value.getAsLong());
        } else if (paramCount > 2) {
            var value1 = variantOperations.convert(this.getParameter(params, 0), VariantType.Long);
            var value2 = variantOperations.convert(this.getParameter(params, 1), VariantType.Long);
            var value3 = variantOperations.convert(this.getParameter(params, 2), VariantType.Long);
            var value4 = paramCount > 3 ? variantOperations.convert(this.getParameter(params, 3), VariantType.Long) : Variant.fromLong(0L);
            var value5 = paramCount > 4 ? variantOperations.convert(this.getParameter(params, 4), VariantType.Long) : Variant.fromLong(0L);

            result.setAsTimeSpan(
                    (((value1.getAsLong() * 24 + value2.getAsLong()) * 60 + value3.getAsLong()) * 60 + value4.getAsLong()) * 1000 + value5.getAsLong()
            );
        }

        return result;
    }

    private Variant nowFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return Variant.fromDateTime(ZonedDateTime.now());
    }

    private Variant dateFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        var paramCount = params.size();
        if (paramCount < 1 || paramCount > 7) {
            throw new ExpressionException(null, "WRONG_PARAM_COUNT", "Expected from 1 to 7 parameters");
        }

        if (paramCount == 1) {
            var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Long);
            return Variant.fromDateTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getAsLong()), ZoneId.of("UTC")));
        }

        var value1 = variantOperations.convert(this.getParameter(params, 0), VariantType.Integer);
        var value2 = paramCount > 1 ? variantOperations.convert(this.getParameter(params, 1), VariantType.Integer) : Variant.fromInteger(1);
        var value3 = paramCount > 2 ? variantOperations.convert(this.getParameter(params, 2), VariantType.Integer) : Variant.fromInteger(1);
        var value4 = paramCount > 3 ? variantOperations.convert(this.getParameter(params, 3), VariantType.Integer) : Variant.fromInteger(0);
        var value5 = paramCount > 4 ? variantOperations.convert(this.getParameter(params, 4), VariantType.Integer) : Variant.fromInteger(0);
        var value6 = paramCount > 5 ? variantOperations.convert(this.getParameter(params, 5), VariantType.Integer) : Variant.fromInteger(0);
        var value7 = paramCount > 6 ? variantOperations.convert(this.getParameter(params, 6), VariantType.Integer) : Variant.fromInteger(0);

        var date = ZonedDateTime.of(value1.getAsInteger(), value2.getAsInteger(), value3.getAsInteger(),
                value4.getAsInteger(), value5.getAsInteger(), value6.getAsInteger(), value7.getAsInteger(), ZoneId.of("UTC"));
        return Variant.fromDateTime(date);
    }

    private Variant dayOfWeekFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.DateTime);
        var date = value.getAsDateTime();
        return Variant.fromInteger(date.getDayOfMonth());
    }

    private Variant minFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        int paramCount = params.size();
        if (paramCount < 2) {
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected at least 2 parameters");
        }
        var result = this.getParameter(params, 0);
        for (int i = 1; i < paramCount; i++) {
            var value = this.getParameter(params, i);
            if (variantOperations.more(result, value).getAsBoolean())
                result = value;
        }
        return result;
    }

    private Variant maxFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        var paramCount = params.size();
        if (paramCount < 2)
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected at least 2 parameters");

        var result = this.getParameter(params, 0);
        for (int i = 1; i < paramCount; i++) {
            var value = this.getParameter(params, i);
            if (variantOperations.less(result, value).getAsBoolean())
                result = value;
        }
        return result;
    }

    private Variant sumFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        int paramCount = params.size();
        if (paramCount < 2)
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected at least 2 parameters");

        Variant result = this.getParameter(params, 0);
        for (int i = 1; i < paramCount; i++) {
            var value = this.getParameter(params, i);
            result = variantOperations.add(result, value);
        }
        return result;
    }

    private Variant ifFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 3);
        var value1 = this.getParameter(params, 0);
        var value2 = this.getParameter(params, 1);
        var value3 = this.getParameter(params, 2);
        var condition = variantOperations.convert(value1, VariantType.Boolean);
        return condition.getAsBoolean() ? value2 : value3;
    }

    private Variant chooseFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        int paramCount = params.size();
        if (paramCount < 3)
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected at least 3 parameters");

        var value1 = this.getParameter(params, 0);
        var condition = variantOperations.convert(value1, VariantType.Integer);
        var paramIndex = condition.getAsInteger();

        if (paramCount < paramIndex + 1)
            throw new ExpressionException(null, "WRONG_PARAM_COUNT",
                    "Expected at least " + (paramIndex + 1) + " parameters");

        return this.getParameter(params, paramIndex);
    }

    private Variant eFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return new Variant(Math.E);
    }

    private Variant piFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return new Variant(Math.PI);
    }

    private Variant rndFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return new Variant(Math.random());
    }

    private Variant absFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = this.getParameter(params, 0);
        var result = new Variant();

        switch (value.getType()) {
            case Integer -> result.setAsInteger(Math.abs(value.getAsInteger()));
            case Long -> result.setAsLong(Math.abs(value.getAsLong()));
            case Float -> result.setAsFloat(Math.abs(value.getAsFloat()));
            case Double -> result.setAsDouble(Math.abs(value.getAsDouble()));
            default -> {
                value = variantOperations.convert(value, VariantType.Double);
                result.setAsDouble(Math.abs(value.getAsDouble()));
            }
        }
        return result;
    }

    private Variant acosFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.acos(value.getAsDouble()));
    }

    private Variant asinFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.asin(value.getAsDouble()));
    }

    private Variant atanFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.atan(value.getAsDouble()));
    }

    private Variant expFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.exp(value.getAsDouble()));
    }

    private Variant logFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.log(value.getAsDouble()));
    }

    private Variant log10FunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.log10(value.getAsDouble()));
    }

    private Variant ceilFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.ceil(value.getAsDouble()));
    }

    private Variant floorFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(value.getAsDouble().intValue());
    }

    private Variant roundFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.round(value.getAsDouble()));
    }

    private Variant truncFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return Variant.fromInteger(value.getAsDouble().intValue());
    }

    private Variant cosFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.cos(value.getAsDouble()));
    }

    private Variant sinFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.sin(value.getAsDouble()));
    }

    private Variant tanFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.tan(value.getAsDouble()));
    }

    private Variant sqrtFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = variantOperations.convert(this.getParameter(params, 0), VariantType.Double);
        return new Variant(Math.sqrt(value.getAsDouble()));
    }

    private Variant emptyFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 1);
        var value = this.getParameter(params, 0);
        return new Variant(value.isEmpty());
    }

    private Variant nullFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 0);
        return new Variant();
    }

    private Variant containsFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) throws ExpressionException {
        this.checkParamCount(params, 2);
        var containerstr = variantOperations.convert(this.getParameter(params, 0), VariantType.String);
        var substring = variantOperations.convert(this.getParameter(params, 1), VariantType.String);

        if (containerstr.isEmpty() || containerstr.isNull())
            return Variant.fromBoolean(false);

        return Variant.fromBoolean(containerstr.getAsString().contains(substring.getAsString()));
    }

    private Variant arrayFunctionCalculator(List<Variant> params, IVariantOperations variantOperations) {
        return Variant.fromArray(params);
    }
}
