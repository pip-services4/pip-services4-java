# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/>Business Logic Components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The Logic module contains standard component definitions to handle complex business transactions.

The module contains the following packages:
- **Cache** - distributed cache
- **Lock** -  distributed lock components
- **State** -  distributed state management components

<a name="links"></a> Quick links:

* [Logging](http://docs.pipservices.org/getting_started/recipes/logging/)
* [Configuration](http://docs.pipservices.org/concepts/configuration/component_configuration/)
* [API Reference](https://pip-services4-java.github.io/pip-services4-logic-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-logic</artifactId>
  <version>0.0.1</version>
</dependency>
```

Example how to use caching and locking.
Here we assume that references are passed externally.

```java

import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.config.ConfigException;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.logic.cache.ICache;
import org.pipservices4.logic.lock.*;

public class MyComponent implements IReferenceable {

    private ICache _cache;
    private ILock _lock;

    @Override
    public void setReferences(IReferences refs) throws ReferenceException, ConfigException {
        this._cache = (ICache) refs.getOneRequired(new Descriptor("*", "cache", "*", "*", "1.0"));
        this._lock = (ILock) refs.getOneRequired(new Descriptor("*", "lock", "*", "*", "1.0"));
    }

    public Object myMethod(IContext context, Object param1) {
        // First check cache for result
        Object result = this._cache.retrieve(context, "mykey");
        if (result != null)
            return result;

        // Lock..
        this._lock.acquireLock(context, "mykey", 1000, 1000);

        // Do processing
        // ...

        // Store result to cache async
        this._cache.store(context, "mykey", result, 3600000);

        // Release lock async
        this._lock.releaseLock(context, "mykey");

        return result;
    }
}
```

Use the component:

```java
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.References;
import org.pipservices4.logic.cache.MemoryCache;
import org.pipservices4.logic.lock.MemoryLock;

public class MainClass {
    public static void main(String[] args) throws ApplicationException {
        // Use the component
        MyComponent myComponent = new MyComponent();

        myComponent.setReferences(References.fromTuples(
                        new Descriptor("pip-services", "cache", "memory", "default", "1.0"), new MemoryCache(),
                        new Descriptor("pip-services", "lock", "memory", "default", "1.0"), new MemoryLock()
                )
        );

        Object result = myComponent.myMethod(null, null);
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
