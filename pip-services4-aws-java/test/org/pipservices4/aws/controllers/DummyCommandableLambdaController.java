package org.pipservices4.aws.controllers;

import org.pipservices4.components.refer.Descriptor;

public class DummyCommandableLambdaController extends CommandableLambdaController {

    public DummyCommandableLambdaController() {
        super("dummies");
        _dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "1.0"));
    }
}
