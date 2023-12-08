package org.pipservices4.aws.connect;

import static org.junit.Assert.*;

import org.junit.*;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.config.ConfigParams;

public class AwsConnectionParamsTest {
    @Test
    public void testEmptyConnection() throws ApplicationException {
        var connection = new AwsConnectionParams();
        assertEquals("arn:aws::::", connection.getArn());
    }

    @Test
    public void parseArn() throws ApplicationException {
        var connection = new AwsConnectionParams();

        connection.setArn("arn:aws:lambda:us-east-1:12342342332:function:pip-services-dummies");
        assertEquals("lambda", connection.getService());
        assertEquals("us-east-1", connection.getRegion());
        assertEquals("12342342332", connection.getAccount());
        assertEquals("function", connection.getResourceType());
        assertEquals("pip-services-dummies", connection.getResource());

        connection.setArn("arn:aws:s3:us-east-1:12342342332:pip-services-dummies");
        assertEquals("s3", connection.getService());
        assertEquals("us-east-1", connection.getRegion());
        assertEquals("12342342332", connection.getAccount());
        assertNull(connection.getResourceType());
        assertEquals("pip-services-dummies", connection.getResource());

        connection.setArn("arn:aws:lambda:us-east-1:12342342332:function/pip-services-dummies");
        assertEquals("lambda", connection.getService());
        assertEquals("us-east-1", connection.getRegion());
        assertEquals("12342342332", connection.getAccount());
        assertEquals("function", connection.getResourceType());
        assertEquals("pip-services-dummies", connection.getResource());
    }

    @Test
    public void composeArn() throws ApplicationException {
        var connection = AwsConnectionParams.fromConfig(
                ConfigParams.fromTuples(
                        "connection.service", "lambda",
                        "connection.region", "us-east-1",
                        "connection.account", "12342342332",
                        "connection.resource_type", "function",
                        "connection.resource", "pip-services-dummies",
                        "credential.access_id", "1234",
                        "credential.access_key", "ABCDEF"
                )
        );

        assertEquals("arn:aws:lambda:us-east-1:12342342332:function:pip-services-dummies", connection.getArn());
        assertEquals("1234", connection.getAccessId());
        assertEquals("ABCDEF", connection.getAccessKey());
    }
}
