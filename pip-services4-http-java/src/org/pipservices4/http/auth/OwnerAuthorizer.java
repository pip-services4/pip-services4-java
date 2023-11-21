package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.commons.errors.UnauthorizedException;
import org.pipservices4.http.controllers.AuthorizeFunction;
import org.pipservices4.http.controllers.HttpResponseSender;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OwnerAuthorizer {
    public AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> owner(
            String idParam) {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            if (req.getSecurityContext().getUserPrincipal() == null) {
                return HttpResponseSender.sendError(
                        new UnauthorizedException(
                                null,
                                "NOT_SIGNED",
                                "User must be signed in to perform this operation").withStatus(401));
            } else {
                String userId = this.getQueryParameter(req, idParam);
                if (req.getProperty("user_id") != userId) {
                    return HttpResponseSender.sendError(
                            new UnauthorizedException(
                                    null,
                                    "FORBIDDEN",
                                    "Only data owner can perform this operation").withStatus(403));
                } else {
                    return nextFunc.apply(req);
                }
            }
        };
    }

    public AuthorizeFunction<ContainerRequestContext, Inflector<ContainerRequestContext, Response>, Response> ownerOrAdmin(
            String idParam) {
        return (ContainerRequestContext req, Inflector<ContainerRequestContext, Response> nextFunc) -> {
            if (req.getSecurityContext().getUserPrincipal() == null) {
                return HttpResponseSender.sendError(
                        new UnauthorizedException(
                                null,
                                "NOT_SIGNED",
                                "User must be signed in to perform this operation").withStatus(401));
            } else {
                String userId = this.getQueryParameter(req, idParam);
                boolean isAdmin = req.getSecurityContext().isUserInRole("admin");
                if (req.getProperty("user_id") != userId && !isAdmin) {
                    return HttpResponseSender.sendError(
                            new UnauthorizedException(
                                    null,
                                    "FORBIDDEN",
                                    "Only data owner can perform this operation").withStatus(403));
                } else {
                    return nextFunc.apply(req);
                }
            }
        };
    }

    private String getQueryParameter(ContainerRequestContext request, String name) {
        String value = null;
        name = URLEncoder.encode(name, StandardCharsets.UTF_8);
        if (request.getUriInfo().getQueryParameters().containsKey(name)) {
            value = request.getUriInfo().getQueryParameters().getFirst(name);
            value = value != null ? URLDecoder.decode(value, StandardCharsets.UTF_8) : null;
        }

        return value;
    }
}
