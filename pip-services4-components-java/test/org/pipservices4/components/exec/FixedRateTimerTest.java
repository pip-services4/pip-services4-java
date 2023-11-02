package org.pipservices4.components.exec;

import org.junit.Test;
import org.pipservices4.components.context.IContext;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class FixedRateTimerTest {
    @Test
    public void tesRunWithTask() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();

        FixedRateTimer timer = new FixedRateTimer(
                (IContext context, Parameters args) -> counter.getAndIncrement(),
                100, 0
        );

        timer.start();

        Thread.sleep(500);

        assertTrue(counter.get() > 3);
    }
}
