package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.commons.errors.UnauthorizedException;
import org.pipservices4.http.controllers.AuthorizeFunction;
import org.pipservices4.http.controllers.HttpResponseSender;

import java.util.List;

public class RoleAuthorizer {
    public AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> userInRoles(
            List<String> roles) {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            if (req.getSecurityContext().getUserPrincipal() == null) {
                return HttpResponseSender.sendError(
                        new UnauthorizedException(
                                null,
                                "NOT_SIGNED",
                                "User must be signed in to perform this operation").withStatus(401));
            } else {
                boolean authorized = false;
                for (String role : roles) {
                    authorized = authorized || req.getSecurityContext().isUserInRole(role);
                }
                if (!authorized) {
                    return HttpResponseSender.sendError(
                            new UnauthorizedException(
                                    null,
                                    "NOT_IN_ROLE",
                                    "User must be " + String.join(" or ", roles) + " to perform this operation")
                                    .withDetails("roles", roles).withStatus(403));
                } else {
                    return nextFunc.apply(req);
                }
            }
        };
    }

    public AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> userInRole(
            String role) {
        return this.userInRoles(List.of(role));
    }

    public AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> admin() {
        return this.userInRole("admin");
    }
}
