package org.pipservices4.config.auth;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.*;

public class CredentialResolverTest {
	private static final ConfigParams RestConfig = ConfigParams.fromTuples(
        "credential.username", "Negrienko",
        "credential.password", "qwerty",
        "credential.access_key", "key",
        "credential.store_key", "store key"
	);
	
	@Test
	public void testConfigure() {
		CredentialResolver credentialResolver;
		credentialResolver = new CredentialResolver(RestConfig);
		List<CredentialParams> configList = credentialResolver.getAll();
		System.out.println(configList.get(0).get("username"));
		assertEquals(configList.get(0).get("username"), "Negrienko");
		assertEquals(configList.get(0).get("password"), "qwerty");
		assertEquals(configList.get(0).get("access_key"), "key");
		assertEquals(configList.get(0).get("store_key"), "store key");
	}
	
	@Test
	public void testLookup() throws ApplicationException {
		CredentialResolver credentialResolver;
		credentialResolver = new CredentialResolver();
		CredentialParams credential = credentialResolver.lookup(Context.fromTraceId("context"));
		assertNull(credential);
		
		ConfigParams RestConfigWithoutStoreKey = ConfigParams.fromTuples(
		        "credential.username", "Negrienko",
		        "credential.password", "qwerty",
		        "credential.access_key", "key"
		);
		credentialResolver = new CredentialResolver(RestConfigWithoutStoreKey);
		credential = credentialResolver.lookup(Context.fromTraceId("context"));
		assertEquals(credential.get("username"), "Negrienko");
		assertEquals(credential.get("password"), "qwerty");
		assertEquals(credential.get("access_key"), "key");
		assertNull(credential.get("store_key"));
		
		credentialResolver = new CredentialResolver(RestConfig);
		credential = credentialResolver.lookup(Context.fromTraceId("context"));
		assertNull(credential);
		
		credentialResolver.setReferences(new References());
		try {
			credential = credentialResolver.lookup(Context.fromTraceId("context"));
		} catch (ApplicationException e) {
			assertEquals("Credential store wasn't found to make lookup", e.getMessage());
		}
	}
}
