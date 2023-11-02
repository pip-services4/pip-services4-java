package org.pipservices4.data.validate;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class AtLeastOneExistRuleTest {
    @Test
    public void TestOnlyOneExistRule() {
        TestObject obj = new TestObject();
        Schema schema = new Schema().withRule(new AtLeastOneExistsRule("MissingProperty", "StringProperty", "NullProperty"));
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(0, results.size());

        schema = new Schema().withRule(new AtLeastOneExistsRule("StringProperty", "NullProperty", "intField"));
        results = schema.validate(obj);
        assertEquals(0, results.size());

        schema = new Schema().withRule(new AtLeastOneExistsRule("MissingProperty", "NullProperty"));
        results = schema.validate(obj);
        assertEquals(1, results.size());
    }
}