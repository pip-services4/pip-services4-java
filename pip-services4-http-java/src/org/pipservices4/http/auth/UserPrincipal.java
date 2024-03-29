package org.pipservices4.http.auth;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private String _userName;


    public UserPrincipal(String userName) {
        _userName = userName;
    }

    @Override
    public String getName() {
        return _userName;
    }

    public void setName(String userName) {
        _userName = userName;
    }
}
