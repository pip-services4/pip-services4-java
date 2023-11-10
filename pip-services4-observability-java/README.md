# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Observability Components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The Observability module contains observability component definitions that can be used to build applications and services.

The module contains the following packages:
- **Count** - performance counters
- **Log** - basic logging components that provide console and composite logging, as well as an interface for developing custom loggers
- **Trace** - tracing components

<a name="links"></a> Quick links:

* [Logging](http://docs.pipservices.org/getting_started/recipes/logging/)
* [API Reference](https://pip-services4-java.github.io/pip-services4-observability-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-observability</artifactId>
  <version>0.0.1</version>
</dependency>
```

Example how to use Logging and Performance counters.
Here we are going to use CompositeLogger and CompositeCounters components.
They will pass through calls to loggers and counters that are set in references.

```java
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.config.ConfigException;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.count.CounterTiming;
import org.pipservices4.observability.log.CompositeLogger;

public class MyComponent implements IConfigurable, IReferenceable {
    private final CompositeLogger _logger = new CompositeLogger();
    private final CompositeCounters _counters = new CompositeCounters();


    @Override
    public void configure(ConfigParams configParams) throws ConfigException {
        this._logger.configure(configParams);
    }

    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        this._logger.setReferences(references);
        this._counters.setReferences(references);
    }

    public void myMethod(IContext context, Object param1) {
        this._logger.trace(context, "Executed method mycomponent.mymethod");
        this._counters.increment("mycomponent.mymethod.exec_count", 1);
        CounterTiming timing = this._counters.beginTiming("mycomponent.mymethod.exec_time");

        try {
            // ...
        } catch (Exception e) {
            this._logger.error(context, e, "Failed to execute mycomponent.mymethod");
            this._counters.increment("mycomponent.mymethod.error_count", 1);
        }
    }
}
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
