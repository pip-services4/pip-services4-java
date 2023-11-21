package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.commons.errors.UnauthorizedException;
import org.pipservices4.http.controllers.AuthorizeFunction;
import org.pipservices4.http.controllers.HttpResponseSender;

public class BasicAuthorizer {
    public AuthorizeFunction<ContainerRequestContext,
            Inflector<ContainerRequestContext, Response>, Response> anybody() {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            return nextFunc.apply(req);
        };
    }

    public AuthorizeFunction<ContainerRequestContext,
            Inflector<ContainerRequestContext, Response>, Response> signed() {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            if (req.getSecurityContext().getUserPrincipal() == null) {
                return HttpResponseSender.sendError(
                        new UnauthorizedException(
                                null,
                                "NOT_SIGNED",
                                "User must be signed in to perform this operation"
                        ).withStatus(401)
                );
            } else {
                return nextFunc.apply(req);
            }
        };
    }
}
