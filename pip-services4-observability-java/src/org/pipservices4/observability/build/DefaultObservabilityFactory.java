package org.pipservices4.observability.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.observability.count.*;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.log.ConsoleLogger;
import org.pipservices4.observability.log.NullLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.observability.trace.LogTracer;
import org.pipservices4.observability.trace.NullTracer;

/**
 * Creates observability components by their descriptors.
 *
 * @see Factory
 * @see NullCounters
 * @see LogCounters
 * @see CompositeCounters
 */
public class DefaultObservabilityFactory extends Factory {
    private static final Descriptor LogCountersDescriptor = new Descriptor("pip-services", "counters", "log", "*",
            "1.0");
    private static final Descriptor CompositeCountersDescriptor = new Descriptor("pip-services", "counters", "composite",
            "*", "1.0");
    private static final Descriptor NullCountersDescriptor = new Descriptor("pip-services", "counters", "null", "*",
            "1.0");
    private static final Descriptor ConsoleLoggerDescriptor = new Descriptor("pip-services", "logger", "console", "*",
            "1.0");
    private static final Descriptor CompositeLoggerDescriptor = new Descriptor("pip-services", "logger", "composite",
            "*", "1.0");
    private static final Descriptor NullLoggerDescriptor = new Descriptor("pip-services", "logger", "null", "*", "1.0");
    private static final Descriptor NullTracerDescriptor = new Descriptor("pip-services", "tracer", "null", "*", "1.0");
    private static final Descriptor LogTracerDescriptor = new Descriptor("pip-services", "tracer", "log", "*", "1.0");
    private static final Descriptor CompositeTracerDescriptor = new Descriptor("pip-services", "tracer", "composite", "*", "1.0");    

    /**
     * Create a new instance of the factory.
     */
    public DefaultObservabilityFactory() {
        registerAsType(DefaultObservabilityFactory.NullCountersDescriptor, NullCounters.class);
        registerAsType(DefaultObservabilityFactory.LogCountersDescriptor, LogCounters.class);
        registerAsType(DefaultObservabilityFactory.CompositeCountersDescriptor, CompositeCounters.class);
        registerAsType(DefaultObservabilityFactory.NullLoggerDescriptor, NullLogger.class);
        registerAsType(DefaultObservabilityFactory.ConsoleLoggerDescriptor, ConsoleLogger.class);
        registerAsType(DefaultObservabilityFactory.CompositeLoggerDescriptor, CompositeLogger.class);
        registerAsType(DefaultObservabilityFactory.NullTracerDescriptor, NullTracer.class);
        registerAsType(DefaultObservabilityFactory.LogTracerDescriptor, LogTracer.class);
        registerAsType(DefaultObservabilityFactory.CompositeTracerDescriptor, CompositeTracer.class);
    }
}
