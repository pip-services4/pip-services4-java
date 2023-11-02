package org.pipservices4.data.validate;

import org.junit.*;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.TypeCode;

import static org.junit.Assert.*;

import java.util.*;

public class SchemasTest {
    @Test
    public void testEmptySchema() {
        ObjectSchema schema = new ObjectSchema();
        List<ValidationResult> results = schema.validate(null);
        assertEquals(0, results.size());
    }

    @Test
    public void TestRequired() {
        Schema schema = new Schema().makeRequired();
        List<ValidationResult> results = schema.validate(null);
        assertEquals(1, results.size());
    }

    @Test
    public void TestUnexpected() {
        Schema schema = new ObjectSchema();
        TestObject obj = new TestObject();
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(8, results.size());
    }

    @Test
    public void TestOptionalProperties() {
        Schema schema = new ObjectSchema()
                .withOptionalProperty("intField", null)
                .withOptionalProperty("StringProperty", null)
                .withOptionalProperty("NullProperty", null)
                .withOptionalProperty("IntArrayProperty", null)
                .withOptionalProperty("StringListProperty", null)
                .withOptionalProperty("MapProperty", null)
                .withOptionalProperty("SubObjectProperty", null)
                .withOptionalProperty("SubArrayProperty", null);

        TestObject obj = new TestObject();
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(0, results.size());
    }

    @Test
    public void TestRequiredProperties() {
        ObjectSchema schema = new ObjectSchema()
                .withRequiredProperty("intField", null)
                .withRequiredProperty("StringProperty", null)
                .withRequiredProperty("NullProperty", null)
                .withRequiredProperty("IntArrayProperty", null)
                .withRequiredProperty("StringListProperty", null)
                .withRequiredProperty("MapProperty", null)
                .withRequiredProperty("SubObjectProperty", null)
                .withRequiredProperty("SubArrayProperty", null);

        TestObject obj = new TestObject();
        obj.setSubArrayProperty(null);

        List<ValidationResult> results = schema.validate(obj);
        assertEquals(2, results.size());
    }

    @Test
    public void TestObjectTypes() {
        ObjectSchema schema = new ObjectSchema()
                .withRequiredProperty("intField", Integer.class)
                .withRequiredProperty("StringProperty", String.class)
                .withOptionalProperty("NullProperty", Object.class)
                .withRequiredProperty("IntArrayProperty", int[].class)
                .withRequiredProperty("StringListProperty", List.class)
                .withRequiredProperty("MapProperty", Map.class)
                .withRequiredProperty("SubObjectProperty", TestSubObject.class)
                .withRequiredProperty("SubArrayProperty", TestSubObject[].class);

        TestObject obj = new TestObject();
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(0, results.size());
    }

    @Test
    public void testTypes() {
        ObjectSchema subSchema = new ObjectSchema()
                .withRequiredProperty("Id", "String")
                .withRequiredProperty("FLOATFIELD", "float")
                .withOptionalProperty("nullproperty", "Object");

        Schema schema = new ObjectSchema()
                .withRequiredProperty("intField", TypeCode.Long)
                .withRequiredProperty("stringProperty", TypeCode.String)
                .withOptionalProperty("nullProperty", TypeCode.Object)
                .withRequiredProperty("intArrayProperty", TypeCode.Array)
                .withRequiredProperty("stringListProperty", TypeCode.Array)
                .withRequiredProperty("mapProperty", TypeCode.Map)
                .withRequiredProperty("subObjectProperty", subSchema)
                .withRequiredProperty("subArrayProperty", TypeCode.Array);

        TestObject obj = new TestObject();
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(results.size(), 0);
    }

    @Test
    public void testArrayAnMapSchemas() {
        Schema subSchema = new ObjectSchema()
                .withRequiredProperty("id", TypeCode.String)
                .withRequiredProperty("floatField", TypeCode.Double)
                .withOptionalProperty("nullProperty", TypeCode.Map);

        Schema schema = new ObjectSchema()
                .withRequiredProperty("intField", TypeCode.Long)
                .withRequiredProperty("stringProperty", TypeCode.String)
                .withOptionalProperty("nullProperty", TypeCode.Object)
                .withRequiredProperty("intArrayProperty", new ArraySchema(TypeCode.Long))
                .withRequiredProperty("stringListProperty", new ArraySchema(TypeCode.String))
                .withRequiredProperty("mapProperty", new MapSchema(TypeCode.String, TypeCode.Long))
                .withRequiredProperty("subObjectProperty", subSchema)
                .withRequiredProperty("subArrayProperty", TypeCode.Array);

        TestObject obj = new TestObject();
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(results.size(), 0);
    }

}