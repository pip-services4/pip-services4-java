package org.pipservices4.http.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.ArrayList;
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



    // Example of creating security context in facade
    public void loadSession(ContainerRequestContext req) {
        class UserObj { // obj from pip lib
            List<String> Roles;
        }
        class Session { // obj from pip lib
            String UserId;
            String UserName;
            UserObj User;
            String Id;
        }

        // Get somewhere session
        var session = new Session();

        req.setProperty("user_id", session.UserId);
        req.setProperty("user_name", session.UserName);
        req.setProperty("user", session.User);
        req.setProperty("session_id", session.Id);

        var origCtx = req.getSecurityContext();
        var roles = session.User.Roles;

        var secCtx = new PipSecurityContext(
                new UserPrincipal("username"),
                roles,
                origCtx.getAuthenticationScheme(),
                origCtx.isSecure()
        );

        req.setSecurityContext(secCtx);

        // next processing methods
    }
}
