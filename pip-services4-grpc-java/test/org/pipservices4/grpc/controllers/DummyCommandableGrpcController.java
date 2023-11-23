package org.pipservices4.grpc.controllers;

import org.pipservices4.components.refer.Descriptor;

public class DummyCommandableGrpcController extends CommandableGrpcController {

    public DummyCommandableGrpcController() {
        super("dummy");
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
    }
}
