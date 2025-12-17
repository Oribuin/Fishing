package dev.oribuin.fishing.scheduler.task;


import dev.oribuin.fishing.FishingPlugin;

public interface ScheduledTask {

    /**
     * Cancels this task
     */
    void cancel();

    /**
     * @return true if this task is cancelled, false otherwise
     */
    boolean isCancelled();

    /**
     * @return the plugin that scheduled this task
     */
    FishingPlugin getOwningPlugin();

    /**
     * @return true if this task is running, false otherwise
     */
    boolean isRunning();

    /**
     * @return true if this task is repeating, false otherwise
     */
    boolean isRepeating();

}
