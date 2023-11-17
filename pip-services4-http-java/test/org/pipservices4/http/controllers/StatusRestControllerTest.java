package org.pipservices4.http.controllers;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import static org.junit.Assert.*;

import org.glassfish.jersey.client.*;
import org.glassfish.jersey.jackson.*;
import org.junit.*;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.context.*;

public class StatusRestControllerTest {
    private StatusRestController _controller;

    static int port = 3006;

    @Before
    public void setUp() throws ApplicationException {
        ConfigParams config = ConfigParams.fromTuples(
                "connection.protocol", "http",
                "connection.host", "localhost",
                "connection.port", port
        );
        _controller = new StatusRestController();
        _controller.configure(config);

        ContextInfo contextInfo = new ContextInfo();
        contextInfo.setName("Test");
        contextInfo.setDescription("This is a test container");

        References references = References.fromTuples(
                new Descriptor("pip-services4", "context-info", "default", "default", "1.0"), contextInfo,
                new Descriptor("pip-services4", "status-controller", "http", "default", "1.0"), _controller
        );
        _controller.setReferences(references);

        _controller.open(null);
    }

    @After
    public void tearDown() throws ApplicationException {
        _controller.close(null);
    }

    @Test
    public void testStatus() throws Exception {
        Object value = invoke(Object.class, "/status");
        assertNotNull(value);
    }


    private static <T> T invoke(Class<T> responseClass, String route) throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        Client httpClient = ClientBuilder.newClient(clientConfig);

        try (Response response = httpClient.target("http://localhost:" + port + route)
                .request(MediaType.APPLICATION_JSON)
                .get()) {
            return response.readEntity(responseClass);
        }
    }

}
