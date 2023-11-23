package org.pipservices4.grpc.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;

import static org.junit.Assert.assertTrue;

public class GrpcEndpointTest {
    static ConfigParams grpcConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", 3000
            );

    GrpcEndpoint endpoint;

    @Before
    public void setup() throws ApplicationException {
        endpoint = new GrpcEndpoint();
        endpoint.configure(grpcConfig);

        endpoint.open(null);
    }

    @After
    public void teardown() {
        endpoint.close(null);
    }

    @Test
    public void testIsOpen() {
        assertTrue(endpoint.isOpen());
    }
}
