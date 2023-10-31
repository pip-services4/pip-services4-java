package org.pipservices4.commons.reflect;

import java.util.List;

public interface IProperties {

    List<String> getPropertyNames();

    Object getProperty(String name);

    void setProperty(String name, Object value);
}
