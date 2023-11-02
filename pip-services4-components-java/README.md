# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Component definitions for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

It defines a portable component model interfaces and provides utility classes to handle component lifecycle.

The module contains the following packages:
- **Build** - basic factories for constructing objects
- **Config** - configuration pattern
- **Refer** - locator inversion of control (IoC) pattern
- **Run** - component life-cycle management patterns

<a name="links"></a> Quick links:

* [Configuration Pattern](http://docs.pipservices.org/toolkit/getting_started/configurations/)
* [Locator Pattern](http://docs.pipservices.org/toolkit/recipes/component_references/)
* [Component Lifecycle](http://docs.pipservices.org/toolkit/recipes/component_lifecycle/)
* [API Reference](https://pip-services4-java.github.io/pip-services4-components-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-components</artifactId>
  <version>0.0.1</version>
</dependency>
```

For instance, here is how you can implement a component, that receives configuration, get assigned references,
can be opened and closed using the patterns from this module.

```java
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.config.ConfigException;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.commons.errors.ApplicationException;

public class MyComponent implements IConfigurable, IReferenceable, IOpenable {

    private String _param1 = "ABC";
    private int _param1 = "ABC";
    private MyComponentB _anotherComponent;
    private boolean _opened = false;

    @Override
    public void configure(ConfigParams config) throws ConfigException {
        _param1 = config.getAsStringWithDefault("param1", _param1);
        _param2 = config.getAsIntegerWithDefault("param2", _param2);
    }

    @Override
    public void setReferences(IReferences refs) throws ReferenceException, ConfigException {
        _anotherComponent = (MyComponentB) refs.getOneRequired(
            new Descriptor("myservice", "mycomponent-b", "*", "*", "1.0")
        );
    }


    @Override
    public boolean isOpen() {
        return _opened;
    }

    @Override
    public void open(IContext context) throws ApplicationException {
        _opened = true;

    }

    @Override
    public void close(IContext context) throws ApplicationException {
        _opened = false;
    }
}


```

Using the component:

```java
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.refer.References;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.commons.errors.ApplicationException;

public class MainClass {
    public static void main(String[] args) throws ApplicationException {
        // Using the component
        MyComponent myComponent = new MyComponent();

        // Configure the component
        myComponent.configure(ConfigParams.fromTuples(
            'param1', 'XYZ',
            'param2', 987
        ));

        // Set references to the component
        myComponentA.setReferences(References.fromTuples(
            new Descriptor("myservice", "mycomponent-b", "default", "default", "1.0",) myComponentB
        ));

        myComponent.open(null);
    }
}
```

If you need to create components using their locators (descriptors) implement
component factories similar to the example below.

```java
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;

public class MyFactory extends Factory {
    public static Descriptor myComponentDescriptor = new Descriptor("myservice", "mycomponent", "default", "*", "1.0");

    public MyFactory() {
        super();
        this.registerAsType(MyFactory.myComponentDescriptor, MyComponent.class);
    }
}

// Using the factory

MyFactory myFactory = new MyFactory();

MyComponent myComponent1 = (MyComponent) myFactory.create(new Descriptor("myservice", "mycomponent", "default", "myComponent1", "1.0"));
MyComponent myComponent2 = (MyComponent) myFactory.create(new Descriptor("myservice", "mycomponent", "default", "myComponent2", "1.0"));
...
```

## Develop

For development you shall install the following prerequisites:
* Java SE Development Kit 17+
* Eclipse Java Photon or another IDE of your choice
* Docker
* Apache Maven

Build the project:
```bash
mvn install
```

Run automated tests:
```bash
mvn test
```

Generate API documentation:
```bash
./docgen.ps1
```

Before committing changes run dockerized build and test as:
```bash
./build.ps1
./test.ps1
./clear.ps1
```

## Contacts

The initial implementation is done by **Sergey Seroukhov**. Pip.Services team is looking for volunteers to 
take ownership over Java implementation in the project.
