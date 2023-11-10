package org.pipservices4.config.connect;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;

public class CompositeConnectionResolverTest {
    @Test
    public void testResolver() throws ApplicationException {
        ConfigParams config = ConfigParams.fromTuples(
                "connection.protocol", "http",
                "connection.host", "localhost",
                "connection.port", 3000,
                "credential.username", "user",
                "credential.password", "pass"
        );

        CompositeConnectionResolver connectionResolver = new CompositeConnectionResolver();
        connectionResolver.configure(config);
        ConfigParams options = connectionResolver.resolve(null);

        assertEquals(options.get("protocol"), "http");
        assertEquals(options.get("host"), "localhost");
        assertEquals(options.get("port"), "3000");
    }
}
