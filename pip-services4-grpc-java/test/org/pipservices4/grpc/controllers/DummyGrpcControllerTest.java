package org.pipservices4.grpc.controllers;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.*;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.grpc.sample.Dummy;
import org.pipservices4.grpc.sample.DummyService;
import org.pipservices4.grpc.dummies.DummiesGrpc;
import org.pipservices4.grpc.dummies.DummiesPageRequest;
import org.pipservices4.grpc.dummies.DummyIdRequest;
import org.pipservices4.grpc.dummies.DummyObjectRequest;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DummyGrpcControllerTest {

    private static final ConfigParams grpcConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", 3000
    );

    Dummy _dummy1;
    Dummy _dummy2;

    ManagedChannel _channel;

    static DummyGrpcController controller;

    DummiesGrpc.DummiesBlockingStub client;

    @BeforeClass
    public static void setupClass() throws ApplicationException {
        var service = new DummyService();

        controller = new DummyGrpcController();
        controller.configure(grpcConfig);

        References references = References.fromTuples(
                new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                new Descriptor("pip-services-dummies", "controller", "grpc", "default", "1.0"), controller
        );
        controller.setReferences(references);

        controller.open(null);
    }

    @AfterClass
    public static void teardownClass() throws InvalidStateException {
        controller.close(null);
    }

    @Before
    public void setup() {
        _channel = ManagedChannelBuilder.forTarget("localhost:3000")
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        client = DummiesGrpc.newBlockingStub(_channel);

        _dummy1 = new Dummy(null, "Key 1", "Content 1");
        _dummy2 = new Dummy(null, "Key 1", "Content 1");
    }

    @After
    public void teardown() throws InterruptedException {
        _channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testCrudOperations() {
        // Create one dummy
        var dummy = org.pipservices4.grpc.dummies.Dummy.newBuilder()
                .setKey(_dummy1.getKey())
                .setContent(_dummy1.getContent())
                .build();

        var request = DummyObjectRequest.newBuilder().setDummy(dummy).build();

        dummy = client.createDummy(request);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy1.getContent());
        assertEquals(dummy.getKey(), _dummy1.getKey());

        var dummy1 = new Dummy(dummy.getId(), dummy.getKey(), dummy.getContent());

        // Create another dummy
        dummy = org.pipservices4.grpc.dummies.Dummy.newBuilder()
                .setKey(_dummy2.getKey())
                .setContent(_dummy2.getContent())
                .build();

        request = DummyObjectRequest.newBuilder().setDummy(dummy).build();

        dummy = client.createDummy(request);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), _dummy2.getContent());
        assertEquals(dummy.getKey(), _dummy2.getKey());

        // Get all dummies
        var pageRequest = DummiesPageRequest.newBuilder().build();

        var dummies = client.getDummies(pageRequest);

        assertNotNull(dummies);
        assertEquals(dummies.getDataCount(), 2);

        // Update the dummy
        dummy1.setContent("Updated Content 1");

        dummy = org.pipservices4.grpc.dummies.Dummy.newBuilder()
                .setId(dummy1.getId())
                .setKey(dummy1.getKey())
                .setContent(dummy1.getContent())
                .build();

        request = DummyObjectRequest.newBuilder().setDummy(dummy).build();

        dummy = client.updateDummy(request);

        assertNotNull(dummy);
        assertEquals(dummy.getContent(), "Updated Content 1");
        assertEquals(dummy.getKey(), _dummy1.getKey());

        dummy1 = new Dummy(dummy.getId(), dummy.getKey(), dummy.getContent());

        // Delete dummy
        var requestId = DummyIdRequest.newBuilder().setDummyId(dummy1.getId()).build();

        dummy = client.deleteDummyById(requestId);

        // Try to get delete dummy
        requestId = DummyIdRequest.newBuilder().setDummyId(dummy1.getId()).build();

        dummy = client.getDummyById(requestId);

        assertEquals("", dummy.toString());

        assertEquals(controller.getNumberOfCalls(), 6);
    }
}
