package org.pipservices4.observability.count;

import static org.junit.Assert.*;

import org.pipservices4.commons.errors.*;

public class CountersFixture {
    private CachedCounters _counters;

    public CountersFixture(CachedCounters counters) {
        _counters = counters;
    }

    public void testSimpleCounters() throws InvocationException {
        _counters.last("Test.LastValue", 123);
        _counters.last("Test.LastValue", 123456);

        Counter counter = _counters.get("Test.LastValue", CounterType.LastValue);
        assertNotNull(counter);
        assertTrue(Math.abs(counter.getLast() - 123456) < 0.001);

        _counters.incrementOne("Test.Increment");
        _counters.increment("Test.Increment", 3);

        counter = _counters.get("Test.Increment", CounterType.Increment);
        assertNotNull(counter);
        assertEquals((int) counter.getCount(), 4);

        _counters.timestampNow("Test.Timestamp");
        _counters.timestampNow("Test.Timestamp");

        counter = _counters.get("Test.Timestamp", CounterType.Timestamp);
        assertNotNull(counter);
        assertNotNull(counter.getTime());

        _counters.stats("Test.Statistics", 1);
        _counters.stats("Test.Statistics", 2);
        _counters.stats("Test.Statistics", 3);

        counter = _counters.get("Test.Statistics", CounterType.Statistics);
        assertNotNull(counter);
        assertTrue(Math.abs(counter.getAverage() - 2) < 0.001);

        _counters.dump();
    }

    public void testMeasureElapsedTime() throws InvocationException {
        CounterTiming timing = _counters.beginTiming("Test.Elapsed");
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // Do nothing...
        } finally {
            timing.endTiming();
        }

        Counter counter = _counters.get("Test.Elapsed", CounterType.Interval);
        assertNotNull(counter);
        assertTrue(counter.getLast() > 50);
        assertTrue(counter.getLast() < 5000);

        _counters.dump();
    }
}
