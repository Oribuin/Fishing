package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.fishing.storage.util.FinePosition;
import xyz.oribuin.fishing.totem.Totem;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TotemManager extends Manager {

    private final Map<FinePosition, Totem> totems = new HashMap<>();
    private BukkitTask asyncTicker;
    private long lastTick = System.currentTimeMillis();

    public TotemManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.unregisterTask();

        this.asyncTicker = Bukkit.getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> this.tick(totem -> {
            if (totem.delay() != Duration.ZERO && System.currentTimeMillis() - this.lastTick < totem.delay().toMillis()) return;

            this.lastTick = System.currentTimeMillis();
            totem.tickAsync();
        }), 0, 1);
    }

    @Override
    public void disable() {
        this.unregisterTask();
    }

    /**
     * Unregister the ticking task for the totem manager.
     */
    private void unregisterTask() {
        if (this.asyncTicker != null) this.asyncTicker.cancel();
        this.asyncTicker = null;
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
        return this.totems.values().stream().filter(Totem::active).min((t1, t2) -> {
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
