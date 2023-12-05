package org.pipservices4.aws.controllers;

import org.pipservices4.data.validate.Schema;

import java.util.Map;
import java.util.function.Function;

public class LambdaAction {
    private String cmd;
    private Schema schema;
    private Function<Map<String, Object>, ?> action;

    public LambdaAction(String cmd, Schema schema, Function<Map<String, Object>, ?> action) {
        this.cmd = cmd;
        this.schema = schema;
        this.action = action;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Function<Map<String, Object>, ?> getAction() {
        return action;
    }

    public void setAction(Function<Map<String, Object>, ?> action) {
        this.action = action;
    }
}
