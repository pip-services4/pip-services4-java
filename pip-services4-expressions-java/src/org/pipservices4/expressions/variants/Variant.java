package org.pipservices4.expressions.variants;

import org.pipservices4.commons.convert.StringConverter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines container for variant values.
 */
public class Variant {
    private VariantType _type;
    private Object _value;

    public static final Variant Empty = new Variant();

    /**
     * Constructs this class and assignes another variant value.
     *
     * @param value a value to be assigned to this variant.
     */
    public Variant(Object value) {
        this.setAsObject(value);
    }

    public Variant() {
    }

    /**
     * Gets a type of the variant value
     *
     * @return the variant value type
     */
    public VariantType getType() {
        return _type;
    }

    /**
     * Gets variant value as integer
     *
     * @return the variant value type
     */
    public Integer getAsInteger() {
        return (Integer) _value;
    }

    /**
     * Sets variant value as integer
     *
     * @param value a value to be set
     */
    public void setAsInteger(Integer value) {
        this._type = VariantType.Integer;
        this._value = value;
    }

    /**
     * Gets variant value as long
     *
     * @return the variant value type
     */
    public Long getAsLong() {
        return (Long) _value;
    }

    /**
     * Sets variant value as long
     *
     * @param value the variant value type
     */
    public void setAsLong(Long value) {
        this._type = VariantType.Long;
        _value = value;
    }

    /**
     * Gets variant value as boolean
     *
     * @return the variant value type
     */
    public Boolean getAsBoolean() {
        return (Boolean) _value;
    }

    /**
     * Sets variant value as boolean
     *
     * @param value the variant value type
     */
    public void setAsBoolean(Boolean value) {
        this._type = VariantType.Boolean;
        _value = value;
    }

    /**
     * Gets variant value as float
     *
     * @return the variant value type
     */
    public Float getAsFloat() {
        return (Float) this._value;
    }

    /**
     * Sets variant value as float
     *
     * @param value the variant value type
     */
    public void setAsFloat(Float value) {
        this._type = VariantType.Float;
        this._value = value;
    }

    /**
     * Gets variant value as double
     */
    public Double getAsDouble() {
        return (Double) this._value;
    }

    /**
     * Sets variant value as double
     *
     * @param value a value to be set
     */
    public void setAsDouble(Double value) {
        this._type = VariantType.Double;
        this._value = value;
    }

    /**
     * Gets variant value as string
     */
    public String getAsString() {
        return (String) this._value;
    }

    /**
     * Sets variant value as string
     *
     * @param value a value to be set
     */
    public void setAsString(String value) {
        this._type = VariantType.String;
        this._value = value;
    }

    /**
     * Gets variant value as DateTime
     */
    public ZonedDateTime getAsDateTime() {
        return (ZonedDateTime) this._value;
    }

    /**
     * Sets variant value as DateTime
     *
     * @param value a value to be set
     */
    public void setAsDateTime(ZonedDateTime value) {
        this._type = VariantType.DateTime;
        this._value = value;
    }

    /**
     * Gets variant value as TimeSpan
     */
    public Long getAsTimeSpan() {
        return (Long) this._value;
    }

    /**
     * Sets variant value as TimeSpan
     *
     * @param value a value to be set
     */
    public void setAsTimeSpan(Long value) {
        this._type = VariantType.TimeSpan;
        this._value = value;
    }

    /**
     * Gets variant value as Object
     */
    public Object getAsObject() {
        return this._value;
    }

    /**
     * Sets variant value as Object
     *
     * @param value a value to be set
     */
    public void setAsObject(Object value) {
        this._value = value;

        if (value == null)
            this._type = VariantType.Null;
        else if (value instanceof Integer)
            this._type = VariantType.Integer;
        else if (value instanceof Long)
            this._type = VariantType.Long;
        else if (value instanceof Double)
            this._type = VariantType.Double;
        else if (value instanceof Boolean)
            this._type = VariantType.Boolean;
        else if (value instanceof ZonedDateTime)
            this._type = VariantType.DateTime;
        else if (value instanceof String)
            this._type = VariantType.String;
        else if (value instanceof List) {
            this._type = VariantType.Array;
        } else if (value instanceof Variant) {
            this._type = ((Variant) value)._type;
            this._value = ((Variant) value)._value;
        } else
            this._type = VariantType.Object;
    }

    /**
     * Gets variant value as variant array
     */
    public List<Variant> getAsArray() {
        return (List<Variant>) this._value;
    }

    /**
     * Sets variant value as variant array
     *
     * @param value a value to be set
     */
    public void setAsArray(List<Variant> value) {
        this._type = VariantType.Array;
        if (value != null) {
            this._value = new ArrayList<>(value);
        } else {
            this._value = null;
        }
    }

    /**
     * Gets length of the array
     *
     * @return The length of the array or 0
     */
    public Integer getLength() {
        if (this._type == VariantType.Array) {
            return this._value instanceof List ? ((List<?>) this._value).size() : 0;
        }
        return 0;
    }

    /**
     * Sets a new array length
     *
     * @param value a new array length
     */
    public void setLength(int value) throws Exception {
        if (this._type == VariantType.Array) {

            while (((List<?>) this._value).size() < value)
                ((List<?>) this._value).add(null);
        } else {
            throw new Exception("Cannot set array length for non-array data type.");
        }
    }

    /**
     * Gets an array element by its index.
     *
     * @param index an element index
     * @return a requested array element
     */
    public Variant getByIndex(int index) throws Exception {
        if (this._type == VariantType.Array) {
            if (this._value instanceof List && ((List<Variant>) this._value).size() > index) {
                return ((List<Variant>) this._value).get(index);
            }
            throw new Exception("Requested element of array is not accessible.");
        }
        throw new Exception("Cannot access array element for none-array data type.");
    }

    /**
     * Sets an array element by its index.
     *
     * @param index   an element index
     * @param element an element value
     */
    public void setByIndex(int index, Variant element) throws Exception {
        if (this._type == VariantType.Array) {
            if (this._value instanceof List) {
                while (((List<?>) this._value).size() <= index) {
                    ((List<?>) this._value).add(null);
                }

                ((List<Variant>) this._value).set(index, element);
            } else {
                throw new Exception("Requested element of array is not accessible.");
            }
        } else {
            throw new Exception("Cannot access array element for none-array data type.");
        }
    }

    /**
     * Checks is this variant value Null.
     *
     * @return <code>true</code> if this variant value is Null.
     */
    public boolean isNull() {
        return this._type == VariantType.Null;
    }

    /**
     * Checks is this variant value empty.
     *
     * @return <code>true</code< is this variant value is empty.
     */
    public boolean isEmpty() {
        return this._value == null;
    }

    /**
     * Assignes a new value to this object.
     *
     * @param value A new value to be assigned.
     */
    public void assign(Variant value) {
        if (value != null) {
            this._type = value._type;
            this._value = value._value;
        } else {
            this._type = VariantType.Null;
            this._value = null;
        }
    }

    /**
     * Clears this object and assignes a VariantType.Null type.
     */
    public void clear() {
        this._type = VariantType.Null;
        this._value = null;
    }

    /**
     * Returns a string value for this object.
     *
     * @return a string value for this object.
     */
    public String toString() {
        return this._value == null ? "null" : StringConverter.toString(this._value);
    }

    /**
     * Compares this object to the specified one.
     *
     * @param obj An object to be compared.
     * @return <code>true</code> if objects are equal.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Variant varObj) {
            Object value1 = this._value;
            Object value2 = varObj._value;
            if (value1 == null || value2 == null) {
                return value1 == value2;
            }
            return (this._type == varObj._type) && (value1 == value2);
        }
        return false;
    }

    /**
     * Cloning the variant value
     *
     * @return The cloned value of this variant
     */
    public Variant clone() {
        return new Variant(this);
    }

    /**
     * Creates a new variant from Integer value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromInteger(Integer value) {
        Variant result = new Variant();
        result.setAsInteger(value);
        return result;
    }

    /**
     * Creates a new variant from Long value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromLong(Long value) {
        Variant result = new Variant();
        result.setAsLong(value);
        return result;
    }

    /**
     * Creates a new variant from Boolean value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromBoolean(boolean value) {
        Variant result = new Variant();
        result.setAsBoolean(value);
        return result;
    }

    /**
     * Creates a new variant from Float value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromFloat(Float value) {
        Variant result = new Variant();
        result.setAsFloat(value);
        return result;
    }

    /**
     * Creates a new variant from Double value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromDouble(Double value) {
        Variant result = new Variant();
        result.setAsDouble(value);
        return result;
    }

    /**
     * Creates a new variant from String value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromString(String value) {
        Variant result = new Variant();
        result.setAsString(value);
        return result;
    }

    /**
     * Creates a new variant from DateTime value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromDateTime(ZonedDateTime value) {
        Variant result = new Variant();
        result.setAsDateTime(value);
        return result;
    }

    /**
     * Creates a new variant from TimeSpan value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromTimeSpan(Long value) {
        Variant result = new Variant();
        result.setAsTimeSpan(value);
        return result;
    }

    /**
     * Creates a new variant from Object value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromObject(Object value) {
        Variant result = new Variant();
        result.setAsObject(value);
        return result;
    }

    /**
     * Creates a new variant from Array value.
     *
     * @param value a variant value.
     * @return a created variant object.
     */
    public static Variant fromArray(List<Variant> value) {
        Variant result = new Variant();
        result.setAsArray(value);
        return result;
    }
}
