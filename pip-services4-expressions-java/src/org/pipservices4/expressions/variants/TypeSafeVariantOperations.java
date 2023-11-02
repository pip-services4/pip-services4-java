package org.pipservices4.expressions.variants;

public class TypeSafeVariantOperations extends AbstractVariantOperations {
    /**
     * Converts variant to specified type
     *
     * @param value   A variant value to be converted.
     * @param newType A type of object to be returned.
     * @return A converted Variant value.
     */
    @Override
    public Variant convert(Variant value, VariantType newType) {
        if (newType == VariantType.Null)
            return new Variant();

        if (newType == value.getType() || newType == VariantType.Object)
            return value;


        switch (value.getType()) {
            case Integer:
                return this.convertFromInteger(value, newType);
            case Long:
                return this.convertFromLong(value, newType);
            case Float:
                return this.convertFromFloat(value, newType);
            case Double:
            case String:
            case Boolean:
            case Array:
                break;
            case Object:
                return value;
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromInteger(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Long -> {
                result.setAsLong(value.getAsInteger().longValue());
                return result;
            }
            case Float -> {
                result.setAsFloat(value.getAsInteger().floatValue());
                return result;
            }
            case Double -> {
                result.setAsDouble(value.getAsInteger().doubleValue());
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromLong(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Float -> {
                result.setAsFloat(value.getAsLong().floatValue());
                return result;
            }
            case Double -> {
                result.setAsDouble(value.getAsLong().doubleValue());
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromFloat(Variant value, VariantType newType) {
        var result = new Variant();
        if (newType == VariantType.Double) {
            result.setAsDouble(value.getAsFloat().doubleValue());
            return result;
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }
}
