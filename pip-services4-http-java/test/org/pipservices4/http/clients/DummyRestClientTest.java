package org.pipservices4.http.clients;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.http.controllers.DummyRestController;
import org.pipservices4.http.sample.DummyService;


import static org.junit.Assert.assertNotNull;

public class DummyRestClientTest {
    static DummyRestController controller;
    static DummyRestClient client;
    static DummyClientFixture fixture;

    static ConfigParams restConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", 3001,
            "options.trace_id_place", "headers"
    );

    @BeforeClass
    public static void setupClass() throws ApplicationException {
        var service = new DummyService();

        controller = new DummyRestController();
        controller.configure(restConfig);

        References references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), controller
        );

        controller.setReferences(references);

        controller.open(null);
    }

    @AfterClass
    public static void teardown() throws ApplicationException {
        controller.close(null);
    }

    @Before
    public void setup() throws ApplicationException {
        client = new DummyRestClient();
        fixture = new DummyClientFixture(client);

        client.configure(restConfig);
        client.setReferences(new References());

        client.open(null);
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }

    @Test
    public void testExceptionPropagation() {
        ApplicationException err = null;
        try {
            client.raiseException(Context.fromTraceId("123"));
            //_client.wait();
        } catch (ApplicationException ex) {
            err = ex;
        }

        assertNotNull(err);
        assertEquals(err.getCode(), "TEST_ERROR");
    }
}
