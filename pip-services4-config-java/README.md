# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/>Config Components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The Config module contains configuration component definitions that can be used to build applications and services.

The module contains the following packages:
- **Auth** - authentication credential stores
- **Config** - configuration readers and managers, whose main task is to deliver configuration parameters to the application from wherever they are being stored
- **Connect** - connection discovery and configuration services


<a name="links"></a> Quick links:

* [Configuration](http://docs.pipservices.org/concepts/configuration/component_configuration/) 
* [API Reference](https://pip-services4-java.github.io/pip-services4-config-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-config</artifactId>
  <version>0.0.1</version>
</dependency>
```

Example how to get connection parameters and credentials using resolvers.
The resolvers support "discovery_key" and "store_key" configuration parameters
to retrieve configuration from discovery services and credential stores respectively.


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
import org.pipservices4.config.auth.CredentialParams;
import org.pipservices4.config.auth.CredentialResolver;
import org.pipservices4.config.connect.ConnectionParams;
import org.pipservices4.config.connect.ConnectionResolver;

public class MyComponent implements IConfigurable, IReferenceable, IOpenable {
    private boolean _opened = false;
    private final ConnectionResolver _connectionResolver = new ConnectionResolver();
    private final CredentialResolver _credentialResolver = new CredentialResolver();

    @Override
    public void configure(ConfigParams configParams) throws ConfigException {
        this._connectionResolver.configure(configParams);
        this._credentialResolver.configure(configParams);
    }

    @Override
    public void setReferences(IReferences refs) throws ReferenceException, ConfigException {
        this._connectionResolver.setReferences(refs);
        this._credentialResolver.setReferences(refs);
    }


    @Override
    public boolean isOpen() {
        return _opened;
    }

    @Override
    public void open(IContext context) throws ApplicationException {
        ConnectionParams connection = this._connectionResolver.resolve(context);
        CredentialParams credential = this._credentialResolver.lookup(context);

        String host = connection.getHost();
        int port = connection.getPort();
        String user = credential.getUsername();
        String pass = credential.getPassword();

        _opened = true;
    }

    @Override
    public void close(String s) throws ApplicationException {
        _opened = false;
    }
}


```


Using the component:

```java
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ApplicationException;

public class MainClass {
    public static void main(String[] args) throws ApplicationException {
        MyComponent myComponent = new MyComponent();

        myComponent.configure(ConfigParams.fromTuples(
                "connection.host", "localhost",
                "connection.port", 1234,
                "credential.username", "anonymous",
                "credential.password", "pass123"
        ));

        myComponent.open(null);
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
