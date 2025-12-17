package dev.oribuin.fishing.util.model;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Confirmation<T> {

    private final Map<T, ConfirmTask> confirmed;
    private final Duration time; // The time until it times out 
    private final Consumer<T> timeout; // Functionality if not timed out 

    /**
     * Create a new confirmation page for the user
     *
     * @param time    The time until the confirmation expires
     * @param timeout The functionality when the confirmation has expired
     */
    public Confirmation(Duration time, Consumer<T> timeout) {
        this.time = time;
        this.timeout = timeout;
        this.confirmed = new HashMap<>();
    }

    /**
     * Create a new confirmation page for the user
     *
     * @param time The time until the confirmation expires
     */
    public Confirmation(Duration time) {
        this(time, t -> {});
    }

    /**
     * Create a new confirmation page for the user
     *
     * @param time    The number unit for the duration
     * @param unit    The unit of measurement for the duration
     * @param timeout The functionality when the confirmation has expired
     */
    public Confirmation(int time, TimeUnit unit, Consumer<T> timeout) {
        this(Duration.ofMillis(unit.toMillis(time)), timeout);
    }

    /**
     * Create a new confirmation page for the user
     *
     * @param time The number unit for the duration
     * @param unit The unit of measurement for the duration
     */
    public Confirmation(int time, TimeUnit unit) {
        this(Duration.ofMillis(unit.toMillis(time)));
    }

    /**
     * Create a timeout task for the user when they're done
     *
     * @param value The value to add to the confirmation
     */
    public void apply(T value) {
        this.confirmed.computeIfAbsent(value, t -> {
            ScheduledTask scheduled = PluginScheduler.get().runTaskLater(() -> {
                ConfirmTask task = this.confirmed.get(t);
                if (task == null || task.task().isCancelled()) return;
                boolean hasPassed = System.currentTimeMillis() - task.time() > this.time.toMillis();
                if (!hasPassed) return;

                this.timeout.accept(t);
                this.confirmed.remove(t);
            }, this.time.toSeconds(), TimeUnit.SECONDS);

            return new ConfirmTask(System.currentTimeMillis(), scheduled);
        });
    }

    /**
     * Confirm the action has been successful for the value
     *
     * @param value What is being confirmed
     *
     * @return true if the confirmation is valid
     */
    public boolean passed(T value) {
        ConfirmTask confirmed = this.confirmed.remove(value);
        if (confirmed == null) return false;

        confirmed.task().cancel(); // Cancel the task since its been confirmed
        return true;
    }

    private record ConfirmTask(long time, ScheduledTask task) {}
}
