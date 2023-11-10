package org.pipservices4.config.connect;

import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;

import static org.junit.Assert.assertEquals;

public class ConnectionUtilsTest {
    @Test
    public void testParseUri() {
        ConfigParams options = ConnectionUtils.parseUri("http://localhost:8080/test?param1=abc", "http", 80);

        assertEquals("http", options.getAsString("protocol"));
        assertEquals("localhost", options.getAsString("host"));
        assertEquals(8080, options.getAsInteger("port"));
        assertEquals("test", options.getAsString("path"));
        assertEquals("abc", options.getAsString("param1"));
    }

    @Test
    public void testComposeUri() {
        ConfigParams options = ConfigParams.fromTuples(
                "protocol", "http",
                "host", "localhost",
                "port", 8080,
                "path", "test",
                "param1", "abc"
        );

        String uri = ConnectionUtils.composeUri(options, "http", 80);
        assertEquals("http://localhost:8080/test?param1=abc", uri);
    }
}
