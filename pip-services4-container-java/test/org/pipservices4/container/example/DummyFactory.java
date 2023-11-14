package org.pipservices4.container.example;

import org.pipservices4.components.build.Factory;
import org.pipservices4.components.refer.*;

public class DummyFactory extends Factory {
    public static Descriptor ControllerDescriptor = new Descriptor("pip-services-dummies", "controller", "*", "*", "1.0");

    public DummyFactory() {
        registerAsType(ControllerDescriptor, DummyController.class);
    }
}