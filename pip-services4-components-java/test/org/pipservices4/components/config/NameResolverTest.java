package org.pipservices4.components.config;

import static org.junit.Assert.*;

import org.junit.*;

public class NameResolverTest {

	@Test
	public void testNormalNameResolution() {
        ConfigParams config = ConfigParams.fromTuples("id", "ABC");
        String name = NameResolver.resolve(config, null);
        assertEquals("ABC", name);

        config = ConfigParams.fromTuples("name", "ABC");
        name = NameResolver.resolve(config, null);
        assertEquals("ABC", name);
    }

    @Test
    public void testEmptyName() {
        ConfigParams config = ConfigParams.fromTuples();
        String name = NameResolver.resolve(config, null);
        assertNull(name);
    }

}
