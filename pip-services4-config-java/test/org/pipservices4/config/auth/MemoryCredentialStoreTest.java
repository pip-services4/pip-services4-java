package org.pipservices4.config.auth;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.context.Context;

public class MemoryCredentialStoreTest {
    @Test
    public void testLookupAndStore() {
        ConfigParams config = ConfigParams.fromTuples(
                "key1.user", "user1",
                "key1.pass", "pass1",
                "key2.user", "user2",
                "key2.pass", "pass2"
        );

        MemoryCredentialStore credentialStore = new MemoryCredentialStore();
        credentialStore.readCredentials(config);

        CredentialParams cred1 = credentialStore.lookup(Context.fromTraceId("123"), "key1");
        CredentialParams cred2 = credentialStore.lookup(Context.fromTraceId("123"), "key2");

        assertEquals(cred1.getUsername(), "user1");
        assertEquals(cred1.getPassword(), "pass1");
        assertEquals(cred2.getUsername(), "user2");
        assertEquals(cred2.getPassword(), "pass2");

        CredentialParams credConfig = CredentialParams.fromTuples(
                "user", "user3",
                "pass", "pass3",
                "access_id", "123"
        );

        credentialStore.store(null, "key3", credConfig);

        CredentialParams cred3 = credentialStore.lookup(Context.fromTraceId("123"), "key3");

        assertEquals(cred3.getUsername(), "user3");
        assertEquals(cred3.getPassword(), "pass3");
        assertEquals(cred3.getAccessId(), "123");
    }
}
