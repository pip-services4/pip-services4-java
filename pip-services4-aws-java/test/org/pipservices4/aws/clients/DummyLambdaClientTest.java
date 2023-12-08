package org.pipservices4.aws.clients;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pipservices4.aws.DummyClientFixture;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;

public class DummyLambdaClientTest {
    static DummyLambdaClient client;
    static DummyClientFixture fixture;


    @BeforeClass
    public static void setupClass() throws ApplicationException {
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

        client = new DummyLambdaClient();
        client.configure(lambdaConfig);

        fixture = new DummyClientFixture(client);

        client.open(null);

    }

    @AfterClass
    public static void teardown() throws ApplicationException {
        client.close(null);
    }

    @Test
    public void testCrudOperations() throws ApplicationException {
        fixture.testCrudOperations();
    }
}
