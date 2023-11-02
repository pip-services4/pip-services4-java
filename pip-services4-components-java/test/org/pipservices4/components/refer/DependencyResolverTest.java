package org.pipservices4.components.refer;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.components.config.*;

public class DependencyResolverTest {

    @Test
    public void testDependencies() throws Exception {
        Object ref1 = new Object();
        Object ref2 = new Object();
        IReferences refs = References.fromTuples(
                "Reference1", ref1,
                new Descriptor("pip-services-commons", "reference", "object", "ref2", "1.0"), ref2
        );

        DependencyResolver resolver = DependencyResolver.fromTuples(
                "ref1", "Reference1",
                "ref2", new Descriptor("pip-services-commons", "reference", "*", "*", "*")
        );
        resolver.setReferences(refs);

        assertEquals(ref1, resolver.getOneRequired("ref1"));
        assertEquals(ref2, resolver.getOneRequired("ref2"));
        assertNull(resolver.getOneOptional("ref3"));
    }

    @Test
    public void testDependenciesConfig() throws Exception {
        Object ref1 = new Object();
        Object ref2 = new Object();
        IReferences refs = References.fromTuples(
                "Reference1", ref1,
                new Descriptor("pip-services-commons", "reference", "object", "ref2", "1.0"), ref2
        );

        ConfigParams config = ConfigParams.fromTuples(
                "dependencies.ref1", "Reference1",
                "dependencies.ref2", "pip-services-commons:reference:*:*:*",
                "dependencies.ref3", null
        );

        DependencyResolver resolver = new DependencyResolver(config);
        resolver.setReferences(refs);

        assertEquals(ref1, resolver.getOneRequired("ref1"));
        assertEquals(ref2, resolver.getOneRequired("ref2"));
        assertNull(resolver.getOneOptional("ref3"));
    }

}
