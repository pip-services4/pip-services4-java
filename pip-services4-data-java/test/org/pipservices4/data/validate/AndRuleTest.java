package org.pipservices4.data.validate;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AndRuleTest {
    @Test
    public void testAndRule() {
        TestObject obj = new TestObject();

        Schema schema = new Schema().withRule(
                new AndRule(
                        new AtLeastOneExistsRule("missingProperty", "stringProperty", "nullProperty"),
                        new AtLeastOneExistsRule("stringProperty", "nullProperty", "intField")
                )
        );

        List<ValidationResult> results = schema.validate(obj);
        assertEquals(results.size(), 0);

        schema = new Schema().withRule(
                new AndRule(
                        new AtLeastOneExistsRule("missingProperty", "stringProperty", "nullProperty"),
                        new AtLeastOneExistsRule("missingProperty", "nullProperty")
                )
        );

        results = schema.validate(obj);
        assertEquals(results.size(), 1);
    }
}
