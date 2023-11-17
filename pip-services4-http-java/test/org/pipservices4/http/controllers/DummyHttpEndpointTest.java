package org.pipservices4.http.controllers;

import org.junit.*;

import static org.junit.Assert.*;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.components.exec.Parameters;
import org.pipservices4.http.sample.Dummy;
import org.pipservices4.http.sample.DummyService;

import java.util.ArrayList;

public class DummyHttpEndpointTest {

    static int port = 3003;
    private static final ConfigParams RestConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", port
    );
    private DummyCommandableHttpController _controllerV1;
    private DummyCommandableHttpController _controllerV2;

    private HttpEndpoint _httpEndpoint;

    @Before
    public void setUp() throws Exception {
        var service = new DummyService();
        _controllerV1 = new DummyCommandableHttpController();
        _controllerV2 = new DummyCommandableHttpController();

        _httpEndpoint = new HttpEndpoint();

        References references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), _controllerV1,
                new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), _controllerV2,
                new Descriptor("pip-services4", "endpoint", "http", "default", "1.0"), _httpEndpoint);

        _controllerV1.configure(ConfigParams.fromTuples("base_route", "/v1/dummy"));

        _controllerV2.configure(ConfigParams.fromTuples("base_route", "/v2/dummy"));

        _httpEndpoint.configure(RestConfig);

        _controllerV1.setReferences(references);
        _controllerV2.setReferences(references);

        _httpEndpoint.open(null);
        _controllerV1.open(null);
        _controllerV2.open(null);
    }

    @After
    public void close() throws Exception {
        _controllerV1.close(null);
        _controllerV2.close(null);
        _httpEndpoint.open(null);
    }

    @Test
    public void testCrudOperations() throws Exception {
        itShouldBeOpened();

        itShouldCreateDummy();

        itShouldGetDummy();
    }

    public void itShouldBeOpened() {
        assertTrue(_httpEndpoint.isOpen());
    }

    public void itShouldCreateDummy() throws Exception {
        Dummy newDummy = new Dummy("1", "Key 1", "Content 1", new ArrayList<>());

        Dummy resultDummy = invoke(
                Dummy.class,
                "/v1/dummy/create_dummy",
                Parameters.fromTuples("dummy", newDummy)
        );

        assertNotNull(resultDummy);
        assertNotNull(resultDummy.getId());
        assertEquals(newDummy.getKey(), resultDummy.getKey());
        assertEquals(newDummy.getContent(), resultDummy.getContent());
    }

    public void itShouldGetDummy() throws Exception {
        Dummy existingDummy = new Dummy("1", "Key 1", "Content 1", new ArrayList<>());

        Dummy resultDummy = invoke(
                Dummy.class,
                "/v1/dummy/get_dummy_by_id",
                Parameters.fromTuples("dummy_id", existingDummy.getId())
        );

        assertNotNull(resultDummy);
        assertNotNull(resultDummy.getId());
        assertEquals(existingDummy.getKey(), resultDummy.getKey());
        assertEquals(existingDummy.getContent(), resultDummy.getContent());
    }

    private static <T> T invoke(Class<T> type, String route, Object entity) throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(new JacksonFeature());
        Client httpClient = ClientBuilder.newClient(clientConfig);

        String content = JsonConverter.toJson(entity);
        try (Response response = httpClient.target("http://localhost:" + port + route)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(content, MediaType.APPLICATION_JSON))) {
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
                throw ex;
            }
        }
    }

}
