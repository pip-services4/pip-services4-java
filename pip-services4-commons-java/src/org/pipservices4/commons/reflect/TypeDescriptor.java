package org.pipservices4.commons.reflect;

import org.pipservices4.commons.errors.*;

/**
 * Descriptor that points to specific object type by it's name
 * and optional library (or module) where this type is defined.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 */
public class TypeDescriptor {
	private final String _name;
	private final String _library;

	/**
	 * Creates a new instance of the type descriptor and sets its values.
	 * 
	 * @param name    a name of the object type.
	 * @param library a library or module where this object type is implemented.
	 */
	public TypeDescriptor(String name, String library) {
		_name = name;
		_library = library;
	}

	/**
	 * Get the name of the object type.
	 * 
	 * @return the name of the object type.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Gets the name of the library or module where the object type is defined.
	 * 
	 * @return the name of the library or module.
	 */
	public String getLibrary() {
		return _library;
	}

	/**
	 * Compares this descriptor to a value. If the value is also a TypeDescriptor it
	 * compares their name and library fields. Otherwise this method returns false.
	 * 
	 * @param obj a value to compare.
	 * @return true if value is identical TypeDescriptor and false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TypeDescriptor) {
			TypeDescriptor otherType = (TypeDescriptor) obj;
			if (this.getName() == null || otherType.getName() == null)
				return false;
			if (!this.getName().equals(otherType.getName()))
				return false;
			return this.getLibrary() == null || otherType.getLibrary() == null
					|| this.getLibrary().equals(otherType.getLibrary());
		}

		return false;
	}

	/**
	 * Gets a string representation of the object. The result has format
	 * name[,library]
	 * 
	 * @return a string representation of the object.
	 * 
	 * @see #fromString(String)
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(_name);
		if (_library != null)
			builder.append(',').append(_library);
		return builder.toString();
	}

	/**
	 * Parses a string to get descriptor fields and returns them as a Descriptor.
	 * The string must have format name[,library]
	 * 
	 * @param value a string to parse.
	 * @return a newly created Descriptor.
	 * @throws ConfigException if the descriptor string is of a wrong format.
	 * 
	 * @see #toString()
	 */
	public static TypeDescriptor fromString(String value) throws ConfigException {
		if (value == null || value.length() == 0)
			return null;

		String[] tokens = value.split(",");
		if (tokens.length == 1) {
			return new TypeDescriptor(tokens[0].trim(), null);
		} else if (tokens.length == 2) {
			return new TypeDescriptor(tokens[0].trim(), tokens[1].trim());
		} else {
			throw (ConfigException) new ConfigException(null, "BAD_DESCRIPTOR",
					"Type descriptor " + value + " is in wrong format").withDetails("descriptor", value);
		}
	}

}
