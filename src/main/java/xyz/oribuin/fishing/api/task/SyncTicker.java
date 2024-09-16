package xyz.oribuin.fishing.api.task;

import java.time.Duration;

public interface SyncTicker {

    /**
     * The method that should run everytime the task is ticked,
     * this method will be ran synchronously
     */
    void tickAsync();

    /**
     * The delay between each tick, Set to Duration#ZERO for no delay
     *
     * @return The delay between each tick
     */
    default Duration delay() {
        return Duration.ZERO;
    }

}
