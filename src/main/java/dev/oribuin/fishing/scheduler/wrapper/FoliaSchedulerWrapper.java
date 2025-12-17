package dev.oribuin.fishing.scheduler.wrapper;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.scheduler.task.FoliaScheduledTask;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.util.math.NumberUtil;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

public class FoliaSchedulerWrapper implements SchedulerWrapper {

    private final FishingPlugin javaPlugin;
    private final RegionScheduler regionScheduler;
    private final GlobalRegionScheduler globalRegionScheduler;
    private final AsyncScheduler asyncScheduler;

    public FoliaSchedulerWrapper(FishingPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.regionScheduler = Bukkit.getRegionScheduler();
        this.globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
        this.asyncScheduler = Bukkit.getAsyncScheduler();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return Bukkit.isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isLocationThread(Location location) {
        return Bukkit.isOwnedByCurrentRegion(location);
    }

    @Override
    public ScheduledTask runTask(Runnable runnable) {
        return wrap(this.globalRegionScheduler.run(this.javaPlugin, task -> runnable.run()));
    }

    @Override
    public ScheduledTask runTaskAsync(Runnable runnable) {
        return wrap(this.asyncScheduler.runNow(this.javaPlugin, task -> runnable.run()));
    }

    @Override
    public ScheduledTask runTaskLater(Runnable runnable, long delay) {
        return wrap(this.globalRegionScheduler.runDelayed(this.javaPlugin, task -> runnable.run(), fix(delay)));
    }

    @Override
    public ScheduledTask runTaskLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(this.globalRegionScheduler.runDelayed(this.javaPlugin, task -> runnable.run(), fix(delay)));
    }

    @Override
    public ScheduledTask runTaskLaterAsync(Runnable runnable, long delay) {
        return wrap(this.asyncScheduler.runDelayed(this.javaPlugin, task -> runnable.run(), fix(ticksToMillis(delay)), TimeUnit.MILLISECONDS));
    }

    @Override
    public ScheduledTask runTaskLaterAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(this.asyncScheduler.runDelayed(this.javaPlugin, task -> runnable.run(), fix(delay), timeUnit));
    }

    @Override
    public ScheduledTask runTaskTimer(Runnable runnable, long delay, long period) {
        return wrap(this.globalRegionScheduler.runAtFixedRate(this.javaPlugin, task -> runnable.run(), fix(delay), fix(period)));
    }

    @Override
    public ScheduledTask runTaskTimer(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrap(this.globalRegionScheduler.runAtFixedRate(this.javaPlugin, task -> runnable.run(), fix(NumberUtil.timeUnitToTicks(delay, timeUnit)), fix(NumberUtil.timeUnitToTicks(period, timeUnit))));
    }

    @Override
    public ScheduledTask runTaskTimerAsync(Runnable runnable, long delay, long period) {
        return wrap(this.asyncScheduler.runAtFixedRate(this.javaPlugin, task -> runnable.run(), fix(ticksToMillis(delay)), fix(ticksToMillis(period)), TimeUnit.MILLISECONDS));
    }

    @Override
    public ScheduledTask runTaskTimerAsync(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrap(this.asyncScheduler.runAtFixedRate(this.javaPlugin, task -> runnable.run(), fix(delay), fix(period), timeUnit));
    }

    @Override
    public ScheduledTask runTaskAtLocation(Location location, Runnable runnable) {
        return wrap(this.regionScheduler.run(this.javaPlugin, location, task -> runnable.run()));
    }

    @Override
    public ScheduledTask runTaskAtLocationLater(Location location, Runnable runnable, long delay) {
        return wrap(this.regionScheduler.runDelayed(this.javaPlugin, location, task -> runnable.run(), fix(delay)));
    }

    @Override
    public ScheduledTask runTaskAtLocationLater(Location location, Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(this.regionScheduler.runDelayed(this.javaPlugin, location, task -> runnable.run(), fix(NumberUtil.timeUnitToTicks(delay, timeUnit))));
    }

    @Override
    public ScheduledTask runTaskTimerAtLocation(Location location, Runnable runnable, long delay, long period) {
        return wrap(this.regionScheduler.runAtFixedRate(this.javaPlugin, location, task -> runnable.run(), fix(delay), fix(period)));
    }

    @Override
    public ScheduledTask runTaskTimerAtLocation(Location location, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrap(this.regionScheduler.runAtFixedRate(this.javaPlugin, location, task -> runnable.run(), fix(NumberUtil.timeUnitToTicks(delay, timeUnit)), fix(NumberUtil.timeUnitToTicks(period, timeUnit))));
    }

    @Override
    public ScheduledTask runTaskAtEntity(Entity entity, Runnable runnable) {
        return wrap(entity.getScheduler().run(this.javaPlugin, task -> runnable.run(), null));
    }

    @Override
    public ScheduledTask runTaskAtEntityLater(Entity entity, Runnable runnable, long delay) {
        return wrap(entity.getScheduler().runDelayed(this.javaPlugin, task -> runnable.run(), null, fix(delay)));
    }

    @Override
    public ScheduledTask runTaskAtEntityLater(Entity entity, Runnable runnable, long delay, TimeUnit timeUnit) {
        return wrap(entity.getScheduler().runDelayed(this.javaPlugin, task -> runnable.run(), null, fix(NumberUtil.timeUnitToTicks(delay, timeUnit))));
    }

    @Override
    public ScheduledTask runTaskTimerAtEntity(Entity entity, Runnable runnable, long delay, long period) {
        return wrap(entity.getScheduler().runAtFixedRate(this.javaPlugin, task -> runnable.run(), null, fix(delay), fix(period)));
    }

    @Override
    public ScheduledTask runTaskTimerAtEntity(Entity entity, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return wrap(entity.getScheduler().runAtFixedRate(this.javaPlugin, task -> runnable.run(), null, fix(NumberUtil.timeUnitToTicks(delay, timeUnit)), fix(NumberUtil.timeUnitToTicks(period, timeUnit))));
    }

    @Override
    public void cancelAllTasks() {
        this.globalRegionScheduler.cancelTasks(this.javaPlugin);
        this.asyncScheduler.cancelTasks(this.javaPlugin);
    }

    private static ScheduledTask wrap(io.papermc.paper.threadedregions.scheduler.ScheduledTask foliaTask) {
        return new FoliaScheduledTask(foliaTask);
    }

    private static long fix(long delay) {
        return delay > 0 ? delay : 1;
    }

    private static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

}
