package org.pipservices4.expressions.variants;

import java.util.List;
import java.util.Objects;

/**
 * Implements an abstractd variant operations manager object.
 */
public abstract class AbstractVariantOperations implements IVariantOperations {

    /**
     * Convert variant type to string representation
     *
     * @param value a variant type to be converted.
     * @return a string representation of the type.
     */
    protected String typeToString(VariantType value) {
        return switch (value) {
            case Null -> "Null";
            case Integer -> "Integer";
            case Long -> "Long";
            case Float -> "Float";
            case Double -> "Double";
            case String -> "String";
            case Boolean -> "Boolean";
            case DateTime -> "DateTime";
            case TimeSpan -> "TimeSpan";
            case Object -> "Object";
            case Array -> "Array";
            default -> "Unknown";
        };
    }

    /**
     * Converts variant to specified type
     *
     * @param value   A variant value to be converted.
     * @param newType A type of object to be returned.
     * @return A converted Variant value.
     */
    public abstract Variant convert(Variant value, VariantType newType);

    /**
     * Performs '+' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant add(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() + value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() + value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsFloat(value1.getAsFloat() + value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsDouble(value1.getAsDouble() + value2.getAsDouble());
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(value1.getAsTimeSpan() + value2.getAsTimeSpan());
                return result;
            }
            case String -> {
                result.setAsString(value1.getAsString() + value2.getAsString());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '+' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '-' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant sub(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;


        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() - value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() - value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsFloat(value1.getAsFloat() - value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsDouble(value1.getAsDouble() - value2.getAsDouble());
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(value1.getAsTimeSpan() - value2.getAsTimeSpan());
                return result;
            }
            case DateTime -> {
                result.setAsTimeSpan(value1.getAsDateTime().toInstant().toEpochMilli() - value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '-' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '*' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant mul(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;


        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() * value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() * value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsFloat(value1.getAsFloat() * value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsDouble(value1.getAsDouble() * value2.getAsDouble());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '*' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '/' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant div(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() / value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() / value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsFloat(value1.getAsFloat() / value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsDouble(value1.getAsDouble() / value2.getAsDouble());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '/' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '%' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant mod(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() % value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() % value2.getAsLong());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '%' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '^' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant pow(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Performs operation.
        switch (value1.getType()) {
            case Integer, Long, Float, Double -> {
                // Converts second operant to the type of the first operand.
                value1 = this.convert(value1, VariantType.Double);
                value2 = this.convert(value2, VariantType.Double);
                result.setAsDouble(value1.getAsDouble() * value2.getAsDouble());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '^' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs AND operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant and(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() & value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() & value2.getAsLong());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value1.getAsBoolean() && value2.getAsBoolean());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation AND is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs OR operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant or(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() | value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() | value2.getAsLong());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value1.getAsBoolean() || value2.getAsBoolean());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation OR is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs XOR operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant xor(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() ^ value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() ^ value2.getAsLong());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean((value1.getAsBoolean() && !value2.getAsBoolean()) || (!value1.getAsBoolean() && value2.getAsBoolean()));
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation XOR is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '<<' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant lsh(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null) {
            return result;
        }

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, VariantType.Integer);

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() << value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() << value2.getAsInteger());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '<<' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '>>' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant rsh(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null) {
            return result;
        }

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, VariantType.Integer);

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsInteger(value1.getAsInteger() >> value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(value1.getAsLong() >> value2.getAsInteger());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '>>' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs NOT operation for a variant.
     *
     * @param value The operand for this operation.
     * @return A result variant object.
     */
    public Variant not(Variant value) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value.getType() == VariantType.Null) {
            return result;
        }

        // Performs operation.
        switch (value.getType()) {
            case Integer -> {
                result.setAsInteger(~value.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(~value.getAsLong());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(!value.getAsBoolean());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation NOT is not supported for type " + this.typeToString(value.getType()));
    }

    /**
     * Performs unary '-' operation for a variant.
     *
     * @param value The operand for this operation.
     * @return A result variant object.
     */
    public Variant negative(Variant value) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value.getType() == VariantType.Null)
            return result;

        // Performs operation.
        switch (value.getType()) {
            case Integer -> {
                result.setAsInteger(-value.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsLong(-value.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsFloat(-value.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsDouble(-value.getAsDouble());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation unary '-' is not supported for type " + this.typeToString(value.getType()));
    }

    /**
     * Performs '=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant equal(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null && value2.getType() == VariantType.Null) {
            result.setAsBoolean(true);
            return result;
        }
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null) {
            result.setAsBoolean(false);
            return result;
        }

        // Fix for double and float string numbers.
        boolean isNumeric;
        {
            var numericTypes = List.of(
                    VariantType.Integer,
                    VariantType.Long,
                    VariantType.Float,
                    VariantType.Double
            );
            isNumeric = numericTypes.contains(value1.getType()) || numericTypes.contains(value2.getType());
        }



        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(Objects.equals(value1.getAsInteger(), value2.getAsInteger()));
                return result;
            }
            case Long -> {
                result.setAsBoolean(Objects.equals(value1.getAsLong(), value2.getAsLong()));
                return result;
            }
            case Float -> {
                result.setAsBoolean(Objects.equals(value1.getAsFloat(), value2.getAsFloat()));
                return result;
            }
            case Double -> {
                result.setAsBoolean(Objects.equals(value1.getAsDouble(), value2.getAsDouble()));
                return result;
            }
            case String -> {
                if (isNumeric) {
                    value1.setAsString(value1.getAsString().replaceAll(".0+$", ""));
                    value2.setAsString(value2.getAsString().replaceAll(".0+$", ""));
                }
                result.setAsBoolean(Objects.equals(value1.getAsString(), value2.getAsString()));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(Objects.equals(value1.getAsTimeSpan(), value2.getAsTimeSpan()));
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() == value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value1.getAsBoolean() == value2.getAsBoolean());
                return result;
            }
            case Object -> {
                result.setAsObject(value1.getAsObject() == value2.getAsObject());
                return result;
            }
        }

        throw new UnsupportedOperationException("Operation '=' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '<>' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant notEqual(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null && value2.getType() == VariantType.Null) {
            result.setAsBoolean(false);
            return result;
        }
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null) {
            result.setAsBoolean(true);
            return result;
        }

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(!Objects.equals(value1.getAsInteger(), value2.getAsInteger()));
                return result;
            }
            case Long -> {
                result.setAsBoolean(!Objects.equals(value1.getAsLong(), value2.getAsLong()));
                return result;
            }
            case Float -> {
                result.setAsBoolean(!Objects.equals(value1.getAsFloat(), value2.getAsFloat()));
                return result;
            }
            case Double -> {
                result.setAsBoolean(!Objects.equals(value1.getAsDouble(), value2.getAsDouble()));
                return result;
            }
            case String -> {
                result.setAsBoolean(!Objects.equals(value1.getAsString(), value2.getAsString()));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(!Objects.equals(value1.getAsTimeSpan(), value2.getAsTimeSpan()));
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() != value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value1.getAsBoolean() != value2.getAsBoolean());
                return result;
            }
            case Object -> {
                result.setAsObject(value1.getAsObject() != value2.getAsObject());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '<>' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '>' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant more(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(value1.getAsInteger() > value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsBoolean(value1.getAsLong() > value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsBoolean(value1.getAsFloat() > value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsBoolean(value1.getAsDouble() > value2.getAsDouble());
                return result;
            }
            case String -> {
                result.setAsBoolean(value1.getAsString().charAt(0) > value2.getAsString().charAt(0));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(value1.getAsTimeSpan() > value2.getAsTimeSpan());
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() > value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '>' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '<' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant less(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operand to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(value1.getAsInteger() < value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsBoolean(value1.getAsLong() < value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsBoolean(value1.getAsFloat() < value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsBoolean(value1.getAsDouble() < value2.getAsDouble());
                return result;
            }
            case String -> {
                result.setAsBoolean(value1.getAsString().charAt(0) < value2.getAsString().charAt(0));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(value1.getAsTimeSpan() < value2.getAsTimeSpan());
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() < value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '<' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '>=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant moreEqual(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(value1.getAsInteger() >= value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsBoolean(value1.getAsLong() >= value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsBoolean(value1.getAsFloat() >= value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsBoolean(value1.getAsDouble() >= value2.getAsDouble());
                return result;
            }
            case String -> {
                result.setAsBoolean(value1.getAsString().charAt(0) >= value2.getAsString().charAt(0));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(value1.getAsTimeSpan() >= value2.getAsTimeSpan());
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() >= value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '>=' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs '<=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant lessEqual(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Converts second operant to the type of the first operand.
        value2 = this.convert(value2, value1.getType());

        // Performs operation.
        switch (value1.getType()) {
            case Integer -> {
                result.setAsBoolean(value1.getAsInteger() <= value2.getAsInteger());
                return result;
            }
            case Long -> {
                result.setAsBoolean(value1.getAsLong() <= value2.getAsLong());
                return result;
            }
            case Float -> {
                result.setAsBoolean(value1.getAsFloat() <= value2.getAsFloat());
                return result;
            }
            case Double -> {
                result.setAsBoolean(value1.getAsDouble() <= value2.getAsDouble());
                return result;
            }
            case String -> {
                result.setAsBoolean(value1.getAsString().charAt(0) <= value2.getAsString().charAt(0));
                return result;
            }
            case TimeSpan -> {
                result.setAsBoolean(value1.getAsTimeSpan() <= value2.getAsTimeSpan());
                return result;
            }
            case DateTime -> {
                result.setAsBoolean(value1.getAsDateTime().toInstant().toEpochMilli() <= value2.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
        }
        throw new UnsupportedOperationException("Operation '<=' is not supported for type " + this.typeToString(value1.getType()));
    }

    /**
     * Performs IN operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant in(Variant value1, Variant value2) {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null)
            return result;

        // Processes null arrays.
        if (value1.getAsObject() == null) {
            result.setAsBoolean(false);
            return result;
        }

        if (value1.getType() == VariantType.Array) {
            var array = value1.getAsArray();
            for (var element : array) {
                var eq = this.equal(value2, element);
                if (eq.getType() == VariantType.Boolean && eq.getAsBoolean()) {
                    result.setAsBoolean(true);
                    return result;
                }
            }
            result.setAsBoolean(false);
            return result;
        }
        return this.equal(value1, value2);
    }

    /**
     * Performs [] operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    public Variant getElement(Variant value1, Variant value2) throws Exception {
        var result = new Variant();

        // Processes VariantType.Null values.
        if (value1.getType() == VariantType.Null || value2.getType() == VariantType.Null) {
            return result;
        }

        value2 = this.convert(value2, VariantType.Integer);

        if (value1.getType() == VariantType.Array) {
            return value1.getByIndex(value2.getAsInteger());
        } else if (value1.getType() == VariantType.String) {
            result.setAsString(String.valueOf(value1.getAsString().charAt(value2.getAsInteger())));
            return result;
        }
        throw new UnsupportedOperationException("Operation '[]' is not supported for type " + this.typeToString(value1.getType()));
    }

}
