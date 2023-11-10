package org.pipservices4.observability.trace;

import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.refer.References;
import org.pipservices4.observability.log.NullLogger;

public class LogTracerTest {
    private LogTracer _tracer;

    @Before
    public void setup() throws ReferenceException {
        _tracer = new LogTracer();
        _tracer.setReferences(References.fromTuples(
                new Descriptor("pip-services", "logger", "null", "default", "1.0"), new NullLogger()
        ));
    }

    @Test
    public void testSimpleTracing() {
        _tracer.trace(Context.fromTraceId("123"), "mycomponent", "mymethod", 123456L);
        _tracer.failure(Context.fromTraceId("123"), "mycomponent", "mymethod", new Exception("Test error"), 123456);
    }

    @Test
    public void testTraceTiming() {
        TraceTiming timing = _tracer.beginTrace(Context.fromTraceId("123"), "mycomponent", "mymethod");
        timing.endTrace();

        timing = _tracer.beginTrace(Context.fromTraceId("123"), "mycomponent", "mymethod");
        timing.endFailure(new Exception("Test error"));
    }
}
