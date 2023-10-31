package org.pipservices4.commons.reflect;

import org.junit.*;

import static org.junit.Assert.*;

public class TypeReflectorTest {
    @Test
    public void testGetType() {
        Class<?> type = TypeReflector.getType("org.pipservices4.commons.convert.TypeCode");
        assertNotNull(type);

        type = TypeReflector.getType("org.pipservices4.commons.convert.TypeCode", "pip-services4-commons");
        assertNotNull(type);
    }

    @Test
    public void testCreateInstance() throws Exception {
        Object value = TypeReflector.createInstance("org.pipservices4.commons.reflect.TestClass");
        assertNotNull(value);
    }
}
