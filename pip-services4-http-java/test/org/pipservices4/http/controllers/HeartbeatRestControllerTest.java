package org.pipservices4.http.controllers;

import java.time.Duration;
import java.time.ZonedDateTime;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

import static org.junit.Assert.*;

import org.glassfish.jersey.client.*;
import org.glassfish.jersey.jackson.*;
import org.junit.*;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.convert.DateTimeConverter;
import org.pipservices4.commons.errors.*;

public class HeartbeatRestControllerTest {
    private HeartbeatRestController _controller;

	static int port = 3005;
	@Before
	public void setUp() throws ApplicationException {
        ConfigParams config = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", port
        );
        _controller = new HeartbeatRestController();
        _controller.configure(config);

        _controller.open(null);
	}

	@After
	public void tearDown() throws ApplicationException {
        _controller.close(null);		
	}
	
	@Test
    public void testHeartbeat() throws Exception {
        String value = invoke(String.class, "/heartbeat");
        ZonedDateTime time = DateTimeConverter.toDateTime(value);
        assertNotNull(time);
        assertTrue(Duration.between(time, ZonedDateTime.now()).getSeconds() < 10);
    }
	
	
	private static <T> T invoke(Class<T> responseClass, String route) throws Exception {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new JacksonFeature());
		Client httpClient = ClientBuilder.newClient(clientConfig);
	
		Response response = httpClient.target("http://localhost:" + port + route)
			.request(MediaType.APPLICATION_JSON)
			.get();
	
		return response.readEntity(responseClass);
	}
	
}
