package org.pipservices4.components.refer;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.commons.errors.*;

public class DescriptorTest {
    @Test
    public void testMatch() {
        Descriptor descriptor = new Descriptor("pip-dummies", "controller", "default", "default", "1.0");

        // Check match by individual fields
        assertTrue(descriptor.match(new Descriptor(null, null, null, null, null)));
        assertTrue(descriptor.match(new Descriptor("pip-dummies", "controller", null, null, null)));
        assertTrue(descriptor.match(new Descriptor(null, null, "default", null, null)));
        assertTrue(descriptor.match(new Descriptor(null, null, null, null, "1.0")));

        // Check match by individual "*" fields
        assertTrue(descriptor.match(new Descriptor("pip-dummies", "*", "*", "*", "*")));
        assertTrue(descriptor.match(new Descriptor("*", "controller", "*", "*", "*")));
        assertTrue(descriptor.match(new Descriptor("*", "*", "default", "*", "*")));
        assertTrue(descriptor.match(new Descriptor("*", "*", "*", "*", "1.0")));

        // Check match by all values
        assertTrue(descriptor.match(new Descriptor("pip-dummies", "controller", "default", "default", null)));
        assertTrue(descriptor.match(new Descriptor(null, "controller", "default", "default", "1.0")));
        assertTrue(descriptor.match(new Descriptor("pip-dummies", "controller", "default", "default", "1.0")));

        // Check mismatch by individual fields
        assertFalse(descriptor.match(new Descriptor(null, "cache", null, null, null)));
        assertFalse(descriptor.match(new Descriptor("pip-commons", "controller", null, null, null)));
        assertFalse(descriptor.match(new Descriptor(null, null, "special", null, null)));
        assertFalse(descriptor.match(new Descriptor(null, null, null, null, "2.0")));
    }

    @Test
    public void testToString() {
        Descriptor descriptor1 = new Descriptor("pip-dummies", "controller", "default", "default", "1.0");
        assertEquals("pip-dummies:controller:default:default:1.0", descriptor1.toString());

        Descriptor descriptor2 = new Descriptor(null, "controller", null, null, null);
        assertEquals("*:controller:*:*:*", descriptor2.toString());
    }

    @Test
    public void testFromString() throws Exception {
        Descriptor descriptor = Descriptor.fromString(null);
        assertNull(descriptor);

        descriptor = Descriptor.fromString("pip-dummies:controller:default:default:1.0");
        assertTrue(descriptor.exactMatch(new Descriptor("pip-dummies", "controller", "default", "default", "1.0")));

        try {
            Descriptor.fromString("xxx");
            fail("Descriptor.fromString shall throw an exception");
        } catch (ConfigException ex) {
            // Ok...
        }
    }

    @Test
    public void testEquals() throws ConfigException {
        Descriptor descriptor1 = Descriptor.fromString("pip-dummies:controller:default:default:1.0");
        Descriptor descriptor2 = Descriptor.fromString("pip-dummies:controller:default:default:1.0");
        assertEquals(descriptor1, descriptor2);
    }
}
