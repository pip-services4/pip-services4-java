package org.pipservices4.aws.test;

import jakarta.ws.rs.core.GenericType;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.aws.clients.LambdaClient;

import java.util.Map;

/**
 * AWS Lambda client used for automated testing.
 */
public class TestLambdaClient extends LambdaClient {
    public TestLambdaClient() {
        super();
    }

    /**
     * Calls a AWS Lambda Function action.
     *
     * @param type          the class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return             action result.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T call(Class<T> type, String cmd, IContext context, Map<String, Object> params)
            throws ApplicationException {

        return super.call(type, cmd, context, params);
    }

    /**
     * Calls a AWS Lambda Function action asynchronously without waiting for response.
     *
     * @param type          the class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return result object.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T callOneWay(Class<T> type, String cmd, IContext context, Map<String, Object> params) throws ApplicationException {
        return super.callOneWay(type, cmd, context, params);
    }
}
