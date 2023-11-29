# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> IoC container for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit. It provides an inversion-of-control (IoC) container to facilitate the development of services and applications composed of loosely coupled components.

The module containes a basic in-memory container that can be embedded inside a service or application, or can be run by itself.
The second container type can run as a system level process and can be configured via command line arguments.
Also it can be used to create docker containers.

The containers can read configuration from JSON or YAML files use it as a recipe for instantiating and configuring components.
Component factories are used to create components based on their locators (descriptor) defined in the container configuration.
The factories shall be registered in containers or dynamically in the container configuration file.

The module contains the following packages:
- **Containers** - Basic in-memory and process containers
- **Build** - Default container factory
- **Config** - Container configuration components
- **Refer** - Inter-container reference management (implementation of the Referenceable pattern inside an IoC container)
- **Test** - minimal set of test components to make testing easier

<a name="links"></a> Quick links:

* [API Reference](https://pip-services4-java.github.io/pip-services4-container-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-container</artifactId>
  <version>0.0.1</version>
</dependency>
```

Create a factory to create components based on their locators (descriptors).

```java
import org.pipservices4.components.build.Factory;
import org.pipservices4.components.refer.Descriptor;

public class MyFactory extends Factory {
    public static Descriptor MyComponentDescriptor = new Descriptor("myservice", "mycomponent", "default", "*", "1.0");

    public MyFactory() {
        this.registerAsType(MyFactory.MyComponentDescriptor, MyComponent.class);
    }
}
```

Then create a process container and register the factory there. You can also register factories defined in other
modules if you plan to include external components into your container.

```java
import org.pipservices4.rpc.build.DefaultRpcFactory;

public class MyProcess extends ProcessContainer {
    public MyProcess() {
        super("myservice", "My service running as a process");
        this._factories.add(new DefaultRpcFactory());
        this._factories.add(new MyFactory());

    }
}
```

Define YAML configuration file with components and their descriptors.
The configuration file is pre-processed using [Handlebars templating engine](https://handlebarsjs.com)
that allows to inject configuration parameters or dynamically include/exclude components using conditional blocks.
The values for the templating engine are defined via process command line arguments or via environment variables.
Support for environment variables works well in docker or other containers like AWS Lambda functions.

```yaml
---
# Context information
- descriptor: "pip-services:context-info:default:default:1.0"
  name: myservice
  description: My service running in a process container

# Console logger
- descriptor: "pip-services:logger:console:default:1.0"
  level: {{LOG_LEVEL}}{{^LOG_LEVEL}}info{{/LOG_LEVEL}}

# Performance counters that posts values to log
- descriptor: "pip-services:counters:log:default:1.0"
  
# My component
- descriptor: "myservice:mycomponent:default:default:1.0"
  param1: XYZ
  param2: 987
  
{{#if HTTP_ENABLED}}
# HTTP endpoint version 1.0
- descriptor: "pip-services:endpoint:http:default:1.0"
  connection:
    protocol: "http"
    host: "0.0.0.0"
    port: {{HTTP_PORT}}{{^HTTP_PORT}}8080{{/HTTP_PORT}}

 # Default Status
- descriptor: "pip-services:status-container:http:default:1.0"

# Default Heartbeat
- descriptor: "pip-services:heartbeat-container:http:default:1.0"
{{/if}}
```

To instantiate and run the container we need a simple process launcher.

```java
public class Program {
    public static void main(String[] args) {
        try {
            MyProcess proc = new MyProcess();
            proc.run(args);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
```

```bash
mvn compile exec:java -Dexec.mainClass="org.example.Program"
```
## Develop

For development, you shall install the following prerequisites:
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
