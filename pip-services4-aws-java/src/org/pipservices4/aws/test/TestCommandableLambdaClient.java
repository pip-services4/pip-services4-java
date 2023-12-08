package org.pipservices4.aws.test;

import jakarta.ws.rs.core.GenericType;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.aws.clients.CommandableLambdaClient;

import java.util.Map;

public class TestCommandableLambdaClient extends CommandableLambdaClient {
    public TestCommandableLambdaClient(String name) {
        super(name);
    }

    /**
     * Calls a remote action in AWS Lambda function.
     * The name of the action is added as "cmd" parameter
     * to the action parameters.
     *
     * @param type          the class type of data.
     * @param cmd               an action name
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            command parameters.
     * @return {any}            action result.
     * @throws ApplicationException when error occured.
     */
    @Override
    public <T> T callCommand(Class<T> type, String cmd, IContext context, Map<String, Object> params)
            throws ApplicationException {
        return super.callCommand(type, cmd, context, params);
    }
}
