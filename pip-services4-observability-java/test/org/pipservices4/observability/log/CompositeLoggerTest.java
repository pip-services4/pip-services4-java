package org.pipservices4.observability.log;

import org.junit.Test;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.refer.References;

public class CompositeLoggerTest {

    private CompositeLogger _log;
    private LoggerFixture _fixture;

    public CompositeLogger getLog() {
        return _log;
    }

    public void setLog(CompositeLogger _log) {
        this._log = _log;
    }

    public LoggerFixture getFixture() {
        return _fixture;
    }

    public void setFixture(LoggerFixture _fixture) {
        this._fixture = _fixture;
    }

    public CompositeLoggerTest() throws ReferenceException {
        _log = new CompositeLogger();

        References refs;

        refs = References.fromTuples(
                new Descriptor("pip-services", "logger", "console", "default", "1.0"), new ConsoleLogger(),
                new Descriptor("pip-services", "logger", "null", "default", "1.0"), new NullLogger()
        );


        _log.setReferences(refs);

        _fixture = new LoggerFixture(_log);

    }

    @Test
    public void testLogLevel() {
        _fixture.testLogLevel();
    }

    @Test
    public void testSimpleLogging() {
        _fixture.testTextOutput();
    }

    @Test
    public void testErrorLogging() {
        _fixture.testErrorLogging();
    }

}
