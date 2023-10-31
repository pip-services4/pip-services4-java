package org.pipservices4.commons.reflect;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;

public class MethodReflectorTest {

    @Test
    public void testGetMethods() {
        TestClass obj = new TestClass();
        List<String> methods = MethodReflector.getMethodNames(obj);
        Object result = MethodReflector.invokeMethod(obj, "PUBLICMETHOD", 1, 2);

        assertEquals(3, result);
    }

    @Test
    public void testHasMethod() {
        TestClass obj = new TestClass();

        boolean result = MethodReflector.hasMethod(obj, "publicMethod");
        assertTrue(result);
    }
}
