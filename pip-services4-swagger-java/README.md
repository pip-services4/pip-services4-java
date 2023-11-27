# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Swagger UI for Pip.Services in Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.

The swagger module provides a Swagger UI that can be added into microservices and seamlessly integrated with existing REST and Commandable HTTP services.

The module contains the following packages:
- **Build** - Swagger controller factory
- **Controllers** - Swagger UI controller

<a name="links"></a> Quick links:

* [API Reference](https://pip-services4-java.github.io/pip-services4-swagger-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)


## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-swagger</artifactId>
  <version>0.0.1</version>
</dependency>
```

Develop a RESTful service component. For example, it may look the following way.
In the `register` method we load an Open API specification for the service.
You can also enable swagger by default in the constractor by setting `_swaggerEnable` property.

```java
package org.pipservices4.swagger.example.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.validate.ObjectSchema;
import org.pipservices4.http.controllers.RestController;

import java.util.Objects;

public class MyRestController extends RestController {
    public MyRestController() {
        super();
        this._baseRoute = "myservice";
        this._swaggerEnable = true;
    }

    private Response greeting(ContainerRequestContext req) {
        var name = req.getUriInfo().getPathParameters().get("name").get(0);
        var response = "Hello, " + name + "!";
        return this.sendResult(response);
    }

    @Override
    public void register() {
        this.registerRoute(
                HttpMethod.GET, "/greeting",
                new ObjectSchema()
                        .withRequiredProperty("name", TypeCode.String),
                this::greeting
        );

        var dirname = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
        this.registerOpenApiSpecFromFile(dirname + "./org/pipservices4/swagger/example/controllers/dummy.yml");
    }
}
```

The Open API specification for the service shall be prepared either manually
or using [Swagger Editor](https://editor.swagger.io/)
```yaml
openapi: '3.0.2'
info:
  title: 'MyService'
  description: 'MyService REST API'
  version: '1'
paths:
  /myservice/greeting:
    get:
      tags:
        - myservice
      operationId: 'greeting'
      parameters:
      - name: trace_id
        in: query
        description: Trace ID
        required: false
        schema:
          type: string
      - name: name
        in: query
        description: Name of a person
        required: true
        schema:
          type: string
      responses:
        200:
          description: 'Successful response'
          content:
            application/json:
              schema:
                type: 'string'
```

Include Swagger service into `config.yml` file and enable swagger for your REST or Commandable HTTP services.
Also explicitely adding HttpEndpoint allows to share the same port betwee REST services and the Swagger service.
```yaml
---
...
# Shared HTTP Endpoint
- descriptor: "pip-services:endpoint:http:default:1.0"
  connection:
    protocol: http
    host: localhost
    port: 8080

# Swagger Service
- descriptor: "pip-services:swagger-service:http:default:1.0"

# My RESTful Service
- descriptor: "myservice:service:rest:default:1.0"
  swagger:
    enable: true
```

Finally, remember to add factories to your container, to allow it creating required components.
```java
...
import org.pipservices4.container.ProcessContainer;
import org.pipservices4.rpc.build.DefaultRpcFactory;
import org.pipservices4.swagger.build.DefaultSwaggerFactory;

public class MyProcess extends ProcessContainer {
    public MyProcess() {
        super("myservice", "MyService microservice");

        this._factories.add(new DefaultRpcFactory());
        this._factories.add(new DefaultSwaggerFactory());
        this._factories.add(new MyServiceFactory());
    ...
    }
}
```

Launch the microservice and open the browser to open the Open API specification at
[http://localhost:8080/greeting/swagger](http://localhost:8080/greeting/swagger)

Then open the Swagger UI using the link [http://localhost:8080/swagger](http://localhost:8080/swagger).
The result shall look similar to the picture below.

<img src="swagger-ui.png"/>

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

The Node.js version of Pip.Services is created and maintained by:
- **Sergey Seroukhov**
- **Danil Prisiazhnyi**
