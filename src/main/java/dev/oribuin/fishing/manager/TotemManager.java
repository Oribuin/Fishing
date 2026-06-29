package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.UpgradeRegistry;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.storage.util.FinePosition;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TotemManager implements Manager {

    private static final File UPGRADES_FOLDER = new File(FishingPlugin.get().getDataFolder(), "totem");
    private static final ConfigLoader loader = new ConfigLoader(UPGRADES_FOLDER.toPath());
    private final FishingPlugin plugin;
    private final Map<FinePosition, Totem> totems;
    private ScheduledTask asyncTicker;
    private long lastTick;

    public TotemManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.totems = new ConcurrentHashMap<>();
        this.asyncTicker = null;
        this.lastTick = System.currentTimeMillis();

        UpgradeRegistry.register();
        // Check active chunks
        CompletableFuture.runAsync(() -> Bukkit.getWorlds().forEach(world ->
                Arrays.stream(world.getLoadedChunks()).forEach(chunk ->
                        Arrays.stream(chunk.getEntities()).forEach(entity -> {
                            if (!(entity instanceof ArmorStand stand)) return;

                            PersistentDataContainer container = stand.getPersistentDataContainer();
                            if (!container.has(KeyRegistry.TOTEM_OWNER.key(), KeyRegistry.TOTEM_OWNER)) return;

                            this.registerTotem(new Totem(stand));
                        }))));
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
            Duration cooldown = totem.getCooldown();
            if (cooldown != Duration.ZERO && System.currentTimeMillis() - this.lastTick < cooldown.toMillis()) return;

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
            if (!totem.getPosition().isChunkLoaded()) return;

            if (totem.getDisplay() == null || totem.getDisplay().isDead()) {
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
        FinePosition position = FinePosition.from(totem.getPosition());

        if (!totem.getPosition().isChunkLoaded()) return;

        this.totems.put(position, totem);
    }

    /**
     * Unregister a totem from the totem manager. This will remove the totem from the totem map.
     *
     * @param totem The totem to unregister.
     */
    public void unregisterTotem(Totem totem) {
        FinePosition position = FinePosition.from(totem.getPosition());

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

        FinePosition position = FinePosition.from(totem.getPosition());
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

        return this.totems.values().stream().filter(Totem::isActive).min((t1, t2) -> {
            double distance1 = t1.getPosition().distance(location);
            double distance2 = t2.getPosition().distance(location);
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
        FinePosition position = FinePosition.from(stand.getLocation());
        return this.totems.computeIfAbsent(position, x -> new Totem(stand));
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

    public static ConfigLoader getLoader() {
        return loader;
    }
}
