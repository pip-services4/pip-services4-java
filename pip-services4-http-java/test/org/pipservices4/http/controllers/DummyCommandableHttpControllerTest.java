package org.pipservices4.http.controllers;

import org.junit.*;

import static org.junit.Assert.*;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import org.glassfish.jersey.client.*;
import org.glassfish.jersey.jackson.*;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.data.query.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.exec.*;
import org.pipservices4.http.sample.Dummy;
import org.pipservices4.http.sample.DummyService;
import org.pipservices4.http.sample.SubDummy;
import org.pipservices4.observability.log.ConsoleLogger;

import java.util.List;

public class DummyCommandableHttpControllerTest {
    private final Dummy DUMMY1 = new Dummy(null, "Key 1", "Content 1",
            List.of(new SubDummy("SubKey 1", "SubContent 1")));
    private final Dummy DUMMY2 = new Dummy(null, "Key 2", "Content 2",
            List.of(new SubDummy("SubKey 2", "SubContent 2")));

    static int port = 3002;
    private static final ConfigParams restConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", port,
            "swagger.enable", "true"
    );

    private DummyCommandableHttpController _controller;

    @Before
    public void setUp() throws Exception {
        var service = new DummyService();
        _controller = new DummyCommandableHttpController();

        _controller.configure(restConfig);

        References references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "http", "default", "1.0"), _controller,
                new Descriptor("pip-services-dummies", "logger", "*", "*", "*"), new ConsoleLogger()
        );

        _controller.setReferences(references);

        _controller.open(null);
    }

    @After
    public void close() throws Exception {
        _controller.close(null);
    }

    @Test
    public void testCrudOperations() throws Exception {
        // Create one dummy
        Dummy dummy1 = invoke(
                Dummy.class,
                "/dummy/create_dummy",
                Parameters.fromTuples("dummy", DUMMY1)
        );

        assertNotNull(dummy1);
        assertNotNull(dummy1.getId());
        assertEquals(DUMMY1.getKey(), dummy1.getKey());
        assertEquals(DUMMY1.getContent(), dummy1.getContent());

        // Create another dummy
        Dummy dummy2 = invoke(
                Dummy.class,
                "/dummy/create_dummy",
                Parameters.fromTuples("dummy", DUMMY2)
        );

        assertNotNull(dummy2);
        assertNotNull(dummy2.getId());
        assertEquals(DUMMY2.getKey(), dummy2.getKey());
        assertEquals(DUMMY2.getContent(), dummy2.getContent());

        // Get all dummies
        DataPage<Dummy> dummies = invoke(
                new GenericType<>() {
                },
                "/dummy/get_dummies",
                null
        );
        assertNotNull(dummies);
        assertEquals(2, dummies.getData().size());

        // Update the dummy
        dummy1.setContent("Updated Content 1");
        Dummy dummy = invoke(
                Dummy.class,
                "/dummy/update_dummy",
                Parameters.fromTuples("dummy", dummy1)
        );

        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(dummy1.getKey(), dummy.getKey());
        assertEquals("Updated Content 1", dummy.getContent());

        dummy1 = dummy;

        // Get the dummy by id
        dummy = invoke(
                Dummy.class,
                "/dummy/get_dummy_by_id",
                Parameters.fromTuples("dummy_id", dummy1.getId())
        );

        assertNotNull(dummy);
        assertEquals(dummy.getId(), dummy1.getId());
        assertEquals(dummy.getKey(), dummy1.getKey());

        // Delete the dummy
        invoke(
                Dummy.class,
                "/dummy/delete_dummy",
                Parameters.fromTuples("dummy_id", dummy1.getId())
        );

        // Try to get deleted dummy
        dummy = invoke(
                Dummy.class,
                "/dummy/get_dummy_by_id",
                Parameters.fromTuples("dummy_id", dummy1.getId())
        );
        assertNull(dummy);
    }

    @Test
    public void testFailedValidation() {
        // Create one dummy with an invalid id
        var err = invoke(
                ErrorDescription.class,
                "/dummy/create_dummy",
                Parameters.fromTuples()
        );

        assertNotNull(err);
        assertEquals(err.getCode(), "INVALID_DATA");
    }

    @Test
    public void testCheckTraceId() {
        String result = invoke(String.class,
                "/dummy/check_trace_id?trace_id=test_cor_id",
                null);
        var mapRes = JsonConverter.toMap(result);
        assertEquals("test_cor_id", mapRes.get("trace_id"));

        var headers = new MultivaluedHashMap<String, Object>();

        headers.add("trace_id", "test_cor_id_header");
        result = invoke(String.class,
                "/dummy/check_trace_id",
                null, headers);
        mapRes = JsonConverter.toMap(result);

        assertEquals("test_cor_id_header", mapRes.get("trace_id"));
    }

    @Test
    public void testGetOpenApiSpec() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        Client httpClient = ClientBuilder.newClient(clientConfig);

        var url = "http://localhost:" + port;
        var response = httpClient.target(url + "/dummy/swagger")
                .request(MediaType.APPLICATION_JSON).get();
        var res = response.readEntity(String.class);
        assertTrue(res.startsWith("openapi"));
    }

    @Test
    public void testOpenApiSpecOverride() throws ApplicationException {
        var openApiContent = "swagger yaml content";

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        Client httpClient = ClientBuilder.newClient(clientConfig);

        var url = "http://localhost:" + port;

        // recreate service with new configuration
        _controller.close(null);

        var config = restConfig.setDefaults(ConfigParams.fromTuples("swagger.auto", false));

        var service = new DummyService();

        _controller = new DummyCommandableHttpController();
        _controller.configure(config);

        var references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "http", "default", "1.0"), _controller
        );

        _controller.setReferences(references);

        _controller.open(null);

        var response = httpClient.target(url + "/dummy/swagger")
                .request(MediaType.APPLICATION_JSON).get();
        var res = response.readEntity(String.class);

        assertEquals(openApiContent, res);

        _controller.close(null);
    }

    private static Response performInvoke(String route, Object entity, MultivaluedMap<String, Object> headers) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        Client httpClient = ClientBuilder.newClient(clientConfig);

        return httpClient.target("http://localhost:" + port + route)
                .request(MediaType.APPLICATION_JSON).headers(headers)
                .post(Entity.entity(entity, MediaType.APPLICATION_JSON));
    }

    private static Response performInvoke(String route, Object entity) {
        return performInvoke(route, entity, null);
    }

    private static <T> T invoke(Class<T> type, String route, Object entity) {
        try (Response response = performInvoke(route, entity)) {
            try {
                return response.readEntity(type);
            } catch (Exception ex) {
                System.err.println("EXCEEEEEEEEEEEEEEEEEEEEEEEEEEEEPT");
                System.err.println(ex.getMessage());

                StackTraceElement[] ste = ex.getStackTrace();
                StringBuilder builder = new StringBuilder();
                if (ste != null) {
                    for (StackTraceElement stackTraceElement : ste) {
                        if (!builder.isEmpty())
                            builder.append(" ");
                        builder.append(stackTraceElement.toString());
                    }
                }

                System.err.println(builder);
                System.err.println("--------------------------------------------------------");
                System.err.println(response.readEntity(String.class));
                throw ex;
            }
        }

    }

    private static <T> T invoke(Class<T> type, String route, Object entity, MultivaluedMap<String, Object> headers) {
        try (Response response = performInvoke(route, entity, headers)) {
            return response.readEntity(type);
        }

    }

    private static <T> T invoke(GenericType<T> type, String route, Object entity) {
        try (Response response = performInvoke(route, entity)) {
            return response.readEntity(type);
        }
    }

}
