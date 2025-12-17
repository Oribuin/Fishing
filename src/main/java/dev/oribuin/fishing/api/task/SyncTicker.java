package dev.oribuin.fishing.api.task;

import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


/**
 * Represents a repeating task that will run regularly with a specified delay
 * <p>
 * Usage: {@code public class MyObject implements SyncTicker {}}
 *
 * @see AsyncTicker The Asynchronous Ticker interface
 */
public interface SyncTicker {

    /**
     * The method that should run everytime the task is ticked,
     * this method will be run asynchronously
     */
    void tickSync();

    /**
     * The delay between each time the task is run
     *
     * @return The delay between each task
     */
    default Duration delay() {
        return Duration.ZERO;
    }

    /**
     * Schedule the task to run asynchronously with the specified delay
     *
     * @return The BukkitTask instance
     */
    default ScheduledTask schedule() {
        if (this.delay().toSeconds() == 0) return null; // If the delay is 0, return null

        return PluginScheduler.get().runTaskTimer(this::tickSync, this.delay().toSeconds(), 0, TimeUnit.SECONDS);
    }

}
