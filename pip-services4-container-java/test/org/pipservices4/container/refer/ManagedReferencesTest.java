package org.pipservices4.container.refer;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.observability.build.DefaultObservabilityFactory;
import org.pipservices4.observability.log.*;
import org.pipservices4.components.refer.*;

public class ManagedReferencesTest {
    @Test
    public void testAutoCreateComponent() throws ApplicationException {
    	ManagedReferences refs = new ManagedReferences();

        DefaultObservabilityFactory factory = new DefaultObservabilityFactory();
        refs.put(null, factory);

        ILogger logger = refs.getOneRequired(ILogger.class, new Descriptor("*", "logger", "*", "*", "*"));
        assertNotNull(logger);
    }

    @Test
    public void testStringLocator() throws ApplicationException {
    	ManagedReferences refs = new ManagedReferences();

        DefaultObservabilityFactory factory = new DefaultObservabilityFactory();
        refs.put(new Object(), factory);

        Object component = refs.getOneOptional("ABC");
        assertNull(component);
    }

    @Test
    public void testNullLocator() throws ApplicationException {
    	ManagedReferences refs = new ManagedReferences();

        DefaultObservabilityFactory factory = new DefaultObservabilityFactory();
        refs.put(new Object(), factory);

        Object component = refs.getOneOptional(null);
        assertNull(component);
    }
}
