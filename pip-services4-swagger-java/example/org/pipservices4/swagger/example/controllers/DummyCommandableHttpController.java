package org.pipservices4.swagger.example.controllers;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.http.controllers.CommandableHttpController;

public class DummyCommandableHttpController extends CommandableHttpController {

    /**
     * Creates a new instance of the service.
     */
    public DummyCommandableHttpController() {
        super("dummies2");
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
    }

    @Override
    public void register() throws ReferenceException {
        // if (!this._swaggerAuto && this._swaggerEnabled) {
        //     this.registerOpenApiSpec("swagger yaml content");
        // }

        super.register();
    }
}
