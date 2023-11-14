package org.pipservices4.rpc.commands;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.exec.IExecutable;
import org.pipservices4.components.exec.Parameters;

public class CommandExec implements IExecutable {
    @Override
    public Object execute(IContext context, Parameters args) throws ApplicationException {
        if (context.equals("wrongId"))
            throw new ApplicationException(null, null, null, "Test error");

        return 0;
    }
}
