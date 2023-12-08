package org.pipservices4.aws.count;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InvocationException;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;

public class CloudWatchCountersTest {
    CloudWatchCounters _counters;
    CountersFixture _fixture;

    @Before
    public void setup() throws ApplicationException {
        var region = System.getenv("AWS_REGION") != null ? System.getenv("AWS_REGION") : "";
        var awsAccessId = System.getenv("AWS_ACCESS_ID") != null ? System.getenv("AWS_ACCESS_ID") : "";
        var awsAccessKey = System.getenv("AWS_ACCESS_KEY") != null ? System.getenv("AWS_ACCESS_KEY") : "";

        _counters = new CloudWatchCounters();
        _fixture = new CountersFixture(_counters);

        var config = ConfigParams.fromTuples(
                "interval", "5000",
                "connection.region", region,
                "credential.access_id", awsAccessId,
                "credential.access_key", awsAccessKey
        );
        _counters.configure(config);
        var contextInfo = new ContextInfo();
        contextInfo.setName("Test");
        contextInfo.setDescription("This is a test container");

        _counters.setReferences(References.fromTuples(
                new Descriptor("pip-services", "context-info", "default", "default", "1.0"), contextInfo,
                new Descriptor("pip-services", "counters", "cloudwatch", "default", "1.0"), _counters
        ));

        _counters.open(null);
    }

    @After
    public void teardown() throws ApplicationException {
        _counters.close(null);
    }

    @Test
    public void testSimpleCounters() throws InvocationException, InterruptedException {
        _fixture.testSimpleCounters();
    }

    @Test
    public void testMeasureElapsedTime() throws InvocationException, InterruptedException {
        _fixture.testMeasureElapsedTime();
    }
}
