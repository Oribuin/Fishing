package dev.oribuin.fishing.model.condition;

import org.bukkit.World;

/**
 * Create a new custom time parameter
 *
 * @param startTime  The start time for it
 * @param finishTime The finish time for it
 */
public record Time(int startTime, int finishTime) {

    /**
     * Should be readable from the following
     * 6-12
     * 6pm-12pm
     * 6pm-12
     * 630-12pm
     */
    public static Time from(String start, String finish) {
        return new Time(0, 24);
    }

    /**
     * Make sure the world time is within the bounds of the time
     *
     * @param world The world to check the time of
     *
     * @return If the time is within the bounds
     */
    public boolean matches(World world) {
        if (this.startTime == 0 && this.finishTime == 24)
            return true;

        return world.getTime() >= this.startTime && world.getTime() <= this.finishTime;
    }

    /**
     * Make sure the hour is within the bounds of the time
     *
     * @param hour The hour to check
     *
     * @return If the time is within the bounds
     */
    public boolean matches(int hour) {
        if (this.startTime == 0 && this.finishTime == 24)
            return true;

        return hour >= this.startTime && hour <= this.finishTime;
    }

}
