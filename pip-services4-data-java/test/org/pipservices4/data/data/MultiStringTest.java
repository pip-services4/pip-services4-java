package org.pipservices4.data.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class MultiStringTest {

    @Test
    public void testGetAndSet() {
        MultiString value = new MultiString();
        assertNull(value.get("en"));

        value.put("ru", "Russian");
        assertEquals("Russian", value.get("ru"));
        assertEquals("Russian", value.get("en"));
        assertEquals("Russian", value.get("pt"));

        value.put("en", "English");
        assertEquals("Russian", value.get("ru"));
        assertEquals("English", value.get("en"));
        assertEquals("English", value.get("pt"));
    }
}
