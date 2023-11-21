package org.pipservices4.http.auth;

import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.List;

public class PipSecurityContext implements SecurityContext {
    private UserPrincipal _user;
    private List<String> _roles;
    private String _authType;

    private boolean _isSecure = false;

    public PipSecurityContext(UserPrincipal user, List<String> roles) {
        _user = user;
        _roles = roles;
        _authType = SecurityContext.BASIC_AUTH;
    }

    public PipSecurityContext(UserPrincipal user, List<String> roles, String authType, boolean isSecure) {
        _user = user;
        _roles = roles;
        _authType = authType;
        _isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        return _user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return !_roles.isEmpty() && _roles.contains(role);
    }

    @Override
    public boolean isSecure() {
        return _isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return _authType;
    }
}
