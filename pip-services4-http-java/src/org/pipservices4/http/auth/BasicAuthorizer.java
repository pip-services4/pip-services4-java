package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.http.controllers.AuthorizeFunction;

public class BasicAuthorizer {
    public AuthorizeFunction<ContainerRequestContext,
            Inflector<ContainerRequestContext, Response>, Response> anybody() {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            return nextFunc.apply(req);
        };
    }
}
