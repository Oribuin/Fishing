package dev.oribuin.fishing.util;

import dev.oribuin.fishing.FishingPlugin;
import dev.rosewood.rosegarden.scheduler.RoseScheduler;
import dev.rosewood.rosegarden.scheduler.task.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper class for bukkit tasks that makes cancelling tasks easier and less ugly!
 */
public class PluginTask {

    private ScheduledTask task;
    private boolean async;
    private long startTime;

    /**
     * Create a new plugin task instance with a task and whether it is async or not
     *
     * @param task  The task to run
     * @param async Whether the task is async or not
     */
    public PluginTask(ScheduledTask task, boolean async, long startTime) {
        this.task = task;
        this.async = async;
        this.startTime = startTime;
    }

    /**
     * Create a new empty plugin task instance with no task
     */
    public static PluginTask empty() {
        return new PluginTask(null, false, 0);
    }

    /**
     * Schedule a repeating task with a delay and period in a given time unit
     *
     * @param runnable The task to run
     * @param unit     The time unit to use
     * @param delay    The delay to run the task after
     * @param period   The period to run the task at
     * @param async    Whether the task is async or not
     * @return The plugin task instance
     */
    public static PluginTask scheduleRepeating(Runnable runnable, TimeUnit unit, long delay, long period, boolean async) {
        RoseScheduler scheduler = FishingPlugin.get().getScheduler();
        
        ScheduledTask scheduledTask = async
                ? scheduler.runTaskTimerAsync(runnable, delay, period, unit)
                : scheduler.runTaskTimer(runnable, delay, period, unit);

        // Create a new plugin task instance
        return new PluginTask(scheduledTask, async, System.currentTimeMillis() + unit.toMillis(delay));
    }

    /**
     * Schedule a repeating task with a delay and period in a given time unit
     *
     * @param runnable The task to run
     * @param unit     The time unit to use
     * @param delay    The delay to run the task after
     * @param period   The period to run the task at
     * @return The plugin task instance
     */
    public static PluginTask scheduleRepeating(Runnable runnable, TimeUnit unit, long delay, long period) {
        return scheduleRepeating(runnable, unit, delay, period, false);
    }

    /**
     * Schedule a repeating task with a period in a given time unit and whether it is async or not
     *
     * @param runnable The task to run
     * @param period   The period to run the task at
     * @param async    Whether the task is async or not
     * @return The plugin task instance
     */
    public static PluginTask scheduleRepeating(Runnable runnable, Duration period, boolean async) {
        return scheduleRepeating(runnable, TimeUnit.SECONDS, 0L, period.toMillis(), async);
    }

    /**
     * Schedule a repeating task with a period in a given time unit
     *
     * @param runnable The task to run
     * @param period   The period to run the task at
     * @return The plugin task instance
     */
    public static PluginTask scheduleRepeating(Runnable runnable, Duration period) {
        return scheduleRepeating(runnable, period, false);
    }

    /**
     * Create a new plugin task instance with a task and whether it is async or not
     *
     * @param runnable The task to run
     * @param unit     The time unit to use
     * @param delay    The delay to run the task after
     * @param async    Whether the task is async or not
     * @return The plugin task instance
     */
    public static PluginTask scheduleDelayed(Runnable runnable, TimeUnit unit, long delay, boolean async) {
        RoseScheduler scheduler = FishingPlugin.get().getScheduler();

        ScheduledTask scheduledTask = async
                ? scheduler.runTaskLaterAsync(runnable, delay, unit)
                : scheduler.runTaskLater(runnable, delay, unit);

        // Create a new plugin task instance
        return new PluginTask(scheduledTask, async, System.currentTimeMillis() + unit.toMillis(delay));
    }

    /**
     * Create a new synchronous plugin task instance with a task
     *
     * @param runnable The task to run
     * @param unit     The time unit to use
     * @param delay    The delay to run the task after
     * @return The plugin task instance
     */
    public static PluginTask scheduleDelayed(Runnable runnable, TimeUnit unit, long delay) {
        return scheduleDelayed(runnable, unit, delay, false);
    }

    /**
     * Create a new synchronous plugin task instance with a task and a duration
     *
     * @param runnable The task to run
     * @param duration The duration to run the task after
     * @param async    Whether the task is async or not
     * @return The plugin task instance
     */
    public static PluginTask scheduleDelayed(Runnable runnable, Duration duration, boolean async) {
        return scheduleDelayed(runnable, TimeUnit.SECONDS, duration.toMillis(), async);
    }

    /**
     * Create a new synchronous plugin task instance with a task and a duration
     *
     * @param runnable The task to run
     * @param duration The duration to run the task after
     * @return The plugin task instance
     */
    public static PluginTask scheduleDelayed(Runnable runnable, Duration duration) {
        return scheduleDelayed(runnable, duration, false);
    }

    /**
     * Check if the task is running and cancel it if it is
     *
     * @return Whether the task was cancelled or not
     */
    public boolean isRunning() {
        return task != null;
    }

    /**
     * Check if the task is running and cancel it if it is
     *
     * @return Whether the task was cancelled or not
     */
    public boolean isFinished() {
        if (this.task == null) return true; // Task is not running
        if (this.task.isCancelled()) return true; // Task is cancelled

        if (System.currentTimeMillis() - this.startTime > 0) {
            this.cancel();
            return true;
        }

        return false;
    }

    /**
     * Cancel the task if it is running
     */
    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.async = false;
        this.startTime = 0;
    }

    /**
     * Get the time the task was started
     *
     * @return The time the task was started
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get the time the task has been running
     *
     * @return The time the task has been running
     */
    public long getRunningTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Get the time the task has been running in a given time unit
     *
     * @param unit The time unit to use
     * @return The time the task has been running
     */
    public long getRunningTime(TimeUnit unit) {
        return unit.convert(getRunningTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * Get the time the task has been running as a duration
     *
     * @return The time the task has been running
     */
    public Duration getRunningDuration() {
        return Duration.ofMillis(getRunningTime());
    }

    /**
     * Check if the task is async
     *
     * @return Whether the task is async or not
     */
    public boolean isAsync() {
        return async;
    }

}
