package org.pipservices4.http.controllers;

/**
 * Interface to perform Swagger registrations.
 */
public interface ISwaggerController {
    /**
     * Perform required Swagger registration steps.
     */
    void registerOpenApiSpec(String baseRoute, String swaggerRoute);
}
