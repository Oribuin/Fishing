package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.util.FinePosition;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.PluginTask;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TotemManager extends Manager {

    private final Map<FinePosition, Totem> totems = new HashMap<>();
    private PluginTask asyncTicker = PluginTask.empty();
    private long lastTick = System.currentTimeMillis();

    public TotemManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        // Define all ticking under one task to prevent 10000000 tasks running at once.
        this.asyncTicker.cancel(); 
        this.asyncTicker = PluginTask.scheduleRepeating(() -> this.tick(totem -> {
            if (totem.delay() != Duration.ZERO && System.currentTimeMillis() - this.lastTick < totem.delay().toMillis()) return;

            this.lastTick = System.currentTimeMillis();
            totem.tickAsync();
        }), Duration.ofSeconds(1));
    }

    @Override
    public void disable() {
        this.asyncTicker.cancel();
    }

    /**
     * Tick all the totems in the totem manager.
     */
    public void tick(Consumer<Totem> action) {
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

    public Map<FinePosition, Totem> totems() {
        return this.totems;
    }


}
