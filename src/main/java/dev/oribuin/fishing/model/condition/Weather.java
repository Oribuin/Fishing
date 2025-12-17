package dev.oribuin.fishing.model.condition;

import org.bukkit.Location;
import org.bukkit.World;

// TODO: Add snowing condition
public enum Weather {
    ANY,
    CLEAR,
    RAIN,
    STORM;
    //    SNOW;

    /**
     * Check if the location is in the state of the weather
     *
     * @param location The location to check
     *
     * @return If the location is in the state of the weather
     */
    public boolean isState(Location location) {
        return this == test(location);
    }

    /**
     * Get the weather in the location provided
     *
     * @param location The location to check the weather
     *
     * @return The weather in the location
     */
    public static Weather test(Location location) {
        if (location == null || location.getWorld() == null) return Weather.ANY;

        World world = location.getWorld();
        if (!world.hasStorm()) return Weather.CLEAR;
        if (world.isThundering()) return Weather.STORM;

        return Weather.RAIN;
    }

}
