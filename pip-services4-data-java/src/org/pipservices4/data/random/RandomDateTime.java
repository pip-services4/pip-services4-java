package org.pipservices4.data.random;

import java.time.*;

/**
 * Random generator for Date time values.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ZonedDateTime value1 = RandomDateTime.nextDate(2010, 0);    // Possible result: 2008-01-03
 * ZonedDateTime value2 = RandomDateTime.nextDateTime(2017, 0);// Possible result: 20017-03-11 11:20:32
 * }
 * </pre>
 */
public class RandomDateTime {
    /**
     * Generates a random ZonedDateTime in the range ['minYear', 'maxYear']. This
     * method generate dates without time (or time set to 00:00:00)
     *
     * @param minYear (optional) minimum range value
     * @param maxYear max range value
     * @return a random ZonedDateTime value.
     */
    public static ZonedDateTime nextDate(int minYear, int maxYear) {
        int currentYear = ZonedDateTime.now().getYear();
        minYear = minYear == 0 ? currentYear - RandomInteger.nextInteger(10) : minYear;
        maxYear = maxYear == 0 ? currentYear : maxYear;

        int year = RandomInteger.nextInteger(minYear, maxYear);
        int month = RandomInteger.nextInteger(1, 13);
        int day = RandomInteger.nextInteger(1, 32);

        if (month == 2)
            day = Math.min(28, day);
        else if (month == 4 || month == 6 || month == 9 || month == 11)
            day = Math.min(30, day);
        return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("UTC"));
    }

//    public static TimeSpan nextTime() {
//        int hour = RandomInteger.nextInteger(0, 24);
//        int min = RandomInteger.nextInteger(0, 60);
//        int sec = RandomInteger.nextInteger(0, 60);
//        int millis = RandomInteger.nextInteger(0, 1000);
//
//        return new TimeSpan(hour, min, sec, millis);
//    }

//    public static ZonedDateTime nextDateTime() {
//    	return nextDateTime(0, 0);
//    }
//
//    public static ZonedDateTime nextDateTime(int year) {
//    	return nextDateTime(year, year);
//    }

    /**
     * Generates a random ZonedDateTime and time in the range ['minYear',
     * 'maxYear']. This method generate dates without time (or time set to 00:00:00)
     *
     * @param minYear (optional) minimum range value
     * @param maxYear max range value
     * @return a random ZonedDateTime and time value.
     */
    public static ZonedDateTime nextDateTime(int minYear, int maxYear) {
        return nextDate(minYear, maxYear).plusSeconds(RandomInteger.nextInteger(3600 * 24 * 365));
    }

//    public static T nextEnum<T>() {
//        var enumType = typeof(T);
//        var values = enumType.GetEnumValues();
//        var index = Integer(values.Length);
//        return (T)values.GetValue(index);
//    }

    /**
     * Updates (drifts) a ZonedDateTime value.
     *
     * @param value a ZonedDateTime value to drift.
     * @return an updated ZonedDateTime and time value.
     */
    public static ZonedDateTime updateDateTime(ZonedDateTime value) {
        return updateDateTime(value, 0);
    }

    /**
     * Updates (drifts) a ZonedDateTime value within specified range defined
     *
     * @param value a ZonedDateTime value to drift.
     * @param range (optional) a range in milliseconds. Default: 10 days
     * @return an updated ZonedDateTime and time value.
     */
    public static ZonedDateTime updateDateTime(ZonedDateTime value, float range) {
        range = range != 0 ? range : 10;
        if (range < 0)
            return value;

        float days = RandomFloat.nextFloat(-range, range);
        return value.plusDays((int) days);
    }

}
