package org.pipservices4.config.auth;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.commons.errors.*;

public class CredentialParamsTest {
    @Test
    public void testStoreKey() throws ApplicationException {
        CredentialParams credential = new CredentialParams();
        credential.setStoreKey(null);
        assertNull(credential.getStoreKey());

        credential.setStoreKey("Store key");
        assertEquals(credential.getStoreKey(), "Store key");
        assertTrue(credential.useCredentialStore());
    }

    @Test
    public void testUsername() throws ApplicationException {
        CredentialParams credential = new CredentialParams();
        credential.setUsername(null);
        assertNull(credential.getUsername());

        credential.setUsername("Kate Negrienko");
        assertEquals(credential.getUsername(), "Kate Negrienko");
    }

    @Test
    public void testPassword() throws ApplicationException {
        CredentialParams credential = new CredentialParams();
        credential.setPassword(null);
        assertNull(credential.getPassword());

        credential.setPassword("qwerty");
        assertEquals(credential.getPassword(), "qwerty");
    }

    @Test
    public void testAccessKey() throws ApplicationException {
        CredentialParams credential = new CredentialParams();
        credential.setAccessKey(null);
        assertNull(credential.getAccessKey());

        credential.setAccessKey("key");
        assertEquals(credential.getAccessKey(), "key");
    }

}
