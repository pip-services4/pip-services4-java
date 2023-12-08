package org.pipservices4.aws;

import org.pipservices4.components.build.Factory;
import org.pipservices4.components.refer.Descriptor;

public class DummyFactory extends Factory {
	public static final Descriptor Descriptor = new Descriptor("pip-services-dummies", "factory", "default", "default", "1.0");
	public static final Descriptor ControllerDescriptor = new Descriptor("pip-services-dummies", "service", "default", "*", "1.0");
	public static final Descriptor LambdaControllerDescriptor = new Descriptor("pip-services-dummies", "controller", "awslambda", "*", "1.0");
	public static final Descriptor CmdLambdaControllerDescriptor = new Descriptor("pip-services-dummies", "controller", "commandable-awslambda", "*", "1.0");
	
	public DummyFactory() {
		super();
		registerAsType(DummyFactory.ControllerDescriptor, DummyService.class);
		registerAsType(DummyFactory.LambdaControllerDescriptor, DummyLambdaController.class);
		registerAsType(DummyFactory.CmdLambdaControllerDescriptor, DummyCommandableLambdaController.class);
	}
	
}
