package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.time.*;
import java.util.*;

import org.junit.*;
import org.pipservices4.commons.data.*;

public class TypeConverterTest {

	@Test
	public void testToTypeCode() {
		assertEquals(TypeCode.String, TypeConverter.toTypeCode(String.class));
		assertEquals(TypeCode.Integer, TypeConverter.toTypeCode(Integer.class));
		assertEquals(TypeCode.Long, TypeConverter.toTypeCode(Long.class));
		assertEquals(TypeCode.Float, TypeConverter.toTypeCode(Float.class));
		assertEquals(TypeCode.Double, TypeConverter.toTypeCode(Double.class));
		assertEquals(TypeCode.DateTime, TypeConverter.toTypeCode(ZonedDateTime.class));
		assertEquals(TypeCode.Enum, TypeConverter.toTypeCode(Enum.class));
		assertEquals(TypeCode.Array, TypeConverter.toTypeCode(List.class));
		assertEquals(TypeCode.Map, TypeConverter.toTypeCode(Map.class));
		assertEquals(TypeCode.Object, TypeConverter.toTypeCode(Object.class));
		assertEquals(TypeCode.Unknown, TypeConverter.toTypeCode(null));

		assertEquals(TypeCode.String, TypeConverter.toTypeCode("123"));
		assertEquals(TypeCode.Integer, TypeConverter.toTypeCode(123));
		assertEquals(TypeCode.Long, TypeConverter.toTypeCode(123L));
		assertEquals(TypeCode.Float, TypeConverter.toTypeCode(123.456f));
		assertEquals(TypeCode.Double, TypeConverter.toTypeCode(123.456));
		assertEquals(TypeCode.DateTime, TypeConverter.toTypeCode(ZonedDateTime.now()));
		//assertEquals(ConverterTypeCode.Enum, TypeConverter.toTypeCode(Enum.class));
		assertEquals(TypeCode.Array, TypeConverter.toTypeCode(new ArrayList<Integer>()));
		assertEquals(TypeCode.Array, TypeConverter.toTypeCode(new int[0]));
		assertEquals(TypeCode.Map, TypeConverter.toTypeCode(new HashMap<String, String>()));
		assertEquals(TypeCode.Object, TypeConverter.toTypeCode(new Object()));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testToNullableType() {
		assertEquals("123", TypeConverter.toNullableType(String.class, 123));
		assertEquals(123, (int)TypeConverter.toNullableType(Integer.class, "123"));
		assertEquals(123L, (long)TypeConverter.toNullableType(Long.class, 123.456));
		assertTrue(123 - TypeConverter.toNullableType(Float.class, 123) < 0.001);
		assertTrue(123 - TypeConverter.toNullableType(Double.class, 123) < 0.001);
		assertEquals(DateTimeConverter.toDateTime("1975-04-08T17:30:00.00Z"), TypeConverter.toNullableType(ZonedDateTime.class, "1975-04-08T17:30:00.00Z"));
		assertEquals(1, ((List)TypeConverter.toNullableType(List.class, 123)).size());
		assertEquals(1, ((Map)TypeConverter.toNullableType(Map.class, StringValueMap.fromString("abc=123"))).size());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testToType() {
		assertEquals("123", TypeConverter.toType(String.class, 123));
		assertEquals(123, (int)TypeConverter.toType(Integer.class, "123"));
		assertEquals(123L, (long)TypeConverter.toType(Long.class, 123.456));
		assertTrue(123 - TypeConverter.toType(Float.class, 123) < 0.001);
		assertTrue(123 - TypeConverter.toType(Double.class, 123) < 0.001);
		assertEquals(DateTimeConverter.toDateTime("1975-04-08T17:30:00.00Z"), TypeConverter.toType(ZonedDateTime.class, "1975-04-08T17:30:00.00Z"));
		assertEquals(1, ((List)TypeConverter.toType(List.class, 123)).size());
		assertEquals(1, ((Map)TypeConverter.toType(Map.class, StringValueMap.fromString("abc=123"))).size());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testToTypeWithDefault() {
		assertEquals("123", TypeConverter.toTypeWithDefault(String.class, null, "123"));
		assertEquals(123, (int)TypeConverter.toTypeWithDefault(Integer.class, null, 123));
		assertEquals(123L, (long)TypeConverter.toTypeWithDefault(Long.class, null, 123L));
		assertTrue(123 - TypeConverter.toTypeWithDefault(Float.class, null, (float)123) < 0.001);
		assertTrue(123 - TypeConverter.toTypeWithDefault(Double.class, null, 123.) < 0.001);
		assertEquals(DateTimeConverter.toDateTime("1975-04-08T17:30:00.00Z"), TypeConverter.toTypeWithDefault(ZonedDateTime.class, "1975-04-08T17:30:00.00Z", null));
		assertEquals(1, ((List)TypeConverter.toTypeWithDefault(List.class, 123, null)).size());
		assertEquals(1, ((Map)TypeConverter.toTypeWithDefault(Map.class, StringValueMap.fromString("abc=123"), null)).size());
	}
}
