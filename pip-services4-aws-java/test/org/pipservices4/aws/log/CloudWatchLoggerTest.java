package org.pipservices4.aws.log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InvocationException;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;

import static org.junit.Assert.assertTrue;

public class CloudWatchLoggerTest {
    CloudWatchLogger _logger;
    LoggerFixture _fixture;

    @Before
    public void setupClass() throws ApplicationException {
        var region = System.getenv("AWS_REGION");
        var awsAccessId = System.getenv("AWS_ACCESS_ID");
        var awsAccessKey = System.getenv("AWS_ACCESS_KEY");

        if (awsAccessId == null || awsAccessKey == null || region == null) {
            return;
        }

        _logger = new CloudWatchLogger();
        _fixture = new LoggerFixture(_logger);

        var config = ConfigParams.fromTuples(
                "group", "TestGroup",
                "connection.region", region,
                "credential.access_id", awsAccessId,
                "credential.access_key", awsAccessKey
        );
        _logger.configure(config);
        var contextInfo = new ContextInfo();
        contextInfo.setName("TestStream");

        _logger.setReferences(References.fromTuples(
                new Descriptor("pip-services", "context-info", "default", "default", "1.0"), contextInfo,
                new Descriptor("pip-services", "logger", "cloudwatch", "default", "1.0"), _logger
        ));

        _logger.open(null);
    }

    @After
    public void teardown() throws InvocationException {
        _logger.close(null);
    }

    @Test
    public void testLogLevel() {
        _fixture.testLogLevel();
    }

    @Test
    public void testSimpleLogging() throws InterruptedException {
        _fixture.testSimpleLogging();
    }

    @Test
    public void testErrorLogging() throws InterruptedException {
        _fixture.testErrorLogging();
    }
}
