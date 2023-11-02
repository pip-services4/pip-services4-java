package org.pipservices4.data.validate;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class InclusionRulesTest {
    @Test
    public void TestIncludedRule() {
        Schema schema = new Schema().withRule(new IncludedRule("AAA", "BBB", "CCC", null));
        List<ValidationResult> results = schema.validate("AAA");
        assertEquals(0, results.size());

        results = schema.validate("ABC");
        assertEquals(1, results.size());
    }

    @Test
    public void TestExcludedRule() {
        Schema schema = new Schema().withRule(new ExcludedRule("AAA", "BBB", "CCC", null));
        List<ValidationResult> results = schema.validate("AAA");
        assertEquals(1, results.size());

        results = schema.validate("ABC");
        assertEquals(0, results.size());
    }
}