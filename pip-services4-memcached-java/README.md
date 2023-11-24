# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Memcached components for Pip.Services in Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The Memcached module contains the following components: MemcachedLock and MemcachedCache for working with locks and cache on the Memcached server.

The module contains the following packages:
- **Build** - a standard factory for constructing components.
- **Cache** - cache Components in Memcached
- **Lock** - components of working with locks in Memcached

<a name="links"></a> Quick links:

* [Configuration](http://docs.pipservices.org/toolkit/getting_started/configurations/)
* [API Reference](https://pip-services4-java.github.io/pip-services4-memcached-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-memcached</artifactId>
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
