package org.pipservices4.aws.controllers;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import org.pipservices4.aws.Dummy;
import org.pipservices4.aws.containers.DummyLambdaFunction;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.data.query.DataPage;
import org.pipservices4.commons.errors.ApplicationException;

import java.util.Map;

public class DummyLambdaControllerTest {

    Dummy _dummy1;
    Dummy _dummy2;

    static DummyLambdaFunction lambda;

    @BeforeClass
    public static void setupAll() throws ApplicationException {
        var config = ConfigParams.fromTuples(
                "logger.descriptor", "pip-services:logger:console:default:1.0",
                "service.descriptor", "pip-services-dummies:service:default:default:1.0",
                "controller.descriptor", "pip-services-dummies:controller:awslambda:default:1.0"
        );

        lambda = new DummyLambdaFunction();
        lambda.configure(config);
        lambda.open(null);
    }

    @Before
    public void setup() {
        _dummy1 = new Dummy(null, "Key 1", "Content 1");
        _dummy2 = new Dummy(null, "Key 2", "Content 2");
    }

    @AfterClass
    public static void teardown() throws ApplicationException {
        lambda.close(null);
    }

    @Test
    public void testCrudOperations() {
        // Create one dummy
        Dummy dummy = (Dummy) lambda.act(Map.of("cmd", "dummies.create_dummy", "dummy", _dummy1));

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy1.getContent());
        assertEquals(dummy.getKey(), _dummy1.getKey());

        var dummy1 = dummy;

        // Create another dummy
        dummy = (Dummy) lambda.act(Map.of("cmd", "dummies.create_dummy", "dummy", _dummy2));

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy2.getContent());
        assertEquals(dummy.getKey(), _dummy2.getKey());

        // Get all dummies
        var dummies = (DataPage<Dummy>) lambda.act(Map.of("cmd", "dummies.get_dummies"));

        assertNotNull(dummies);
        assertEquals(dummies.getData().size(), 2);

        // Update the dummy
        dummy1.setContent("Updated Content 1");
        dummy = (Dummy) lambda.act(Map.of("cmd", "dummies.update_dummy", "dummy", _dummy1));

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), "Updated Content 1");
        assertEquals(dummy.getKey(), _dummy1.getKey());

        dummy1 = dummy;

        // Delete dummy
        lambda.act(Map.of("cmd", "delete_dummy", "dummies.dummy_id", _dummy1.getId()));

        // Try to get delete dummy
        dummy = (Dummy) lambda.act(Map.of("cmd", "dummies.get_dummy_by_id", "dummy_id", _dummy1.getId()));

        assertNull(dummy);
    }
}
