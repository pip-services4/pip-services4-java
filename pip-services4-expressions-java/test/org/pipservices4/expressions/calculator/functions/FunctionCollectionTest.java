package org.pipservices4.expressions.calculator.functions;

import org.junit.Test;
import org.pipservices4.expressions.variants.Variant;

import static org.junit.Assert.*;

public class FunctionCollectionTest {
    FunctionCalculator testFunc = (stack, operations) -> new Variant("ABC") ;

    @Test
    public void testAddRemoveFunctions() {
        var collection = new FunctionCollection();

        var func1 = new DelegatedFunction("ABC", testFunc);
        collection.add(func1);
        assertEquals(1, collection.length());

        var func2 = new DelegatedFunction("XYZ", testFunc);
        collection.add(func2);
        assertEquals(2, collection.length());

        var index = collection.findIndexByName("abc");
        assertEquals(0, index);

        var func = collection.findByName("Xyz");
        assertEquals(func2, func);

        collection.remove(0);
        assertEquals(1, collection.length());

        collection.removeByName("XYZ");
        assertEquals(0, collection.length());
    }
}
