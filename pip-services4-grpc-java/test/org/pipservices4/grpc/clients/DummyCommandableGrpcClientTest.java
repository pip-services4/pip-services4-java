package org.pipservices4.grpc.clients;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.grpc.sample.DummyService;
import org.pipservices4.grpc.controllers.DummyCommandableGrpcController;
import org.pipservices4.grpc.controllers.DummyGrpcController;

public class DummyCommandableGrpcClientTest {
    private static final ConfigParams grpcConfig = ConfigParams.fromTuples(
            "connection.protocol", "http",
            "connection.host", "localhost",
            "connection.port", 3002
    );

    static DummyCommandableGrpcController controller;
    DummyCommandableGrpcClient client;
    DummyClientFixture fixture;

    @BeforeClass
    public static void setupClass() throws ApplicationException {
        var service = new DummyService();

        controller = new DummyCommandableGrpcController();
        controller.configure(grpcConfig);

        var references = References.fromTuples(
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
    public void setup() throws ApplicationException {
        client = new DummyCommandableGrpcClient();
        fixture = new DummyClientFixture(client);

        client.configure(grpcConfig);
        client.setReferences(new References());
        client.open(null);
    }

    @Test
    public void testCrudOperations() {
        fixture.testCrudOperations();
    }
}
