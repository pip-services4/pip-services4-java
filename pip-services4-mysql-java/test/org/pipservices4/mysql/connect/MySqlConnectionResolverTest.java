package org.pipservices4.mysql.connect;

import org.junit.Test;

import static org.junit.Assert.*;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;

public class MySqlConnectionResolverTest {

    @Test
    public void testConnectionConfig() throws ApplicationException {
        var dbConfig = ConfigParams.fromTuples(
                "connection.host", "localhost",
                "connection.port", 3306,
                "connection.database", "test",
                "connection.ssl", false,
                "credential.username", "mysql",
                "credential.password", "mysql"
        );

        var resolver = new MySqlConnectionResolver();
        resolver.configure(dbConfig);

        var uri = resolver.resolve(null);
        assertNotNull(uri);
        assertEquals("mysql://mysql:mysql@localhost:3306/test?ssl=false", uri);
    }

}
