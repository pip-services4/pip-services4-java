package org.pipservices4.expressions.variants;

import org.pipservices4.commons.convert.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Implements a type unsafe variant operations manager object.
 */
public class TypeUnsafeVariantOperations extends AbstractVariantOperations {
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

        if (newType == VariantType.String) {
            var result = new Variant();
            result.setAsString(StringConverter.toString(value.getAsObject()));
            return result;
        }

        switch (value.getType()) {
            case Null:
                return this.convertFromNull(newType);
            case Integer:
                return this.convertFromInteger(value, newType);
            case Long:
                return this.convertFromLong(value, newType);
            case Float:
                return this.convertFromFloat(value, newType);
            case Double:
                return this.convertFromDouble(value, newType);
            case DateTime:
                return this.convertFromDateTime(value, newType);
            case TimeSpan:
                return this.convertFromTimeSpan(value, newType);
            case String:
                return this.convertFromString(value, newType);
            case Boolean:
                return this.convertFromBoolean(value, newType);
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromNull(VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(0);
                return result;
            }
            case Long -> {
                result.setAsLong(0L);
                return result;
            }
            case Float -> {
                result.setAsFloat(0F);
                return result;
            }
            case Double -> {
                result.setAsDouble(0D);
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(false);
                return result;
            }
            case DateTime -> {
                result.setAsDateTime(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")));
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(0L);
                return result;
            }
            case String -> {
                result.setAsString("null");
                return result;
            }
            case Object -> {
                result.setAsObject(null);
                return result;
            }
            case Array -> {
                result.setAsArray(null);
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from Null "
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
            case DateTime -> {
                result.setAsDateTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(value.getAsInteger().longValue()), ZoneId.of("UTC")));
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(value.getAsInteger().longValue());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value.getAsInteger() != 0);
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromLong(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(value.getAsLong().intValue());
                return result;
            }
            case Float -> {
                result.setAsFloat(value.getAsLong().floatValue());
                return result;
            }
            case Double -> {
                result.setAsDouble(value.getAsLong().doubleValue());
                return result;
            }
            case DateTime -> {
                result.setAsDateTime(ZonedDateTime.ofInstant(Instant.ofEpochSecond(value.getAsLong()), ZoneId.of("UTC")));
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(value.getAsLong());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value.getAsLong() != 0);
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromFloat(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(value.getAsFloat().intValue());
                return result;
            }
            case Long -> {
                result.setAsLong((long) value.getAsFloat().intValue());
                return result;
            }
            case Double -> {
                result.setAsDouble(value.getAsFloat().doubleValue());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value.getAsFloat() != 0);
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromDouble(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(value.getAsDouble().intValue());
                return result;
            }
            case Long -> {
                result.setAsLong(value.getAsDouble().longValue());
                return result;
            }
            case Float -> {
                result.setAsFloat(value.getAsDouble().floatValue());
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(value.getAsDouble() != 0);
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromString(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(IntegerConverter.toInteger(value.getAsString()));
                return result;
            }
            case Long -> {
                result.setAsLong(LongConverter.toLong(value.getAsString()));
                return result;
            }
            case Float -> {
                result.setAsFloat(FloatConverter.toFloat(value.getAsString()));
                return result;
            }
            case Double -> {
                result.setAsDouble(DoubleConverter.toDouble(value.getAsString()));
                return result;
            }
            case DateTime -> {
                result.setAsDateTime(DateTimeConverter.toDateTime(value.getAsString()));
                return result;
            }
            case TimeSpan -> {
                result.setAsTimeSpan(LongConverter.toLong(value.getAsString()));
                return result;
            }
            case Boolean -> {
                result.setAsBoolean(BooleanConverter.toBoolean(value.getAsString()));
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromBoolean(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(value.getAsBoolean() ? 1 : 0);
                return result;
            }
            case Long -> {
                result.setAsLong(value.getAsBoolean() ? 1L : 0L);
                return result;
            }
            case Float -> {
                result.setAsFloat(value.getAsBoolean() ? 1F : 0F);
                return result;
            }
            case Double -> {
                result.setAsDouble(value.getAsBoolean() ? 1D : 0D);
                return result;
            }
            case String -> {
                result.setAsString(value.getAsBoolean() ? "true" : "false");
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromDateTime(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger((int) value.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
            case Long -> {
                result.setAsLong(value.getAsDateTime().toInstant().toEpochMilli());
                return result;
            }
            case String -> {
                result.setAsString(StringConverter.toString(value.getAsDateTime()));
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }

    private Variant convertFromTimeSpan(Variant value, VariantType newType) {
        var result = new Variant();
        switch (newType) {
            case Integer -> {
                result.setAsInteger(value.getAsTimeSpan().intValue());
                return result;
            }
            case Long -> {
                result.setAsLong(value.getAsTimeSpan());
                return result;
            }
            case String -> {
                result.setAsString(StringConverter.toString(value.getAsTimeSpan()));
                return result;
            }
        }
        throw new UnsupportedOperationException("Variant convertion from " + this.typeToString(value.getType())
                + " to " + this.typeToString(newType) + " is not supported.");
    }
}
