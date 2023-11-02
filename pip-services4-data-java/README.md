# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Data Handling Components

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

It defines a portable component model interfaces and provides utility classes to handle component lifecycle.

It dynamic and static objects and data handling components.

The module contains the following packages:
- **Data** - data patterns
- **Keys**- object key (id) generators
- **Process**- data processing components
- **Query**- data query objects
- **Random** - random data generators
- **Validate** - validation patterns


<a name="links"></a> Quick links:

* [API Reference](https://pip-services4-java.github.io/pip-services4-data-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-data</artifactId>
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

The initial implementation is done by **Sergey Seroukhov**. Pip.Services team is looking for volunteers to 
take ownership over Java implementation in the project.
