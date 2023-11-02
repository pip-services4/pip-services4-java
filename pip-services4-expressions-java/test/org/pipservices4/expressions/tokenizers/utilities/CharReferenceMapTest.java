package org.pipservices4.expressions.tokenizers.utilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class CharReferenceMapTest {
    @Test
    public void testDefaultInterval() throws Exception {
        var map = new CharReferenceMap<>();
        assertNull(map.lookup('A'));
        assertNull(map.lookup(0x2045));

        map.addDefaultInterval(true);
        assertNotNull(map.lookup('A'));
        assertNotNull(map.lookup(0x2045));

        map.clear();
        assertNull(map.lookup('A'));
        assertNull(map.lookup(0x2045));
    }

    @Test
    public void testInterval() throws Exception {
        var map = new CharReferenceMap<>();
        assertNull(map.lookup('A'));
        assertNull(map.lookup(0x2045));

        map.addInterval('A', 'z', true);
        assertNotNull(map.lookup('A'));
        assertNull(map.lookup(0x2045));

        map.addInterval(0x2000, 0x20ff, true);
        assertNotNull(map.lookup('A'));
        assertNotNull(map.lookup(0x2045));

        map.clear();
        assertNull(map.lookup('A'));
        assertNull(map.lookup(0x2045));

        map.addInterval('A', 0x20ff, true);
        assertNotNull(map.lookup('A'));
        assertNotNull(map.lookup(0x2045));
    }
}
