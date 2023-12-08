package org.pipservices4.aws.containers;

import org.junit.*;

import static org.junit.Assert.*;

import org.pipservices4.aws.Dummy;
import org.pipservices4.components.config.*;
import org.pipservices4.components.exec.*;
import org.pipservices4.data.query.DataPage;

public class DummyCommandableLambdaFunctionTest {
    private final Dummy DUMMY1 = new Dummy(null, "Key 1", "Content 1");
    private final Dummy DUMMY2 = new Dummy(null, "Key 2", "Content 2");

    private DummyCommandableLambdaFunction lambda;

    @Before
    public void setUp() throws Exception {
        var config = ConfigParams.fromTuples(
                "logger.descriptor", "pip-services:logger:console:default:1.0",
                "service.descriptor", "pip-services-dummies:service:default:default:1.0"
        );

        lambda = new DummyCommandableLambdaFunction();
        lambda.configure(config);
        lambda.open(null);
    }

    @After
    public void close() throws Exception {
        lambda.close(null);
    }

    @Test
    public void testCrudOperations() throws Exception {
        // Create one dummy
        Dummy dummy1 = (Dummy) lambda.act(
                Parameters.fromTuples("cmd", "create_dummy", "dummy", DUMMY1)
        );

        assertNotNull(dummy1);
        assertNotNull(dummy1.getId());
        assertEquals(DUMMY1.getKey(), dummy1.getKey());
        assertEquals(DUMMY1.getContent(), dummy1.getContent());

        // Create another dummy
        Dummy dummy2 = (Dummy) lambda.act(
                Parameters.fromTuples("cmd", "create_dummy", "dummy", DUMMY2)
        );

        assertNotNull(dummy2);
        assertNotNull(dummy2.getId());
        assertEquals(DUMMY2.getKey(), dummy2.getKey());
        assertEquals(DUMMY2.getContent(), dummy2.getContent());

        // Get all dummies
        DataPage<Dummy> dummies = (DataPage<Dummy>) lambda.act(Parameters.fromTuples("cmd", "get_dummies"));
        assertNotNull(dummies);
        assertEquals(2, dummies.getData().size());

        // Update the dummy
        dummy1.setContent("Updated Content 1");
        Dummy dummy = (Dummy) lambda.act(
                Parameters.fromTuples("cmd", "update_dummy", "dummy", dummy1)
        );

        assertNotNull(dummy);
        assertEquals(dummy1.getId(), dummy.getId());
        assertEquals(dummy1.getKey(), dummy.getKey());
        assertEquals("Updated Content 1", dummy.getContent());

        dummy1 = dummy;

        assertNotNull(dummy);
        assertEquals(dummy.getId(), dummy1.getId());
        assertEquals(dummy.getKey(), dummy1.getKey());

        // Delete the dummy
        lambda.act(
                Parameters.fromTuples("cmd", "delete_dummy", "dummy_id", dummy1.getId())
        );

        // Try to get deleted dummy
        dummy = (Dummy) lambda.act(
                Parameters.fromTuples("cmd", "get_dummy_by_id", "dummy_id", dummy1.getId())
        );
        assertNull(dummy);
    }
}
