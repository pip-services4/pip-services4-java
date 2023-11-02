package org.pipservices4.expressions.calculator.functions;

import org.junit.Test;

import static org.junit.Assert.*;

import org.pipservices4.commons.convert.StringConverter;
import org.pipservices4.expressions.calculator.ExpressionException;
import org.pipservices4.expressions.variants.TypeUnsafeVariantOperations;
import org.pipservices4.expressions.variants.Variant;
import org.pipservices4.expressions.variants.VariantType;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DefaultFunctionCollectionTest {

    @Test
    public void testCalculateFunctions() throws ExpressionException {
        var collection = new DefaultFunctionCollection();
        var params = List.of(
                new Variant(1),
                new Variant(2),
                new Variant(3)
        );

        var operations = new TypeUnsafeVariantOperations();

        var func = collection.findByName("sum");
        assertNotNull(func);

        var result = func.calculate(params, operations);
        assertEquals(VariantType.Integer, result.getType());
        assertEquals(6, (int) result.getAsInteger());
    }

    @Test
    public void testDateFunctions() throws ExpressionException {
        var collection = new DefaultFunctionCollection();
        List<Variant> params = new ArrayList<>();
        var operations = new TypeUnsafeVariantOperations();

        var func = collection.findByName("now");
        assertNotNull(func);

        var result = func.calculate(params, operations);
        assertEquals(VariantType.DateTime, result.getType());

        collection = new DefaultFunctionCollection();
        params = List.of(
                new Variant(1975),
                new Variant(4),
                new Variant(8)
        );

        func = collection.findByName("date");
        assertNotNull(func);

        result = func.calculate(params, operations);
        assertEquals(VariantType.DateTime, result.getType());
        assertEquals(
                StringConverter.toString(
                        ZonedDateTime.of(1975, 4, 8, 0, 0, 0, 0, ZoneId.of("UTC"))
                ),
                StringConverter.toString(result.getAsDateTime())
        );
    }
}
