package org.pipservices4.data.validate;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class PropertiesComparisonRuleTest {
    @Test
    public void TestPropertiesComparison() {
        TestObject obj = new TestObject();
        Schema schema = new Schema().withRule(new PropertiesComparisonRule("StringProperty", "EQ", "NullProperty"));

        obj.setStringProperty("ABC");
        obj.setNullProperty("ABC");
        List<ValidationResult> results = schema.validate(obj);
        assertEquals(0, results.size());

        obj.setNullProperty("XYZ");
        results = schema.validate(obj);
        assertEquals(1, results.size());
    }
}