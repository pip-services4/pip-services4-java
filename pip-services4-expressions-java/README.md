# <img src="https://uploads-ssl.webflow.com/5ea5d3315186cf5ec60c3ee4/5edf1c94ce4c859f2b188094_logo.svg" alt="Pip.Services Logo" width="200"> <br/> Tokenizers, parsers and expression calculators in Java

This module is a part of the [Pip.Services](http://pipservices.org) polyglot microservices toolkit.
It provides syntax and lexical analyzers and expression calculator optimized for repeated calculations.

The module contains the following packages:
- **Calculator** - Expression calculator
- **CSV** - CSV tokenizer
- **IO** - input/output utility classes to support lexical analysis
- **Mustache** - Mustache templating engine
- **Tokenizers** - lexical analyzers to break incoming character streams into tokens
- **Variants** - dynamic objects that can hold any values and operators for them

<a name="links"></a> Quick links:

* [API Reference](https://pip-services4-java.github.io/pip-services4-expressions-java/)
* [Change Log](CHANGELOG.md)
* [Get Help](http://docs.pipservices.org/get_help/)
* [Contribute](http://docs.pipservices.org/contribute/)

## Use

Go to the pom.xml file in Maven project and add dependencies::
```xml
<dependency>
  <groupId>org.pipservices</groupId>
  <artifactId>pip-services4-expressions</artifactId>
  <version>0.0.1</version>
</dependency>
```

The example below shows how to use expression calculator to dynamically
calculate user-defined expressions.

```java
import org.pipservices4.expressions.calculator.ExpressionCalculator;
import org.pipservices4.expressions.calculator.variables.Variable;
import org.pipservices4.expressions.calculator.variables.VariableCollection;
import org.pipservices4.expressions.variants.Variant;

public class Program {
    public static void main(String[] args) throws Exception {
        var calculator = new ExpressionCalculator();

        calculator.setExpression("A + b / (3 - Max(-123, 1)*2)");

        var vars = new VariableCollection();
        vars.add(new Variable("A", new Variant("1")));
        vars.add(new Variable("B", new Variant(3)));

        var result = calculator.evaluateWithVariables(vars);
        System.out.println("The result of the expression is " + result.getAsString());
    }
}

```

This is an example to process mustache templates.


```java
import org.pipservices4.expressions.mustache.MustacheTemplate;

import java.util.HashMap;
import java.util.Map;

public class Program {
    public static void main(String[] args) throws Exception {
        var mustache = new MustacheTemplate();
        mustache.setTemplate("Hello, {{{NAME}}}{{#ESCLAMATION}}!{{/ESCLAMATION}}{{#unless ESCLAMATION}}.{{/unless}}");
        var result = mustache.evaluateWithVariables(
                new HashMap<>(Map.of(
                        "NAME", "Mike",
                        "ESCLAMATION", true
                ))
        );
        System.out.println("The result of template evaluation is '" + result + "'");
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

The initial implementation is done by 
**Sergey Seroukhov** 
**Danil Prisyazhnyi**.
