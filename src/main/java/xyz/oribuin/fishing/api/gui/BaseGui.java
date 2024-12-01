package xyz.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.api.config.Configurable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseGui implements InventoryHolder, Configurable {

    private static final BiConsumer<MenuIcon, InventoryClickEvent> EMPTY = (icon, event) -> {};

    protected final RosePlugin plugin;
    protected final String id;
    private Inventory inventory;
    private String title;
    private int rows;
    private Consumer<InventoryClickEvent> clickConsumer;
    private Consumer<InventoryCloseEvent> closeConsumer;
    private Map<Integer, MenuIcon> staticIcons;
    private Map<Integer, Consumer<InventoryClickEvent>> slotConsumers;

    /**
     * Create a new gui instance with the specified plugin and id
     *
     * @param plugin The plugin instance
     * @param id     The id of the gui
     */
    public BaseGui(RosePlugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.title = "undefined";
        this.rows = 6;
        this.clickConsumer = event -> {};
        this.closeConsumer = event -> {};
        this.staticIcons = new HashMap<>();
        this.inventory = Bukkit.createInventory(this, this.rows * 9, Component.text(this.title));
    }

    /**
     * Update the inventory with the static icons
     */
    public void update() {
        this.inventory.clear(); // Clear the inventory

        // Add the static icons
        for (Map.Entry<Integer, MenuIcon> entry : this.staticIcons.entrySet()) {
            int slot = entry.getKey();
            MenuIcon icon = entry.getValue();
            this.inventory.setItem(slot, icon.item().build(icon.placeholders()));
        }
    }

    /**
     * Place a static icon in the GUI at the specified slot
     *
     * @param icon  The icon to place
     */
    public final void placeStatic(MenuIcon icon) {
        for (int slot : icon.slots()) {
            this.staticIcons.put(slot, icon);
        }
    }

    /**
     * Construct a new MenuIcon with the given path. This is used to place an item in the menu.
     *
     * @param path The path to the item in the config
     *
     * @return The constructed MenuIcon
     */
    protected MenuIcon construct(String path) {
        return this.construct(path, null, StringPlaceholders.empty(), EMPTY);
    }


    /**
     * Construct a new MenuIcon with the given path. This is used to place an item in the menu.
     *
     * @param path   The path to the item in the config
     * @param player The player to construct the item for
     *
     * @return The constructed MenuIcon
     */
    protected MenuIcon construct(String path, Player player) {
        return this.construct(path, player, StringPlaceholders.empty(), EMPTY);
    }

    /**
     * Construct a new MenuIcon with the given path. This is used to place an item in the menu.
     *
     * @param path     The path to the item in the config
     * @param player   The player to construct the item for
     * @param consumer The consumer to run when the item is clicked
     *
     * @return The constructed MenuIcon
     */
    protected MenuIcon construct(String path, Player player, BiConsumer<MenuIcon, InventoryClickEvent> consumer) {
        return this.construct(path, player, StringPlaceholders.empty(), consumer);
    }

    /**
     * Construct a new MenuIcon with the given path. This is used to place an item in the menu.
     *
     * @param path         The path to the item in the config
     * @param player       The player to construct the item for
     * @param placeholders The placeholders to replace in the item
     * @param consumer     The consumer to run when the item is clicked
     */
    protected MenuIcon construct(String path, Player player, StringPlaceholders placeholders, BiConsumer<MenuIcon, InventoryClickEvent> consumer) {
        return null;
    }


    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.title = config.getString("title", this.title);
        this.rows = config.getInt("rows", this.rows);

        this.inventory = Bukkit.createInventory(this, this.rows * 9, Component.text(this.title));
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("title", this.title);
        config.set("rows", this.rows);
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @Nullable Path configPath() {
        return Path.of("menus", this.id + ".yml");
    }

    /**
     * @return The parent folder for the configuration file
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    /**
     * @return The title of the GUI
     */
    public String title() {
        return title;
    }

    /**
     * Set the title of the GUI
     *
     * @param title The title of the GUI
     */
    public void title(String title) {
        this.title = title;
    }

    /**
     * @return The amount of rows in the GUI
     */
    public int rows() {
        return rows;
    }

    /**
     * Set the amount of rows in the GUI
     *
     * @param rows The amount of rows in the GUI
     */
    public void rows(int rows) {
        this.rows = rows;
    }

    /**
     * @return The inventory of the GUI
     */
    public Inventory inventory() {
        return inventory;
    }

    /**
     * Set the inventory of the GUI
     *
     * @param inventory The inventory of the GUI
     */
    public void inventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return The consumer for when the GUI is closed
     */
    public Consumer<InventoryCloseEvent> closeConsumer() {
        return closeConsumer;
    }

    /**
     * Set the consumer for when the GUI is closed
     *
     * @param closeConsumer The consumer for when the GUI is closed
     */
    public void closeConsumer(Consumer<InventoryCloseEvent> closeConsumer) {
        this.closeConsumer = closeConsumer;
    }

    /**
     * @return The consumer for when an item is clicked
     */
    public Consumer<InventoryClickEvent> clickConsumer() {
        return clickConsumer;
    }

    /**
     * Set the consumer for when an item is clicked
     *
     * @param clickConsumer The consumer for when an item is clicked
     */
    public void clickConsumer(Consumer<InventoryClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
    }

    /**
     * @return The static icons for the GUI
     */
    public Map<Integer, MenuIcon> staticIcons() {
        return staticIcons;
    }

    /**
     * Set the static icons for the GUI
     *
     * @param staticIcons The static icons for the GUI
     */
    public void staticIcons(Map<Integer, MenuIcon> staticIcons) {
        this.staticIcons = staticIcons;
    }

    /**
     * @return The slot consumers for the GUI
     */
    public Map<Integer, Consumer<InventoryClickEvent>> slotConsumers() {
        return slotConsumers;
    }

    /**
     * Set the slot consumers for the GUI
     *
     * @param slotConsumers The slot consumers for the GUI
     */
    public void slotConsumers(Map<Integer, Consumer<InventoryClickEvent>> slotConsumers) {
        this.slotConsumers = slotConsumers;
    }

}
