package org.pipservices4.expressions.variants;

/**
 * Defines an interface for variant operations manager.
 */
public interface IVariantOperations {
    /**
     * Converts variant to specified type
     *
     * @param value   A variant value to be converted.
     * @param newType A type of object to be returned.
     * @return A converted Variant value.
     */
    Variant convert(Variant value, VariantType newType);

    /**
     * Performs '+' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant add(Variant value1, Variant value2);

    /**
     * Performs '-' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant sub(Variant value1, Variant value2);

    /**
     * Performs '*' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant mul(Variant value1, Variant value2);

    /**
     * Performs '/' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant div(Variant value1, Variant value2);

    /**
     * Performs '%' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant mod(Variant value1, Variant value2);

    /**
     * Performs '^' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant pow(Variant value1, Variant value2);

    /**
     * Performs AND operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant and(Variant value1, Variant value2);

    /**
     * Performs OR operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant or(Variant value1, Variant value2);

    /**
     * Performs XOR operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant xor(Variant value1, Variant value2);

    /**
     * Performs << operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant lsh(Variant value1, Variant value2);

    /**
     * Performs >> operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant rsh(Variant value1, Variant value2);

    /**
     * Performs NOT operation for a variant.
     *
     * @param value The operand for this operation.
     * @return A result variant object.
     */
    Variant not(Variant value);

    /**
     * Performs unary '-' operation for a variant.
     *
     * @param value The operand for this operation.
     * @return A result variant object.
     */
    Variant negative(Variant value);

    /**
     * Performs '=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant equal(Variant value1, Variant value2);

    /**
     * Performs '<>' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant notEqual(Variant value1, Variant value2);

    /**
     * Performs '>' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant more(Variant value1, Variant value2);

    /**
     * Performs '<' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant less(Variant value1, Variant value2);

    /**
     * Performs '>=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant moreEqual(Variant value1, Variant value2);

    /**
     * Performs '<=' operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant lessEqual(Variant value1, Variant value2);

    /**
     * Performs IN operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant in(Variant value1, Variant value2);

    /**
     * Performs [] operation for two variants.
     *
     * @param value1 The first operand for this operation.
     * @param value2 The second operand for this operation.
     * @return A result variant object.
     */
    Variant getElement(Variant value1, Variant value2) throws Exception;
}
