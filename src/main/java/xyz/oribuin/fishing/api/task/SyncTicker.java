package xyz.oribuin.fishing.api.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.fishing.FishingPlugin;

import java.time.Duration;


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
    default BukkitTask schedule() {
        if (this.delay().toSeconds() == 0) return null; // If the delay is 0, return null

        return Bukkit.getScheduler().runTaskTimer(FishingPlugin.get(), this::tickSync, 0L, this.delay().toSeconds() * 20L);
    }

}
