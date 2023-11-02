package org.pipservices4.data.validate;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class ValueComparisonRuleTest {
    @Test
    public void TestEqualComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("EQ", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(0, results.size());

        results = schema.validate(432);
        assertEquals(1, results.size());

        schema = new Schema().withRule(new ValueComparisonRule("EQ", "ABC"));
        results = schema.validate("ABC");
        assertEquals(0, results.size());

        results = schema.validate("XYZ");
        assertEquals(1, results.size());
    }

    @Test
    public void TestNotEqualComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("NE", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(1, results.size());

        results = schema.validate(432);
        assertEquals(0, results.size());

        schema = new Schema().withRule(new ValueComparisonRule("NE", "ABC"));
        results = schema.validate("ABC");
        assertEquals(1, results.size());

        results = schema.validate("XYZ");
        assertEquals(0, results.size());
    }

    @Test
    public void TestLessThanOrEqualComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("LE", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(0, results.size());

        results = schema.validate(432);
        assertEquals(1, results.size());
    }

    @Test
    public void TestLessThanComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("LT", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(1, results.size());

        results = schema.validate(0);
        assertEquals(0, results.size());
    }

    @Test
    public void TestMoreThanOrEqualComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("GE", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(0, results.size());

        results = schema.validate(432);
        assertEquals(0, results.size());

        results = schema.validate(0);
        assertEquals(1, results.size());
    }

    @Test
    public void TestMoreThanComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("GT", 123));
        List<ValidationResult> results = schema.validate(123);
        assertEquals(1, results.size());

        results = schema.validate(432);
        assertEquals(0, results.size());

        results = schema.validate(0);
        assertEquals(1, results.size());
    }

    @Test
    public void TestMatchComparison() {
        Schema schema = new Schema().withRule(new ValueComparisonRule("LIKE", "A.*"));
        List<ValidationResult> results = schema.validate("ABC");
        assertEquals(0, results.size());

        results = schema.validate("XYZ");
        assertEquals(1, results.size());
    }
}