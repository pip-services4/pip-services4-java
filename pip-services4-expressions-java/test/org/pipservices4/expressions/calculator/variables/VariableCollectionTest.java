package org.pipservices4.expressions.calculator.variables;

import org.junit.Test;

import static org.junit.Assert.*;

public class VariableCollectionTest {
    @Test
    public void testAddRemoveVariables() {
        var collection = new VariableCollection();

        var var1 = new Variable("ABC");
        collection.add(var1);
        assertEquals(1, collection.length());

        var var2 = new Variable("XYZ");
        collection.add(var2);
        assertEquals(2, collection.length());

        var index = collection.findIndexByName("abc");
        assertEquals(0, index);

        var v = collection.findByName("Xyz");
        assertEquals(var2, v);

        var var3 = collection.locate("ghi");
        assertNotNull(var3);
        assertEquals("ghi", var3.getName());
        assertEquals(3, collection.length());

        collection.remove(0);
        assertEquals(2, collection.length());

        collection.removeByName("GHI");
        assertEquals(1, collection.length());
    }
}
