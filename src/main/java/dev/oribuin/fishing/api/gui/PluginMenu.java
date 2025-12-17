package dev.oribuin.fishing.api.gui;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.util.Placeholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class PluginMenu<T extends BaseGui> {

    public static final ItemConstruct BORDER = new ItemConstruct(Material.BLACK_STAINED_GLASS_PANE);

    protected final FishingPlugin plugin;
    protected final GuiAction<InventoryClickEvent> EMPTY = event -> {};
    protected final String name;
    protected String title;
    protected int rows;
    protected Map<String, GuiItem> items;
    protected Map<String, GuiItem> extraItems;
    protected int pageSize;
    protected int updateFrequency;

    protected T gui;

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

    }

    /**
     * Create the menu for the GUI with the specified settings
     *
     * @return The created GUI
     */
    @SuppressWarnings("unchecked")
    public T createRegular() {
        return this.gui = (T) Gui.gui()
                .title(Component.text(this.title))
                .rows(this.rows)
                .disableAllInteractions()
                .create();
    }

    /**
     * Create the menu for the GUI with the specified settings
     *
     * @return The created GUI
     */
    @SuppressWarnings("unchecked")
    public T createPaginated() {
        return this.gui = (T) Gui.paginated().disableAllInteractions()
                .title(Component.text(this.title))
                .rows(this.rows)
                .create();
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
        GuiItem item = this.items.get(key.toLowerCase());
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
     *
     */
    public void reload() {
        FishingPlugin plugin = FishingPlugin.get();
        Path path = this.configPath();
        File targetFile = plugin.getDataPath().resolve(this.configPath()).toFile();

        try {
            // Create the file if it doesn't exist, set the defaults
            if (!targetFile.exists()) {
                plugin.saveResource(path.toString(), false);
                plugin.getLogger().info("Created a new file at path " + this.configPath()); // TODO: Remove... perhaps
            }

            // TODO: Load config file
            //            // Load the configuration file
            //            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(targetFile);
            //            this.loadSettings(config);
        } catch (Exception ex) {
            plugin.getLogger().warning("Configurable: There was an error loading the config file at path " + this.configPath() + ": " + ex.getMessage());
        }
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

}
