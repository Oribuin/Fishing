package xyz.oribuin.fishing.fish.condition;

import org.bukkit.Location;
import org.bukkit.World;

// TODO: Add snowing condition
public enum Weather {
    CLEAR,
    RAIN,
    STORM;
    //    SNOW;

    /**
     * Get the weather in the location provided
     *
     * @param location The location to check the weather
     *
     * @return The weather in the location
     */
    public static Weather test(Location location) {
        if (location == null || location.getWorld() == null) return Weather.CLEAR;

        World world = location.getWorld();
        if (!world.hasStorm()) return Weather.CLEAR;
        if (world.isThundering()) return Weather.STORM;

        return Weather.RAIN;
    }

}
