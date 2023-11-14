package org.pipservices4.container.build;


import org.pipservices4.components.build.CompositeFactory;
import org.pipservices4.components.build.IFactory;
import org.pipservices4.components.context.DefaultContextFactory;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.config.build.DefaultConfigFactory;
import org.pipservices4.container.test.DefaultTestFactory;
import org.pipservices4.logic.build.DefaultLogicFactory;
import org.pipservices4.observability.build.DefaultObservabilityFactory;

/**
 * Creates default container components (loggers, counters, caches, locks, etc.) by their descriptors.
 *
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/build/Factory.html">Factory</a>
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/context/DefaultContextFactory.html">DefaultContextFactory</a>
 * @see <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/build/DefaultObservabilityFactory.html">DefaultObservabilityFactory</a>
 * @see <a href="https://pip-services4-java.github.io/pip-services4-logic-java/org/pipservices4/logic/build/DefaultLogicFactory.html">DefaultLogicFactory</a>
 * @see <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/build/DefaultConfigFactory.html">DefaultConfigFactory</a>
 * @see <a href="https://pip-services4-java.github.io/pip-services4-container-java/org/pipservices4/container/test/DefaultTestFactory.html">DefaultTestFactory</a>
 */
public class DefaultContainerFactory extends CompositeFactory {
    public final static Descriptor Descriptor = new Descriptor("pip-services", "factory", "container", "default",
            "1.0");

    /**
     * Create a new instance of the factory and sets nested factories.
     *
     * @param factories a list of nested factories
     */
    public DefaultContainerFactory(IFactory... factories) {
        super(factories);

        add(new DefaultContextFactory());
        add(new DefaultObservabilityFactory());
        add(new DefaultLogicFactory());
        add(new DefaultConfigFactory());
        add(new DefaultTestFactory());
    }
}
