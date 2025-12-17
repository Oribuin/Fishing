package dev.oribuin.fishing.util.math;

import java.util.concurrent.TimeUnit;

public final class NumberUtil {

    /**
     * Converts a long value and TimeUnit to a value in ticks
     *
     * @param value    The value
     * @param timeUnit The TimeUnit
     *
     * @return the value in ticks, rounded to the nearest tick
     */
    public static long timeUnitToTicks(long value, TimeUnit timeUnit) {
        return Math.round(timeUnit.toMillis(value) / 50.0);
    }

    /**
     * Convert a Double into a String with maximum 2 decimal places
     *
     * @param value The value
     *
     * @return The double as a String
     */
    public static String rounded(Double value) {
        return rounded(value, 2);
    }
    
    /**
     * Convert a Double into a String with maximum 2 decimal places
     *
     * @param value The value
     *
     * @return The double as a String
     */
    public static String rounded(Float value) {
        return String.valueOf(value).substring(0, 2);
    }

    /**
     * Convert a Double into a String with a specified amount of decimal places
     *
     * @param value The value
     *
     * @return The double as a String
     */
    public static String rounded(Double value, int decimals) {
        if (decimals == 0) return String.valueOf((int) Math.round(value));

        String format = "%." + decimals + "f"; // %.2f
        return String.format(format, value);
    }

}
