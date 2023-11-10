package org.pipservices4.observability.count;

import org.junit.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.*;
import org.pipservices4.observability.log.ConsoleLogger;

public class LogCountersTest {
    private LogCounters counters;
    private CountersFixture fixture;
    
	@Before
	public void setUp() throws ReferenceException {
        ConsoleLogger log = new ConsoleLogger();
        References refs = References.fromTuples(log);

        counters = new LogCounters();
        counters.setReferences(refs);
        
        fixture = new CountersFixture(counters);
    }

    @Test
    public void testSimpleCounters() throws InvocationException {
        fixture.testSimpleCounters();
    }

    @Test
    public void TestMeasureElapsedTime() throws InvocationException {
        fixture.testMeasureElapsedTime();
    }
}
