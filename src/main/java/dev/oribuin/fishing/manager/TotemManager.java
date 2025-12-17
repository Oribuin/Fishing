package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.storage.util.FinePosition;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TotemManager implements Manager {

    private final FishingPlugin plugin;
    private final Map<FinePosition, Totem> totems;
    private ScheduledTask asyncTicker;
    private long lastTick;

    public TotemManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.totems = new ConcurrentHashMap<>();
        this.asyncTicker = null;
        this.lastTick = System.currentTimeMillis();
        this.reload(this.plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        this.disable(plugin);
        
        // Define all ticking under one task to prevent 10000000 tasks running at once.
        if (this.asyncTicker != null) {
            this.asyncTicker.cancel();
        }

        this.asyncTicker = PluginScheduler.get().runTaskTimerAsync(() -> this.tick(totem -> {
            if (totem.delay() != Duration.ZERO && System.currentTimeMillis() - this.lastTick < totem.delay().toMillis()) return;

            this.lastTick = System.currentTimeMillis();
            totem.tickAsync();
        }), 1, 1, TimeUnit.SECONDS);
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
        if (this.asyncTicker != null) {
            this.asyncTicker.cancel();
            this.asyncTicker = null;
        }
    }
    
    /**
     * Tick all the totems in the totem manager.
     */
    public void tick(Consumer<Totem> action) {
        if (this.totems.isEmpty()) return; // don't bother attempting anything if no totems loaded
        
        new HashMap<>(this.totems).forEach((finePosition, totem) -> {
            if (!totem.center().isChunkLoaded()) return;

            if (totem.entity() == null || totem.entity().isDead()) {
                this.unregisterTotem(totem);
                return;
            }

            action.accept(totem);
        });
    }

    /**
     * Register a totem to the totem manager. This will add the totem to the totem map.
     *
     * @param totem The totem to register.
     */
    public void registerTotem(Totem totem) {
        FinePosition position = FinePosition.from(totem.center());

        if (!totem.center().isChunkLoaded()) return;

        this.totems.put(position, totem);
    }

    /**
     * Unregister a totem from the totem manager. This will remove the totem from the totem map.
     *
     * @param totem The totem to unregister.
     */
    public void unregisterTotem(Totem totem) {
        FinePosition position = FinePosition.from(totem.center());

        this.totems.remove(position);
    }

    /**
     * Check if a totem is registered in the totem manager.
     *
     * @param totem The totem to check.
     *
     * @return If the totem is registered.
     */
    public boolean isRegistered(Totem totem) {
        if (totem == null) return false;

        FinePosition position = FinePosition.from(totem.center());
        return this.totems.containsKey(position);
    }

    /**
     * Get the closest active totem to a location.
     *
     * @param location The location to check.
     *
     * @return The closest active totem.
     */
    public Totem getClosestActive(Location location) {
        if (this.totems.isEmpty()) return null;

        return this.totems.values().stream().filter(x -> x.getProperty(KeyRegistry.TOTEM_ACTIVE)).min((t1, t2) -> {
            double distance1 = t1.center().distance(location);
            double distance2 = t2.center().distance(location);
            return Double.compare(distance1, distance2);
        }).orElse(null);
    }

    /**
     * Get a totem from the totem manager by its fine position.
     *
     * @param position The fine position of the totem.
     *
     * @return The totem.
     */
    public Totem getTotem(FinePosition position) {
        return this.totems.get(position);
    }

    /**
     * Get a totem from the totem manager by its location.
     *
     * @param location The location of the totem.
     *
     * @return The totem.
     */
    public Totem getTotem(Location location) {
        return this.getTotem(FinePosition.from(location));
    }

    /**
     * Get a totem from the totem manager by its armor stand.
     *
     * @param stand The armor stand of the totem.
     *
     * @return The totem.
     */
    public Totem getTotem(ArmorStand stand) {
        return this.getTotem(stand.getLocation());
    }

    public Map<FinePosition, Totem> getTotems() {
        return this.totems;
    }

    public FishingPlugin getPlugin() {
        return plugin;
    }

    public ScheduledTask getAsyncTicker() {
        return asyncTicker;
    }

    public long getLastTick() {
        return lastTick;
    }
}
