package org.pipservices4.aws.controllers;

import org.pipservices4.aws.DummyFactory;
import org.pipservices4.aws.containers.LambdaFunction;

public class DummyLambdaFunction extends LambdaFunction {
    public DummyLambdaFunction() {
        super("dummy", "Dummy lambda function");
        this._factories.add(new DummyFactory());
    }

    @Override
    public void register() {
        // TODO: implement register
    }

}
