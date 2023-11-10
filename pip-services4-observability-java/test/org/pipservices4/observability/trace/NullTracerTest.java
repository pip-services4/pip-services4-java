package org.pipservices4.observability.trace;

import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.refer.ReferenceException;

public class NullTracerTest {
    private NullTracer _tracer;

    @Before
    public void setup() throws ReferenceException {
        _tracer = new NullTracer();
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
