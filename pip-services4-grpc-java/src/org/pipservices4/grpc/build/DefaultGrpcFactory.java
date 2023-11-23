package org.pipservices4.grpc.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;

import org.pipservices4.grpc.controllers.GrpcEndpoint;

/**
 * Creates GRPC components by their descriptors.
 *
 * @see Factory
 * @see GrpcEndpoint
 */
public class DefaultGrpcFactory extends Factory {
    private static final Descriptor GrpcEndpointDescriptor = new Descriptor("pip-services", "endpoint", "grpc", "*", "1.0");

    public DefaultGrpcFactory() {
        super();
        this.registerAsType(DefaultGrpcFactory.GrpcEndpointDescriptor, GrpcEndpoint.class);
    }
}
