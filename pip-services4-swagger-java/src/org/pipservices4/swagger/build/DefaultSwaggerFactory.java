package org.pipservices4.swagger.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.swagger.controllers.SwaggerController;

/**
 * Creates Swagger components by their descriptors.
 *
 * @see org.pipservices4.components.build.Factory
 * @see org.pipservices4.http.controllers.HeartbeatRestController
 * @see org.pipservices4.http.controllers.HttpEndpoint
 * @see org.pipservices4.http.controllers.StatusRestController
 */
public class DefaultSwaggerFactory extends Factory {
    private static final Descriptor SwaggerControllerDescriptor = new Descriptor("pip-services", "swagger-controller", "*", "*", "1.0");

    public DefaultSwaggerFactory() {
        super();
        this.registerAsType(DefaultSwaggerFactory.SwaggerControllerDescriptor, SwaggerController.class);
    }
}
