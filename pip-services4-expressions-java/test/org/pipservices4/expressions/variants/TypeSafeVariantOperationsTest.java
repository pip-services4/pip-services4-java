package org.pipservices4.expressions.variants;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TypeSafeVariantOperationsTest {
    @Test
    public void testOperations() throws Exception {
        var a = new Variant(123);
        var manager = new TypeSafeVariantOperations();

        var b = manager.convert(a, VariantType.Float);
        assertEquals(VariantType.Float, b.getType());
        assertEquals(123.0, (float) b.getAsFloat(), 0);

        var c = new Variant(2);
        assertEquals(125, (int) manager.add(a, c).getAsInteger());
        assertEquals(121, (int) manager.sub(a, c).getAsInteger());
        assertFalse(manager.equal(a, c).getAsBoolean());

        var array = List.of(new Variant("aaa"), new Variant("bbb"), new Variant("ccc"), new Variant("ddd"));
        var d = new Variant(array);
        assertTrue(manager.in(d, new Variant("ccc")).getAsBoolean());
        assertFalse(manager.in(d, new Variant("eee")).getAsBoolean());
        assertEquals("bbb", manager.getElement(d, new Variant(1)).getAsString());
    }
}
