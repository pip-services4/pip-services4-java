package org.pipservices4.observability.log;

import org.pipservices4.components.context.Context;

import static org.junit.Assert.*;

public class LoggerFixture {
    private final ILogger _logger;

    public LoggerFixture(ILogger logger) {
        _logger = logger;
    }

    public void testLogLevel() {
        assertTrue(LogLevelConverter.toInteger(_logger.getLevel()) >= LogLevelConverter.toInteger(LogLevel.None));
        assertTrue(LogLevelConverter.toInteger(_logger.getLevel()) <= LogLevelConverter.toInteger(LogLevel.Trace));
    }

    public void testTextOutput() {
        _logger.log(LogLevel.Fatal, Context.fromTraceId("123"), null, "Fatal error...");
        _logger.log(LogLevel.Error, Context.fromTraceId("123"), new Exception("Test Exception"), "Recoverable error...");
        _logger.log(LogLevel.Warn, Context.fromTraceId("123"), null, "Warning...");
        _logger.log(LogLevel.Info, Context.fromTraceId("123"), null, "Information message...");
        _logger.log(LogLevel.Debug, Context.fromTraceId("123"), null, "Debug message...");
        _logger.log(LogLevel.Trace, Context.fromTraceId("123"), null, "Trace message...");
    }

    public void testErrorLogging() {
        try {
            // Raise an exception
            throw new Exception();
        } catch (Exception ex) {
            _logger.log(LogLevel.Fatal, Context.fromTraceId("123"), ex, "Fatal error...");
            _logger.log(LogLevel.Error, Context.fromTraceId("123"), ex, "Recoverable error...");
        }

    }

}