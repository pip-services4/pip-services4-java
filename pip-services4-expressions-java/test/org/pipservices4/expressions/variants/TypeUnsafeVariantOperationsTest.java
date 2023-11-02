package org.pipservices4.expressions.variants;

import org.junit.Test;

import static org.junit.Assert.*;

public class TypeUnsafeVariantOperationsTest {
    @Test
    public void testOperations() {
        var a = new Variant("123");
        var manager = new TypeUnsafeVariantOperations();

        var b = manager.convert(a, VariantType.Float);
        assertEquals(VariantType.Float, b.getType());
        assertEquals(123.0, (float) b.getAsFloat(), 0);

        var c = new Variant(2);
        assertEquals(125.0, manager.add(b, c).getAsFloat(), 0);
        assertEquals(121.0, manager.sub(b, c).getAsFloat(), 0);
        assertTrue(manager.equal(a, b).getAsBoolean());
    }
}
