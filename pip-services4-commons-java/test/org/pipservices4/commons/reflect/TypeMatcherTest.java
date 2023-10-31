package org.pipservices4.commons.reflect;

import org.junit.*;
import org.pipservices4.commons.convert.TypeCode;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
import java.util.*;

public class TypeMatcherTest {
	@Test
	public void matchInteger() {
		assertTrue(TypeMatcher.matchValueTypeByName("int", 123));
		assertTrue(TypeMatcher.matchValueTypeByName("Integer", 123));
		assertTrue(TypeMatcher.matchValueType(Integer.class, 123));
	}
	
	@Test
	public void matchBoolean() {
		assertTrue(TypeMatcher.matchValueTypeByName("bool", true));
		assertTrue(TypeMatcher.matchValueTypeByName("Boolean", true));
		assertTrue(TypeMatcher.matchValueType(Boolean.class, true));
	}

	@Test
	public void matchDouble() {
		assertTrue(TypeMatcher.matchValueTypeByName("double", 123.456));
		assertTrue(TypeMatcher.matchValueTypeByName("Double", 123.456));
		assertTrue(TypeMatcher.matchValueType(Double.class, 123.456));
	}

	@Test
	public void matchLong() {
		assertTrue(TypeMatcher.matchValueTypeByName("long", 123L));
		assertTrue(TypeMatcher.matchValueType(Long.class, 123L));
	}

	@Test
	public void matchFloat() {
		assertTrue(TypeMatcher.matchValueTypeByName("float", 123.456f));
		assertTrue(TypeMatcher.matchValueTypeByName("Float", 123.456f));
		assertTrue(TypeMatcher.matchValueType(Float.class, 123.456f));
	}

	@Test
	public void matchString() {
		assertTrue(TypeMatcher.matchValueTypeByName("string", "ABC"));
		assertTrue(TypeMatcher.matchValueType(String.class, "ABC"));
	}

	@Test
	public void matchDateTime() {
		assertTrue(TypeMatcher.matchValueTypeByName("date", ZonedDateTime.now()));
		assertTrue(TypeMatcher.matchValueTypeByName("DateTime", ZonedDateTime.now()));
		assertTrue(TypeMatcher.matchValueType(ZonedDateTime.class, ZonedDateTime.now()));
		assertTrue(TypeMatcher.matchValueType(TypeCode.DateTime, ZonedDateTime.now()));
	}

	@Test
	public void matchDuration() {
		assertTrue(TypeMatcher.matchValueTypeByName("duration", 123));
		assertTrue(TypeMatcher.matchValueTypeByName("TimeSpan", 123));
	}

	@Test
	public void matchMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		assertTrue(TypeMatcher.matchValueTypeByName("map", map));
		assertTrue(TypeMatcher.matchValueTypeByName("dict", map));
		assertTrue(TypeMatcher.matchValueTypeByName("Dictionary", map));
		assertTrue(TypeMatcher.matchValueType(Map.class, map));
	}

	@Test
	public void matchArray() {
		List<Object> list = new ArrayList<Object>();
		assertTrue(TypeMatcher.matchValueTypeByName("list", list));
		assertTrue(TypeMatcher.matchValueTypeByName("array", list));
		assertTrue(TypeMatcher.matchValueTypeByName("object[]", list));
		assertTrue(TypeMatcher.matchValueType(List.class, list));

		int[] array = new int[0];
		assertTrue(TypeMatcher.matchValueTypeByName("list", array));
		assertTrue(TypeMatcher.matchValueTypeByName("array", array));
		assertTrue(TypeMatcher.matchValueTypeByName("object[]", array));
	}
}
