package org.pipservices4.commons.reflect;

import org.pipservices4.commons.convert.DateTimeConverter;
import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.commons.convert.TypeConverter;

/**
 * Helper class matches value types for equality.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * 
 * @see TypeCode
 */
public class TypeMatcher {

	/**
	 * Matches expected type to a type of a value. The expected type can be
	 * specified by a type, type name or TypeCode.
	 * 
	 * @param expectedType an expected type to match.
	 * @param actualValue  a value to match its type to the expected one.
	 * @return true if types are matching and false if they don't.
	 * 
	 * @see #matchType(Object, TypeCode)
	 * @see #matchValueTypeByName(String, Object) (for matching by types' string names)
	 */
	public static boolean matchValueType(Object expectedType, Object actualValue) {
		if (expectedType == null)
			return true;
		if (actualValue == null)
			throw new NullPointerException("Actual value cannot be null");

		return matchType(expectedType, TypeConverter.toTypeCode(actualValue.getClass()));
	}

	/**
	 * Matches expected type to an actual type. The types can be specified as types,
	 * type names or TypeCode.
	 *
	 * @param expectedType an expected type to match.
	 * @param actualType   an actual type to match.
	 * @param actualValue an optional value to match its type to the expected one.
	 * @return true if types are matching and false if they don't.
	 *
	 * @see #matchTypeByName(String, TypeCode)
	 */
	public static boolean matchType(Object expectedType, TypeCode actualType, Object actualValue) {
		if (expectedType == null)
			return true;
		if (actualType == null)
			throw new NullPointerException("Actual type cannot be null");

		if (!(expectedType instanceof String)) {
			TypeCode type;
			if (expectedType instanceof Class)
				type = TypeConverter.toTypeCode(expectedType);
			else
				type = (TypeCode) expectedType;

			if (type != null) {
				if (type == actualType)
					return true;
				// Special provisions for dynamic data
				if (type == TypeCode.Integer
						&& (actualType == TypeCode.Long || actualType == TypeCode.Float || actualType == TypeCode.Double))
					return true;
				if (type == TypeCode.Long
						&& (actualType == TypeCode.Integer || actualType == TypeCode.Float || actualType == TypeCode.Double))
					return true;
				if (type == TypeCode.Float
						&& (actualType == TypeCode.Integer || actualType == TypeCode.Long || actualType == TypeCode.Double))
					return true;
				if (type == TypeCode.Double
						&& (actualType == TypeCode.Integer || actualType == TypeCode.Long || actualType == TypeCode.Float))
					return true;
				if (type == TypeCode.DateTime
						&& (actualType == TypeCode.String && DateTimeConverter.toNullableDateTime(actualValue) != null))
					return true;
				return false;
			}
		}

		if (expectedType.equals(actualType))
			return true;

		if (expectedType instanceof String)
			return TypeMatcher.matchTypeByName((String) expectedType, actualType, actualValue);

		return matchTypeByName(expectedType.toString(), actualType);
	}

	/**
	 * Matches expected type to an actual type. The types can be specified as types,
	 * type names or TypeCode.
	 * 
	 * @param expectedType an expected type to match.
	 * @param actualType   an actual type to match.
	 * @return true if types are matching and false if they don't.
	 * 
	 * @see #matchTypeByName(String, TypeCode)
	 */
	public static boolean matchType(Object expectedType, TypeCode actualType) {
		return matchType(expectedType, actualType, null);
	}

	/**
	 * Matches expected type to a type of a value.
	 * 
	 * @param expectedType an expected type name to match.
	 * @param actualValue  a value to match its type to the expected one.
	 * @return true if types are matching and false if they don't.
	 */
	public static boolean matchValueTypeByName(String expectedType, Object actualValue) {
		if (expectedType == null)
			return true;
		if (actualValue == null)
			throw new NullPointerException("Actual value cannot be null");

		return matchTypeByName(expectedType, TypeConverter.toTypeCode(actualValue.getClass()));
	}

	/**
	 * Matches expected type to an actual type.
	 * @param expectedType an expected type name to match.
	 * @param actualType an actual type to match defined by type code.
	 * @param actualValue an optional value to match its type to the expected one.
	 * @return true if types are matching and false if they don't.
	 */
	public static boolean matchTypeByName(String expectedType, TypeCode actualType, Object actualValue) {
		if (expectedType == null)
			return true;
		if (actualType == null)
			throw new NullPointerException("Actual type cannot be null");

		expectedType = expectedType.toLowerCase();

		if (expectedType.equals("object"))
			return true;
		else if (expectedType.equals("int") || expectedType.equals("integer")) {
			return actualType == TypeCode.Integer
					// Special provisions for dynamic data
					|| actualType == TypeCode.Long;
		} else if (expectedType.equals("long")) {
			return actualType == TypeCode.Long
					// Special provisions for dynamic data
					|| actualType == TypeCode.Integer;
		} else if (expectedType.equals("float")) {
			return actualType == TypeCode.Float
					// Special provisions for dynamic data
					|| actualType == TypeCode.Double
					|| actualType == TypeCode.Integer
					|| actualType == TypeCode.Long;
		} else if (expectedType.equals("double")) {
			return actualType == TypeCode.Double
					// Special provisions fro dynamic data
					|| actualType == TypeCode.Float;
		} else if (expectedType.equals("string")) {
			return actualType == TypeCode.String;
		} else if (expectedType.equals("bool") || expectedType.equals("boolean")) {
			return actualType == TypeCode.Boolean;
		} else if (expectedType.equals("date") || expectedType.equals("datetime")) {
			return actualType == TypeCode.DateTime
					// Special provisions fro dynamic data
					|| (actualType == TypeCode.String && DateTimeConverter.toNullableDateTime(actualValue) != null);
		} else if (expectedType.equals("timespan") || expectedType.equals("duration")) {
			return actualType == TypeCode.Integer
					|| actualType == TypeCode.Long
					|| actualType == TypeCode.Float
					|| actualType == TypeCode.Double;
		} else if (expectedType.equals("enum")) {
			return actualType == TypeCode.Integer
					|| actualType == TypeCode.String;
		} else if (expectedType.equals("map") || expectedType.equals("dict") || expectedType.equals("dictionary")) {
			return actualType == TypeCode.Map;
		} else if (expectedType.equals("array") || expectedType.equals("list")) {
			return actualType == TypeCode.Array;
		} else if (expectedType.endsWith("[]")) {
			// Todo: Check subtype
			return actualType == TypeCode.Array;
		} else
			return false;
	}

	/**
	 * Matches expected type to an actual type.
	 * 
	 * @param expectedType an expected type name to match.
	 * @param actualType   an actual type to match defined by type code.
	 * @return true if types are matching and false if they don't.
	 */
	public static boolean matchTypeByName(String expectedType, TypeCode actualType) {
		return matchTypeByName(expectedType, actualType, null);
	}
}
