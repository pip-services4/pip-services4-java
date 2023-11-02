package org.pipservices4.expressions.variants;

import org.junit.Test;

import static org.junit.Assert.*;

public class VariantTest {
    @Test
    public void testVariants() {
        var a = new Variant(123);
        assertEquals(VariantType.Integer, a.getType());
        assertEquals(123, (int) a.getAsInteger());
        assertEquals(123, a.getAsObject());

        var b = new Variant("xyz");
        assertEquals(VariantType.String, b.getType());
        assertEquals("xyz", b.getAsString());
        assertEquals("xyz", b.getAsObject());
    }
}
