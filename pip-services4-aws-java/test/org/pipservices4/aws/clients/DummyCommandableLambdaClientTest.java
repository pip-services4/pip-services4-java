package org.pipservices4.aws.clients;

import org.junit.*;

import static org.junit.Assert.*;

import org.pipservices4.aws.DummyClientFixture;
import org.pipservices4.components.config.*;
import org.pipservices4.commons.errors.*;

public class DummyCommandableLambdaClientTest {

    private DummyCommandableLambdaClient _client;
    private DummyClientFixture _fixture;

    @Before
    public void setUpClass() throws Exception {
        var lambdaArn = System.getenv("LAMBDA_ARN");
        var awsAccessId = System.getenv("AWS_ACCESS_ID");
        var awsAccessKey = System.getenv("AWS_ACCESS_KEY");

        if (awsAccessId == null || awsAccessKey == null || lambdaArn == null) {
            return;
        }

        ConfigParams lambdaConfig = ConfigParams.fromTuples(
                "connection.protocol", "aws",
                "connection.arn", lambdaArn,
                "credential.access_id", awsAccessId,
                "credential.access_key", awsAccessKey,
                "options.connection_timeout", 30000
        );

        _client = new DummyCommandableLambdaClient();

        _client.configure(lambdaConfig);
        _client.open(null);

        _fixture = new DummyClientFixture(_client);
    }

    @After
    public void tearDown() throws Exception {
        _client.close(null);
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        _fixture.testCrudOperations();
    }

}
