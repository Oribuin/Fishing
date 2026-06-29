package dev.oribuin.fishing.gui;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigHandler;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.scheduler.PluginScheduler;
import dev.oribuin.fishing.scheduler.task.ScheduledTask;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class PluginMenu<T extends BaseGui> {

    public static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE)
            .setTooltip(false);

    protected final transient FishingPlugin plugin;
    protected final transient GuiAction<InventoryClickEvent> EMPTY = event -> {};
    protected final transient GuiAction<InventoryClickEvent> CANCELLED = event -> {
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    };

    protected final transient String name;
    protected transient ConfigHandler<PluginMenu<?>> configHandler;
    protected String title;
    protected int rows;
    protected Map<String, MenuItem> items;
    protected Map<String, MenuItem> extraItems;
    protected int pageSize;
    protected int updateFrequency;
    protected transient boolean viewed;
    private transient ScheduledTask task;

    protected transient T gui;

    /**
     * Creates a new plugin menu instance, with the specified name
     *
     * @param name The name of the menu, will be also function as the menu path
     */
    public PluginMenu(String name) {
        this.plugin = FishingPlugin.get();
        this.name = name;
        this.title = name;
        this.rows = 6;
        this.items = new HashMap<>();
        this.extraItems = new HashMap<>();
        this.pageSize = 0;
        this.updateFrequency = 60; // 3s
        this.gui = null;
        this.viewed = false;
    }

    public PluginMenu() {
        this("unknown-menu");
    }

    /**
     * Creates the menu for the plugin
     *
     * @return the resulting menu
     */
    public abstract Supplier<T> createMenu();

    /**
     * Open the menu for the player synchronously and mark the menu as being viewed
     *
     * @param player The player opening the menu
     */
    public void open(Player player) {
        if (this.gui == null) return;

        this.viewed = true;
        PluginScheduler.get().runTask(() -> this.gui.open(player));

        // If the gui is tickable, tick the gui
        if (this instanceof GuiTickable tickable) {
            long delay = tickable.getTickDelay().toSeconds() * 20L;
            if (this.task != null) this.task.cancel();

            this.task = PluginScheduler.get().runTaskTimerAsync(() -> {
                // Gui doesn't exist, don't tick & cancel
                if (this.gui == null) {
                    this.task.cancel();
                    return;
                }

                // GUI is viewed but no longer has viewers, cancel
                if (this.viewed && this.gui.getInventory().getViewers().isEmpty()) {
                    this.task.cancel();
                    return;
                }

                tickable.tick();
            }, delay, delay, TimeUnit.SECONDS);
        }
    }

    /**
     * Place additional items in the GUI that are not part of the function of the menu
     */
    public void placeExtras(Placeholders placeholders) {
        if (this.gui == null) {
            FishingPlugin.get().getLogger().warning("Failed to place extra items in menu: " + this.name + ". GUI is null.");
            return;
        }

        this.extraItems.forEach((key, value) -> value.place(this.gui, placeholders, EMPTY));
    }

    /**
     * Place the item in the GUI
     *
     * @param key The key of the item to place
     */
    public void placeItem(String key) {
        this.placeItem(key, Placeholders.empty(), EMPTY);
    }

    /**
     * Place the item in the GUI
     *
     * @param key          The key of the item to place
     * @param placeholders The placeholders to apply to the item
     */
    public void placeItem(String key, Placeholders placeholders) {
        this.placeItem(key, placeholders, EMPTY);
    }

    /**
     * Place the item in the GUI
     *
     * @param key      The key of the item to place
     * @param function The function to run when the item is clicked
     */
    public void placeItem(String key, GuiAction<InventoryClickEvent> function) {
        this.placeItem(key, Placeholders.empty(), function);
    }

    /**
     * Place the item in the GUI
     *
     * @param key          The key of the item to place
     * @param placeholders The placeholders to apply to the item
     * @param function     The function to run when the item is clicked
     */
    public void placeItem(String key, Placeholders placeholders, GuiAction<InventoryClickEvent> function) {
        MenuItem item = this.items.get(key.toLowerCase());
        if (item == null) {
            FishingPlugin.get().getLogger().warning("Failed to place item with key: " + key + " in menu: " + this.name + ". Item not found.");
            return;
        }

        if (this.gui == null) {
            FishingPlugin.get().getLogger().warning("Failed to place extra items in menu: " + this.name + ". GUI is null.");
            return;
        }

        item.place(gui, placeholders, function);
    }

    /**
     * Update the task for the menu to run a function every x ticks, this will automatically cancel the task if the GUI is closed
     *
     * @param runnable The function to run
     */
    public void updateTask(Runnable runnable) {
        if (this.updateFrequency <= 0) this.updateFrequency = 60;

        // TODO: Swap to use PluginTask
        Bukkit.getScheduler().runTaskTimerAsynchronously(FishingPlugin.get(), task -> {
            if (gui.getInventory().getViewers().isEmpty()) {
                task.cancel();
                return;
            }

            runnable.run();
        }, 0L, this.updateFrequency);
    }

    /**
     * @return The path to the configuration file
     */
    public @NotNull Path configPath() {
        return Path.of("menus", this.name + ".yml");
    }

    /**
     * The name of the menu instance
     *
     * @return The name of the menu
     */
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "PluginMenu{" +
               "updateFrequency=" + updateFrequency +
               ", pageSize=" + pageSize +
               ", extraItems=" + extraItems +
               ", items=" + items +
               ", rows=" + rows +
               ", title='" + title + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

    public ConfigHandler<PluginMenu<?>> getConfigHandler() {
        return configHandler;
    }

    public void setConfigHandler(ConfigHandler<PluginMenu<?>> configHandler) {
        this.configHandler = configHandler;
    }
}
