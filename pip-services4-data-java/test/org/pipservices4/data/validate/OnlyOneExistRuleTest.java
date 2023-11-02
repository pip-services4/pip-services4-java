package org.pipservices4.data.validate;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class OnlyOneExistRuleTest {
    @Test
    public void TestOnlyOneExistRule() {
        TestObject obj = new TestObject();
        Schema schema = new Schema().withRule(new OnlyOneExistsRule("MissingProperty", "StringProperty", "NullProperty"));
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(0, results.size());

        schema = new Schema().withRule(new OnlyOneExistsRule("StringProperty", "NullProperty", "intField"));
        results = schema.validate(obj);
        assertEquals(1, results.size());
    }
}