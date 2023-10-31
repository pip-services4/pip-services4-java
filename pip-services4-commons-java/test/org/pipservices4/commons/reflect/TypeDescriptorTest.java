package org.pipservices4.commons.reflect;

import org.junit.*;
import org.pipservices4.commons.errors.*;

import static org.junit.Assert.*;

public class TypeDescriptorTest {
    @Test
    public void testFromString() throws Exception {
        TypeDescriptor descriptor = TypeDescriptor.fromString(null);
        assertNull(descriptor);

        descriptor = TypeDescriptor.fromString("xxx,yyy");
        assertEquals("xxx", descriptor.getName());
        assertEquals("yyy", descriptor.getLibrary());

        descriptor = TypeDescriptor.fromString("xxx");
        assertEquals("xxx", descriptor.getName());
        assertNull(descriptor.getLibrary());

        try {
            descriptor = TypeDescriptor.fromString("xxx,yyy,zzz");
            fail("Wrong descriptor shall raise an exception");
        } catch (ConfigException ex) {
            // Ok...
        }
    }

    @Test
    public void testEquals() throws ConfigException {
        TypeDescriptor descriptor1 = TypeDescriptor.fromString("xxx,yyy");
        TypeDescriptor descriptor2 = TypeDescriptor.fromString("xxx,yyy");
        assertEquals(descriptor1, descriptor2);
    }
}
