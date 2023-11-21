package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class PipSecurityContext implements SecurityContext {
    private UserPrincipal _user;
    private Object _session;
    private ContainerRequestContext _requestContext;

    public PipSecurityContext(UserPrincipal user, ContainerRequestContext requestContext) {
        _user = user;
        _requestContext = requestContext;
    }

    public PipSecurityContext(UserPrincipal user, Object session, ContainerRequestContext requestContext) {
        _user = user;
        _session = session;
        _requestContext = requestContext;
    }

    @Override
    public Principal getUserPrincipal() {
        return _user;
    }

    @Override
    public boolean isUserInRole(String role) {
        List<String> roles = _user != null ? _user.getProperty("roles") != null ?
                (List<String>) _user.getProperty("roles") : new ArrayList<>() : new ArrayList<>();
        return !roles.isEmpty() && roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return _requestContext.getSecurityContext().isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return _requestContext.getSecurityContext().getAuthenticationScheme();
    }

    public Object getSession() {
        return _session;
    }

    public void setSession(Object session) {
        _session = session;
    }
}
