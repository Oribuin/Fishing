package dev.oribuin.fishing.fish.condition;

import org.bukkit.World;

public enum Time {
    ANY_TIME(0, 24),
    DAY(6, 18),
    NIGHT(18, 6),
    SUNSET(18, 20),
    SUNRISE(6, 8),
    ;

    private final int lowerBound;
    private final int upperBound;

    /**
     * Create a new time with a lower and upper bound, This uses 24 hour time
     *
     * @param lowerBound The lower bound of the time
     * @param upperBound The upper bound of the time
     */
    Time(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound * 1000;
        this.upperBound = upperBound * 1000;
    }

    /**
     * Make sure the world time is within the bounds of the time
     *
     * @param world The world to check the time of
     *
     * @return If the time is within the bounds
     */
    public boolean matches(World world) {
        if (this == ANY_TIME)
            return true;

        return world.getTime() >= this.lowerBound && world.getTime() <= this.upperBound;
    }

}
