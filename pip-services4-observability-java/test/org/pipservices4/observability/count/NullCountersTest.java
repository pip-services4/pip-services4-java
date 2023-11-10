package org.pipservices4.observability.count;

import org.junit.*;

public class NullCountersTest {
    private ICounters counters;

	@Before
	public void setUp() {
        counters = new NullCounters();
    }

    @Test
    public void testSimpleCounters() {
        counters.last("Test.LastValue", 123);
        counters.increment("Test.Increment", 3);
        counters.stats("Test.Statistics", 123);
    }

    @Test
    public void TestMeasureElapsedTime() {
        CounterTiming timer = counters.beginTiming("Test.Elapsed");
        timer.endTiming();
    }
}
