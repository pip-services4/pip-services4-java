# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> GRPC components for Pip.Services in Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The grpc module is used to organize synchronous data exchange using calls through the gRPC protocol. It has implementations of both the server and client parts.

The module contains the following packages:

- **Build** - factories for creating gRPC controllers
- **Clients** - basic client components that use the gRPC protocol and Commandable pattern through gRPC
- **Controllers** - basic controller implementations for connecting via the gRPC protocol and using the Commandable pattern via gRPC

<a name="links"></a> Quick links:

* [Configuration](http://docs.pipservices.org/toolkit/getting_started/configurations/)
* [Protocol buffer](https://github.com/pip-services4/pip-services4-java/tree/main/pip-services4-grpc-java/src/org/pipservices4/grpc/proto/commandable.proto)
* [API Reference](https://pip-services4-java.github.io/pip-services4-grpc-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-grpc</artifactId>
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
