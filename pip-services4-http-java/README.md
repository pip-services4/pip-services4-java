# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> HTTP/REST Communication Components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The http module provides the synchronous communication using local calls or the HTTP(S) protocol. It contains both server and client side implementations.

The module contains the following packages:
- **Auth** - authentication and authorization components
- **Build** - HTTP service factory
- **Clients** - mechanisms for retrieving connection settings from the microservice’s configuration and providing clients and services with these settings
- **Connect** - helper module to retrieve connections for HTTP-based services and clients
- **Services** - basic implementation of services for connecting via the HTTP/REST protocol and using the Commandable pattern over HTTP

<a name="links"></a> Quick links:

* [Your first microservice](http://docs.pipservices.org/toolkit/getting_started/your_first_microservice/) 
* [Data Microservice. Step 5](http://docs.pipservices.org/toolkit/tutorials/data_microservice/step5/)
* [Microservice Facade](http://docs.pipservices.org/toolkit/tutorials/microservice_facade/) 
* [Client Library. Step 3](http://docs.pipservices.org/toolkit/tutorials/client_library/step2/)
* [Client Library. Step 4](http://docs.pipservices.org/toolkit/tutorials/client_library/step3/)
* [API Reference](https://pip-services4-java.github.io/pip-services4-rpc-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-http</artifactId>
  <version>0.0.1</version>
</dependency>
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

The initial implementation is done by 
**Sergey Seroukhov**
**Danil Prisiazhnyi**
