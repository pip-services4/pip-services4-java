package org.pipservices4.data.validate;

import org.pipservices4.commons.convert.*;

/**
 * Helper class to perform comparison operations over arbitrary values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ObjectComparator.compare(2, "GT", 1);        // Result: true
 * ObjectComparator.areEqual("A", "B");         // Result: false
 * }
 * </pre>
 * 
 */
public class ObjectComparator {
	/**
	 * Perform comparison operation over two arguments. The operation can be
	 * performed over values of any type.
	 * 
	 * @param value1    the first argument to compare
	 * @param operation the comparison operation
	 * @param value2    the second argument to compare
	 * @return result of the comparison operation
	 */
	public static boolean compare(Object value1, String operation, Object value2) {
		if (operation == null)
			return false;

		operation = operation.toUpperCase();

		if (operation.equals("=") || operation.equals("==") || operation.equals("EQ"))
			return areEqual(value1, value2);
		if (operation.equals("!=") || operation.equals("<>") || operation.equals("NE"))
			return areNotEqual(value1, value2);
		if (operation.equals("<") || operation.equals("LT"))
			return less(value1, value2);
		if (operation.equals("<=") || operation.equals("LE"))
			return areEqual(value1, value2) || less(value1, value2);
		if (operation.equals(">") || operation.equals("GT"))
			return more(value1, value2);
		if (operation.equals(">=") || operation.equals("GE"))
			return areEqual(value1, value2) || more(value1, value2);
		if (operation.equals("LIKE"))
			return match(value1, value2);

		return true;
	}

	/**
	 * Checks if two values are equal. The operation can be performed over values of
	 * any type.
	 * 
	 * @param value1 the first value to compare
	 * @param value2 the second value to compare
	 * @return true if values are equal and false otherwise
	 */
	public static boolean areEqual(Object value1, Object value2) {
		if (value1 == null && value2 == null)
			return true;
		if (value1 == null || value2 == null)
			return false;
		return value1.equals(value2);
	}

	/**
	 * Checks if two values are NOT equal The operation can be performed over values
	 * of any type.
	 * 
	 * @param value1 the first value to compare
	 * @param value2 the second value to compare
	 * @return true if values are NOT equal and false otherwise
	 */
	public static boolean areNotEqual(Object value1, Object value2) {
		return !areEqual(value1, value2);
	}

	/**
	 * Checks if first value is less than the second one. The operation can be
	 * performed over numbers or strings.
	 * 
	 * @param value1 the first value to compare
	 * @param value2 the second value to compare
	 * @return true if the first value is less than second and false otherwise.
	 */
	public static boolean less(Object value1, Object value2) {
		Double number1 = DoubleConverter.toNullableDouble(value1);
		Double number2 = DoubleConverter.toNullableDouble(value2);

		if (number1 == null || number2 == null)
			return false;

		return number1 < number2;
	}

	/**
	 * Checks if first value is greater than the second one. The operation can be
	 * performed over numbers or strings.
	 * 
	 * @param value1 the first value to compare
	 * @param value2 the second value to compare
	 * @return true if the first value is greater than second and false otherwise.
	 */
	public static boolean more(Object value1, Object value2) {
		Double number1 = DoubleConverter.toNullableDouble(value1);
		Double number2 = DoubleConverter.toNullableDouble(value2);

		if (number1 == null || number2 == null)
			return false;

		return number1 > number2;
	}

	/**
	 * Checks if string matches a regular expression
	 * 
	 * @param value1  a string value to match
	 * @param value2 a regular expression string
	 * @return true if the value matches regular expression and false otherwise.
	 */
	public static boolean match(Object value1, Object value2) {
		if (value1 == null && value2 == null)
			return true;
		if (value1 == null || value2 == null)
			return false;

		String string1 = value1.toString();
		String string2 = value2.toString();
		return string1.matches(string2);
	}
}
