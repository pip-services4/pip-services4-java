package org.pipservices4.http.auth;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class UserPrincipal implements Principal {
    private String _userName;
    private String _userId;
    private Map<String, Object> _props;


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

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public Map<String, Object> getProps() {
        return _props;
    }

    public void setProps(Map<String, Object> props) {
        if (props != null && !props.keySet().isEmpty()) {
            props.keySet().forEach(k -> this.setProperty(k, props.get(k)));
        }
    }

    public void setProps(Map<String, Object> props, boolean replaceAll) {
        if (props != null && !props.keySet().isEmpty()) {
            if (replaceAll) {
                _props = new HashMap<>(props);
            } else props.keySet().forEach(k -> this.setProperty(k, props.get(k)));
        }
    }

    public Object getProperty(String key) {
        if (_props == null || !_props.containsKey(key)) {
            return null;
        }
        return _props.get(key);
    }

    public void setProperty(String key, Object value) {
        if (_props == null) {
            _props = new HashMap<>();
        }
        if (_props.containsKey(key)) {
            _props.replace(key, value);
        } else {
            _props.put(key, value);
        }
    }

    public void removeProperty(String key) {
        if (_props != null) {
            _props.remove(key);
        }
    }

    public void clearProps() {
        if (_props == null) {
            _props = new HashMap<>();
        } else _props.clear();
    }
}
