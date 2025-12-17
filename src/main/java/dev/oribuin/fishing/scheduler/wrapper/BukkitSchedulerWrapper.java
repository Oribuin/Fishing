package dev.oribuin.fishing.scheduler.wrapper;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.scheduler.task.BukkitScheduledTask;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.util.math.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class BukkitSchedulerWrapper implements SchedulerWrapper {

    private final FishingPlugin javaPlugin;
    private final BukkitScheduler scheduler;

    public BukkitSchedulerWrapper(FishingPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.scheduler = Bukkit.getScheduler();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public boolean isLocationThread(Location location) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public ScheduledTask runTask(Runnable runnable) {
        return wrap(this.scheduler.runTask(this.javaPlugin, runnable));
    }

    @Override
    public ScheduledTask runTaskAsync(Runnable runnable) {
        return wrap(this.scheduler.runTaskAsynchronously(this.javaPlugin, runnable));
    }

    @Override
    public ScheduledTask runTaskLater(Runnable runnable, long delay) {
        return wrap(this.scheduler.runTaskLater(this.javaPlugin, runnable, delay));
    }

    @Override
    public ScheduledTask runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(this.scheduler.runTaskLater(this.javaPlugin, runnable, NumberUtil.timeUnitToTicks(delay, timeUnit)));
    }

    @Override
    public ScheduledTask runTaskLaterAsync(Runnable runnable, long delay) {
        return wrap(this.scheduler.runTaskLaterAsynchronously(this.javaPlugin, runnable, delay));
    }

    @Override
    public ScheduledTask runTaskLaterAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(this.scheduler.runTaskLaterAsynchronously(this.javaPlugin, runnable, NumberUtil.timeUnitToTicks(delay, timeUnit)));
    }

    @Override
    public ScheduledTask runTaskTimer(Runnable runnable, long delay, long period) {
        return wrapRepeating(this.scheduler.runTaskTimer(this.javaPlugin, runnable, delay, period));
    }

    @Override
    public ScheduledTask runTaskTimer(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrapRepeating(this.scheduler.runTaskTimer(this.javaPlugin, runnable, NumberUtil.timeUnitToTicks(delay, timeUnit), NumberUtil.timeUnitToTicks(period, timeUnit)));
    }

    @Override
    public ScheduledTask runTaskTimerAsync(Runnable runnable, long delay, long period) {
        return wrapRepeating(this.scheduler.runTaskTimerAsynchronously(this.javaPlugin, runnable, delay, period));
    }

    @Override
    public ScheduledTask runTaskTimerAsync(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrapRepeating(this.scheduler.runTaskTimerAsynchronously(this.javaPlugin, runnable, NumberUtil.timeUnitToTicks(delay, timeUnit), NumberUtil.timeUnitToTicks(period, timeUnit)));
    }

    @Override
    public ScheduledTask runTaskAtLocation(Location location, Runnable runnable) {
        return this.runTask(runnable);
    }

    @Override
    public ScheduledTask runTaskAtLocationLater(Location location, Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    @Override
    public ScheduledTask runTaskAtLocationLater(Location location, Runnable runnable, long delay, TimeUnit timeUnit) {
        return this.runTaskLater(runnable, delay, timeUnit);
    }

    @Override
    public ScheduledTask runTaskTimerAtLocation(Location location, Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    @Override
    public ScheduledTask runTaskTimerAtLocation(Location location, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return this.runTaskTimer(runnable, delay, period, timeUnit);
    }

    @Override
    public ScheduledTask runTaskAtEntity(Entity entity, Runnable runnable) {
        return this.runTask(runnable);
    }

    @Override
    public ScheduledTask runTaskAtEntityLater(Entity entity, Runnable runnable, long delay) {
        return this.runTaskLater(runnable, delay);
    }

    @Override
    public ScheduledTask runTaskAtEntityLater(Entity entity, Runnable runnable, long delay, TimeUnit timeUnit) {
        return this.runTaskLater(runnable, delay, timeUnit);
    }

    @Override
    public ScheduledTask runTaskTimerAtEntity(Entity entity, Runnable runnable, long delay, long period) {
        return this.runTaskTimer(runnable, delay, period);
    }

    @Override
    public ScheduledTask runTaskTimerAtEntity(Entity entity, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return this.runTaskTimer(runnable, delay, period, timeUnit);
    }

    @Override
    public void cancelAllTasks() {
        this.scheduler.cancelTasks(this.javaPlugin);
    }

    private static ScheduledTask wrap(BukkitTask task) {
        return new BukkitScheduledTask(task, false);
    }

    private static ScheduledTask wrapRepeating(BukkitTask task) {
        return new BukkitScheduledTask(task, true);
    }

}
