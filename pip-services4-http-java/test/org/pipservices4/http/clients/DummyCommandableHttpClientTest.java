package org.pipservices4.http.clients;

import org.junit.*;

import static org.junit.Assert.*;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.*;
import org.pipservices4.http.controllers.DummyCommandableHttpController;
import org.pipservices4.http.sample.DummyService;

public class DummyCommandableHttpClientTest {

    private static final ConfigParams RestConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", 3000
    );

    private DummyCommandableHttpClient _client;
    private DummyClientFixture _fixture;
    private DummyCommandableHttpController _controller;

    @Before
    public void setUp() throws Exception {
        var service = new DummyService();

        _controller = new DummyCommandableHttpController();
        _client = new DummyCommandableHttpClient();

        _controller.configure(RestConfig);
        _client.configure(RestConfig);

        References references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), _controller
        );

        _client.setReferences(references);
        _controller.setReferences(references);

        _controller.open(null);
        _client.open(null);

        _fixture = new DummyClientFixture(_client);
    }

    @After
    public void tearDown() throws Exception {
        _client.close(null);
        _controller.close(null);
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        _fixture.testCrudOperations();
    }

    @Test
    public void testExceptionPropagation() {
        ApplicationException err = null;
        try {
            _client.raiseException(Context.fromTraceId("123"));
            //_client.wait();
        } catch (ApplicationException ex) {
            err = ex;
        }

        assertNotNull(err);
        assertEquals(err.getCode(), "TEST_ERROR");
    }

}
