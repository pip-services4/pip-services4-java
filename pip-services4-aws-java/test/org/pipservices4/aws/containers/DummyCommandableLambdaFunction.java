package org.pipservices4.aws.containers;

import org.pipservices4.aws.DummyFactory;
import org.pipservices4.components.refer.Descriptor;

public class DummyCommandableLambdaFunction extends CommandableLambdaFunction {

    public DummyCommandableLambdaFunction() {
        super("dummy", "Dummy lambda function");
        _dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "1.0"));
        _factories.add(new DummyFactory());
    }
}
