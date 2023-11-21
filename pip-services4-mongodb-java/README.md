# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> MongoDB components for Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit. It provides a set of components to implement MongoDB persistence.

The module contains the following packages:
- **Build** - Factory to create MongoDB persistence components.
- **Codecs** - contains all the default BSON codecs.
- **Connect** - Connection component to configure MongoDB connection to database.
- **Persistence** - abstract persistence components to perform basic CRUD operations.

<a name="links"></a> Quick links:

* [MongoDB persistence](https://www.pipservices.org/recipies/mongodb-persistence)
* [Configuration](https://www.pipservices.org/recipies/configuration)
* [API Reference](https://pip-services4-java.github.io/pip-services4-mongodb-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](https://www.pipservices.org/community/help)
* [Contribute](https://www.pipservices.org/community/contribute)

## Use

Go to the pom.xml file in Maven project and add dependencies:
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-mongodb</artifactId>
  <version>0.0.1</version>
</dependency>
```

As an example, lets implement persistence for the following data object.

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pipservices4.data.data.IStringIdentifiable;

public class MyObject implements IStringIdentifiable {
    public MyObject() {}

    public MyObject(String id, String key, String content) {
        super();
        this._id = id;
        this._key = key;
        this._content = content;
    }


    @JsonProperty("id")
    private String _id;
    public String getId() {	return _id; }
    public void setId(String id) {	this._id = id;}

    @JsonProperty("key")
    private String _key;
    public String getKey() { return _key; }
    public void setKey(String key) { this._key = key; }

    @JsonProperty("content")
    private String _content;
    public String getContent() { return _content; }
    public void setContent(String content) { this._content = content; }
}

```

Our persistence component shall implement the following interface with a basic set of CRUD operations.

```java
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.commons.errors.ApplicationException;


public interface IMyPersistence {
    DataPage<Dummy> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) throws ApplicationException;
    Dummy getOneById(IContext context, String dummyId) throws ApplicationException;
    Dummy getOneByKey(IContext context, String key) throws ApplicationException;
    Dummy create(IContext context, Dummy dummy) throws ApplicationException;
    Dummy update(IContext context, Dummy dummy) throws ApplicationException;

    Dummy deleteById(IContext context, String dummyId) throws ApplicationException;
}
```

To implement in-memory persistence component you shall inherit `IdentifiableMemoryPersistence`.
Most CRUD operations will come from the base class. You only need to override `getPageByFilter` method with a custom filter function.
And implement a `getOneByKey` custom persistence method that doesn't exist in the base class.

```java
import org.pipservices4.data.query.DataPage;
import org.pipservices4.data.query.FilterParams;
import org.pipservices4.data.query.PagingParams;

import java.util.Objects;
import java.util.function.Predicate;

public class MyMemoryPersistence extends IdentifiableMemoryPersistence<MyObject, String> {
    protected MyMemoryPersistence() {
        super(MyObject.class);
    }

    private Predicate<MyObject> composeFilter(FilterParams filter) {
        filter = filter != null ? filter : new FilterParams();
        var key = filter.getAsNullableString("key");

        return (item) -> {
            return key == null || Objects.equals(item.getKey(), key);
        };
    }

    public DataPage<MyObject> getPageByFilter(IContext context, FilterParams filter, PagingParams paging) {
        return super.getPageByFilter(context, composeFilter(filter), paging, null);
    }

    public MyObject getOneByKey(IContext context, String key) {
        synchronized(this._lock) {
          var item = this._items.stream().filter((el) -> el.getKey().equals(key)).findAny();

          if (item.isPresent())
              this._logger.trace(context, "Found object by key=%s", key);
          else
              this._logger.trace(context, "Cannot find by key=%s", key);
          
          return item.orElse(null);
        }
    }
}
```
Configuration for your microservice that includes mongodb persistence may look the following way.

```yaml
...
{{#if MONGODB_ENABLED}}
- descriptor: pip-services:connection:mongodb:con1:1.0
  connection:
    uri: {{{MONGO_SERVICE_URI}}}
    host: {{{MONGO_SERVICE_HOST}}}{{#unless MONGO_SERVICE_HOST}}localhost{{/unless}}
    port: {{MONGO_SERVICE_PORT}}{{#unless MONGO_SERVICE_PORT}}27017{{/unless}}
    database: {{MONGO_DB}}{{#unless MONGO_DB}}app{{/unless}}
  credential:
    username: {{MONGO_USER}}
    password: {{MONGO_PASS}}
    
- descriptor: myservice:persistence:mongodb:default:1.0
  dependencies:
    connection: pip-services:connection:mongodb:con1:1.0
  collection: {{MONGO_COLLECTION}}{{#unless MONGO_COLLECTION}}myobjects{{/unless}}
{{/if}}
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
