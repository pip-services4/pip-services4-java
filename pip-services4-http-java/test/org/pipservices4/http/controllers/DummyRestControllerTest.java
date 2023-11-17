package org.pipservices4.http.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.http.sample.Dummy;
import org.pipservices4.http.sample.DummyService;
import org.pipservices4.http.sample.SubDummy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DummyRestControllerTest {

    static int port = 3004;
    static ConfigParams restConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", port,
            "swagger.enable", "true",
            "swagger.content", "swagger yaml or json content"  // for test only
    );

    Dummy _dummy1;
    Dummy _dummy2;
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    Client rest;
    static DummyRestController controller;

    @BeforeClass
    public static void setupAll() throws ApplicationException {
        var service = new DummyService();

        controller = new DummyRestController();
        controller.configure(restConfig);

        var references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), controller
        );
        controller.setReferences(references);

        controller.open(null);
    }

    @Before
    public void setup() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        rest = ClientBuilder.newClient(clientConfig);

        _dummy1 = new Dummy(null, "Key 1", "Content 1", List.of(new SubDummy("SubKey 1", "SubContent 1")));
        _dummy2 = new Dummy(null, "Key 2", "Content 2", List.of(new SubDummy("SubKey 2", "SubContent 2")));
    }

    @AfterClass
    public static void teardown() throws ApplicationException {
        controller.close(null);
    }

    @Test
    public void testCrudOperations() {
        // Create one dummy
        Dummy dummy = invoke(Dummy.class, HttpMethod.POST, "/dummies", _dummy1);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy1.getContent());
        assertEquals(dummy.getKey(), _dummy1.getKey());

        var dummy1 = dummy;

        // Create another dummy
        dummy = invoke(Dummy.class, HttpMethod.POST, "/dummies", _dummy2);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy2.getContent());
        assertEquals(dummy.getKey(), _dummy2.getKey());

        // Get all dummies
        var dummies = invoke(DataPage.class, HttpMethod.GET, "/dummies", null);

        assertNotNull(dummies);
        assertEquals(dummies.getData().size(), 2);

        // Update the dummy
        dummy1.setContent("Updated Content 1");
        dummy = invoke(Dummy.class, HttpMethod.PUT, "/dummies", dummy1);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), "Updated Content 1");
        assertEquals(dummy.getKey(), _dummy1.getKey());

        dummy1 = dummy;

        // Delete dummy
        invoke(Object.class, HttpMethod.DELETE, "/dummies/" + dummy1.getId(), null);

        // Try to get delete dummy
        dummy = invoke(Dummy.class, HttpMethod.GET, "/dummies/" + dummy1.getId(), null);

        assertNull(dummy);

        assertEquals(controller.getNumberOfCalls(), 4);
    }

    @Test
    public void testFailedValidation() {
        var err = invoke(ErrorDescription.class, HttpMethod.POST, "/dummies", null);
        assertNotNull(err);
        assertEquals(err.getCode(), "INVALID_DATA");
    }

    @Test
    public void testCheckTraceId() {
        var result = invoke(String.class, HttpMethod.GET, "/dummies/check/trace_id?trace_id=test_cor_id", null);

        var mapRes = JsonConverter.toMap(result);
        assertEquals("test_cor_id", mapRes.get("trace_id"));

        headers.add("trace_id", "test_cor_id_header");

        result = invoke(String.class, HttpMethod.GET, "/dummies/check/trace_id", null);

        mapRes = JsonConverter.toMap(result);
        assertEquals("test_cor_id_header", mapRes.get("trace_id"));
    }

    @Test
    public void testGetOpenApiSpecFromString() {
        var result = invoke(String.class, HttpMethod.GET, "/swagger", null);

        var openApiContent = restConfig.getAsString("swagger.content");
        assertEquals(openApiContent, result);
    }

    @Test
    public void testGetOpenApiSpecFromFile() throws ApplicationException {
        var openApiContent = "swagger yaml content from file";
        var filename = "dummy_" + IdGenerator.nextLong() + ".tmp";

        // create temp file
        try (var fs = new FileOutputStream(filename)) {
            fs.write(openApiContent.getBytes(StandardCharsets.UTF_8));

            // recreate service with new configuration
            controller.close(null);

            var serviceConfig = ConfigParams.fromTuples(
                    "connection.protocol", "http",
                    "connection.host", "localhost",
                    "connection.port", port,
                    "swagger.enable", "true",
                    "swagger.path", filename  // for test only
            );

            var service = new DummyService();
            controller = new DummyRestController();
            controller.configure(serviceConfig);

            var references = References.fromTuples(
                    new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                    new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), controller
            );
            controller.setReferences(references);

            controller.open(null);

            var content = invoke(String.class, HttpMethod.GET,"/swagger", null);
            assertEquals(openApiContent, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // delete temp file
            assertTrue(new File(filename).delete());
        }
    }

    private <T> T invoke(Class<T> type, String method, String route, Object entity) {
        try (Response response = rest.target("http://localhost:" + port + route)
                .request(MediaType.APPLICATION_JSON).headers(headers)
                .method(method, Entity.entity(entity, MediaType.APPLICATION_JSON))) {
            return response.readEntity(type);
        }
    }
}
