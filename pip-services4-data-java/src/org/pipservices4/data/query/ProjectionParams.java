package org.pipservices4.data.query;

import org.pipservices4.commons.data.AnyValueArray;

import java.io.Serial;
import java.util.*;

/**
 * Defines projection parameters with list if fields to include into query results.
 * <p>
 * The parameters support two formats: dot format and nested format.
 * <p>
 * The dot format is the standard way to define included fields and subfields using
 * dot object notation: <code>"field1,field2.field21,field2.field22.field221"</code>
 * <p>
 * As alternative the nested format offers a more compact representation:
 * <code>"field1,field2(field21,field22(field221))"</code>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * FilterParams filter = FilterParams.fromTuples("type", "Type1");
 * PagingParams paging = new PagingParams(0, 100);
 * ProjectionParams projection = ProjectionParams.fromString("field1,field2(field21,field22)")
 * 
 * myDataClient.getDataByFilter(filter, paging, projection);
 * }
 * </pre>
 */
public class ProjectionParams extends ArrayList<String> {

	@Serial
	private static final long serialVersionUID = 5876557837753631885L;
	private static final char defaultDelimiter = ',';

	public ProjectionParams() {
	}

	/**
     * Creates a new instance of the projection parameters and assigns its value.
     * 
     * @param values     (optional) values to initialize this object.
     */
	public ProjectionParams(String[] values) {
		if (values != null) {
			this.addAll(Arrays.asList(values));
		}
	}

	/**
     * Creates a new instance of the projection parameters and assigns its value.
     * 
     * @param array     (optional) values to initialize this object.
     */
	public ProjectionParams(AnyValueArray array) {
		if (array == null) {
			return;
		}

		for (int index = 0; index < array.size(); index++) {
			String value = array.getAsString(index);
			if (value != null && !value.isEmpty()) {
				add(value);
			}
		}
	}

	/**
     * Converts specified value into ProjectionParams.
     * 
     * @param value     value to be converted
     * @return         a newly created ProjectionParams.
     * 
     * @see AnyValueArray#fromValue(Object)
     */
	public static ProjectionParams fromValue(Object value) {
		if (value instanceof ProjectionParams) //// value.getClass() == ProjectionParams.class
		{
			return (ProjectionParams) value;
		}

		AnyValueArray array = value != null ? AnyValueArray.fromValue(value) : new AnyValueArray();
		return new ProjectionParams(array);
	}

	/**
     * Parses comma-separated list of projection fields.
     * 
     * @param values    one or more comma-separated lists of projection fields
     * @return         a newly created ProjectionParams.
     */
	public static ProjectionParams fromValues(String... values) {
		return fromValues(defaultDelimiter, values);
	}

	/**
     * Parses comma-separated list of projection fields.
     * 
     * @param delimiter a certain type of delimiter
     * @param values    one or more comma-separated lists of projection fields
     * @return         a newly created ProjectionParams.
     */
	public static ProjectionParams fromValues(char delimiter, String... values) {
		return new ProjectionParams(parse(delimiter, values));
	}

	/** 
     * Gets a string representation of the object.
     * The result is a comma-separated list of projection fields
     * "field1,field2.field21,field2.field22.field221"
     * 
     * @return a string representation of the object.
     */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < this.size(); index++) {
			if (index > 0) {
				builder.append(',');
			}

			builder.append(super.get(index));
		}
		return builder.toString();
	}

	private static String[] parse(char delimiter, String[] values) {
		List<String> result = new ArrayList<>();
		String prefix = "";

		for (String value : values) {
			parseValue(prefix, result, value.trim(), delimiter);
		}
		return result.toArray(new String[result.size()]);
	}

	private static void parseValue(String prefix, List<String> result, String value, char delimiter) {
		value = value.trim();

		int openBracket = 0;
		int openBracketIndex = -1;
		int closeBracketIndex = -1;
		int commaIndex = -1;

		boolean breakCycleRequired = false;

		for (int index = 0; index < value.length(); index++) {
			Character valueChar = value.charAt(index);
			if (valueChar.equals('(')) {
				if (openBracket == 0) {
					openBracketIndex = index;
				}

				openBracket++;
			} else if (valueChar.equals(')')) {
				openBracket--;

				if (openBracket == 0) {
					closeBracketIndex = index;

					if (openBracketIndex >= 0 && closeBracketIndex > 0) {
						String previousPrefix = prefix;

						if (prefix != null && !prefix.isEmpty()) {
							prefix = prefix + "." + value.substring(0, openBracketIndex);
						} else {
							prefix = value.substring(0, openBracketIndex);
						}

						String subValue = value.substring(openBracketIndex + 1, closeBracketIndex);

						parseValue(prefix, result, subValue, delimiter);

						subValue = value.substring(closeBracketIndex + 1);
						parseValue(previousPrefix, result, subValue, delimiter);
						breakCycleRequired = true;
					}
				}
			} else if (valueChar.equals(delimiter)) {
				if (openBracket == 0) {
					commaIndex = index;

					String subValue = value.substring(0, commaIndex);
					if (subValue != null && !subValue.isEmpty()) {
						if (prefix != null && !prefix.isEmpty()) {
							result.add(prefix + "." + subValue);
						} else {
							result.add(subValue);
						}
					}

					subValue = value.substring(commaIndex + 1);

					if (subValue != null && !subValue.isEmpty()) {
						parseValue(prefix, result, subValue, delimiter);
						breakCycleRequired = true;
					}
				}
			}

			if (breakCycleRequired) {
				break;
			}
		}

		if (value != null && !value.isEmpty() && openBracketIndex == -1 && commaIndex == -1) {
			if (prefix != null && !prefix.isEmpty()) {
				result.add(prefix + "." + value);
			} else {
				result.add(value);
			}
		}
	}
}
