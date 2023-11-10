package org.pipservices4.observability.log;

import org.junit.*;

public class ConsoleLoggerTest {
    private LoggerFixture fixture;

    @Before
    public void setUp() throws Exception {
        ILogger log = new ConsoleLogger();
        fixture = new LoggerFixture(log);
    }

    @Test
    public void testLogLevel() {
        fixture.testLogLevel();
    }

    @Test
    public void testTextOutput() {
        fixture.testTextOutput();
    }
}
