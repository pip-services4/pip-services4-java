package org.pipservices4.http.controllers;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.ReferenceException;

public class DummyCommandableHttpController extends CommandableHttpController {

    public DummyCommandableHttpController() {
        super("dummy");
        _dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "1.0"));
    }

    @Override
    public void register() throws ReferenceException {
        if (!this._swaggerAuto && this._swaggerEnable)
            this.registerOpenApiSpec("swagger yaml content");

        super.register();
    }

}
