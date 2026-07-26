package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgradeRegistry;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.NMSUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.io.File;
import java.sql.Ref;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TotemManager implements Manager {

    private static final File UPGRADES_FOLDER = new File(FishingPlugin.get().getDataFolder(), "totem");
    private static final ConfigLoader loader = new ConfigLoader(UPGRADES_FOLDER.toPath());
    private final FishingPlugin plugin;
    private final Map<UUID, Totem> totems;
    private ScheduledTask asyncTicker;
    private final long lastTick;

    public TotemManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.totems = new ConcurrentHashMap<>();
        this.asyncTicker = null;
        this.lastTick = System.currentTimeMillis();

        TotemUpgradeRegistry.register();
        
        // Check active chunks
        this.plugin.getDataManager().loadTotems().thenAccept(this.totems::putAll);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        this.disable(plugin);
        
        // When using folia, The task to ticket them is activated in Totem#activate(Player)
        // This is done to tick each individual active totem's display entity as thats how folia works....
        if (NMSUtil.isFolia()) return;
        
        // Define all ticking under one task to prevent 10000000 tasks running at once.
        if (this.asyncTicker != null) this.asyncTicker.cancel();
        
        this.asyncTicker = PluginScheduler.get().runTaskTimerAsync(
                () -> this.tick(Totem::tickAsync),
                1000, 250, TimeUnit.MILLISECONDS
        );
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

        for (Totem totem : this.totems.values()) {
            if (totem.getDisplayId() == null) continue;
            if (!totem.isActive()) continue;
            if (!totem.getPosition().isChunkLoaded()) continue;
            action.accept(totem);
        }
    }

    /**
     * Register a totem to the totem manager. This will add the totem to the totem map.
     *
     * @param totem The totem to register.
     */
    public void registerTotem(Totem totem) {
        ArmorStand display = totem.getDisplay();
        if (display == null) return;

        this.totems.put(display.getUniqueId(), totem);
        this.plugin.getDataManager().saveTotem(totem);
    }

    /**
     * Register a totem to the totem manager. This will add the totem to the totem map.
     *
     * @param stand The armour stand to register it to
     * @param totem The totem to register.
     */
    public void registerTotem(ArmorStand stand, Totem totem) {
        this.totems.put(stand.getUniqueId(), totem);
    }

    /**
     * Register a totem to the totem manager. This will add the totem to the totem map.
     *
     * @param stand The armourstand to register it to
     * @param totem The totem to register.
     */
    public void registerTotem(UUID stand, Totem totem) {
        this.totems.put(stand, totem);
    }

    /**
     * Unregister a totem from the totem manager. This will remove the totem from the totem map.
     *
     * @param totem The totem to unregister.
     */
    public void unregisterTotem(Totem totem) {
        if (totem.getDisplayId() == null) return;

        this.totems.remove(totem.getDisplayId());
        this.plugin.getDataManager().removeTotem(totem.getDisplayId());
    }

    /**
     * Check if a totem is registered in the totem manager.
     *
     * @param totem The totem to check.
     *
     * @return If the totem is registered.
     */
    public boolean isRegistered(Totem totem) {
        if (totem == null || totem.getDisplayId() == null) return false;

        return this.totems.containsKey(totem.getDisplayId());
    }

    /**
     * Check if a totem is registered in the totem manager.
     *
     * @param totem The totem to check.
     *
     * @return If the totem is registered.
     */
    public boolean isRegistered(UUID totem) {
        return this.totems.containsKey(totem);
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
     * @param display The display entity of the totem.
     *
     * @return The totem.
     */
    public Totem getTotem(UUID display) {
        return this.totems.get(display);
    }

    /**
     * Get a totem from the totem manager by its armor stand.
     *
     * @param stand The armor stand of the totem.
     *
     * @return The totem.
     */
    public Totem getAndLoadTotem(ArmorStand stand) {
        if (!stand.getPersistentDataContainer().has(KeyRegistry.TOTEM_ACTIVE.key())) return null;

        return this.totems.computeIfAbsent(stand.getUniqueId(), uuid -> {
            Totem totem = new Totem(stand);
            this.plugin.getDataManager().saveTotem(totem);
            return totem;
        });
    }

    public Map<UUID, Totem> getTotems() {
        return totems;
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
