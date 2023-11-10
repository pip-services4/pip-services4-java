package org.pipservices4.config.connect;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.*;

public class ConnectionResolverTest {
	private static final ConfigParams RestConfig = ConfigParams.fromTuples(
        "connection.protocol", "http",
        "connection.host", "localhost",
        "connection.port", 3000
	);
	
	@Test
	public void testConfigure() {
		ConnectionResolver connectionResolver = new ConnectionResolver(RestConfig);
		List<ConnectionParams> configList = connectionResolver.getAll();
		assertEquals(configList.get(0).get("protocol"), "http");
		assertEquals(configList.get(0).get("host"), "localhost");
		assertEquals(configList.get(0).get("port"), "3000");
	}
	
	@Test
	public void testRegister() throws ApplicationException {
		ConnectionParams connectionParams = new ConnectionParams();
		ConnectionResolver connectionResolver = new ConnectionResolver(RestConfig);
		connectionResolver.register(Context.fromTraceId("context"), connectionParams);
		List<ConnectionParams> configList = connectionResolver.getAll();
		assertEquals(configList.size(), 1);

		connectionParams.setDiscoveryKey("Discovery key value");
		connectionResolver.register(Context.fromTraceId("context"), connectionParams);
		configList = connectionResolver.getAll();
		assertEquals(configList.size(), 1);
		
		IReferences references = new References();
		connectionResolver.setReferences(references);
		connectionResolver.register(Context.fromTraceId("context"), connectionParams);
		configList = connectionResolver.getAll();
		assertEquals(configList.size(), 2);
		assertEquals(configList.get(0).get("protocol"), "http");
		assertEquals(configList.get(0).get("host"), "localhost");
		assertEquals(configList.get(0).get("port"), "3000");
		assertEquals(configList.get(1).get("discovery_key"), "Discovery key value");
	}
	
	@Test
	public void testResolve() throws ApplicationException  {
		ConnectionResolver connectionResolver = new ConnectionResolver(RestConfig);
		ConnectionParams connectionParams = connectionResolver.resolve(Context.fromTraceId("context"));
		assertEquals(connectionParams.get("protocol"), "http");
		assertEquals(connectionParams.get("host"), "localhost");
		assertEquals(connectionParams.get("port"), "3000");
		
		ConfigParams RestConfigDiscovery = ConfigParams.fromTuples(
			"connection.protocol", "http",
		    "connection.host", "localhost",
		    "connection.port", 3000,
		    "connection.discovery_key", "Discovery key value"
		);
		IReferences references = new References();
		connectionResolver = new ConnectionResolver(RestConfigDiscovery , references);		
		try {
			connectionParams = connectionResolver.resolve(Context.fromTraceId("context"));
		} catch (ApplicationException e) {
			assertEquals("Failed to obtain reference to *:discovery:*:*:*", e.getMessage());
		}
	}
}
