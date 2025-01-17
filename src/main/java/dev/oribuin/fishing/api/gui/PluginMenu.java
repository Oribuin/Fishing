package dev.oribuin.fishing.api.gui;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.util.ItemConstruct;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class PluginMenu implements Configurable {

    public static final ItemConstruct BORDER = ItemConstruct.of(Material.BLACK_STAINED_GLASS_PANE)
            .tooltip(false);

    protected final FishingPlugin plugin;
    protected final GuiAction<InventoryClickEvent> EMPTY = event -> {};

    protected final String name;
    protected String title;
    protected int rows;
    protected Map<String, GuiItem> items;
    protected Map<String, GuiItem> extraItems;
    protected int pageSize;

    /**
     * Creates a new plugin menu instance, with the specified name
     *
     * @param plugin The plugin instance
     * @param name   The name of the menu, will be also function as the menu path
     */
    public PluginMenu(FishingPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.title = name;
        this.rows = 6;
        this.items = new HashMap<>();
        this.extraItems = new HashMap<>();
        this.pageSize = 0;
    }

    /**
     * Create the menu for the GUI with the specified settings
     *
     * @return The created GUI
     */
    public Gui createRegular() {
        return Gui.gui().disableAllInteractions().title(Component.text(this.title)).rows(this.rows).create();
    }

    /**
     * Create the menu for the GUI with the specified settings
     *
     * @return The created GUI
     */
    public PaginatedGui createPaginated() {
        return Gui.paginated().disableAllInteractions()
                .title(Component.text(this.title))
                .rows(this.rows)
                .create();
    }

    /**
     * Place the items in the GUI
     *
     * @param gui The GUI to place the items in
     */
    public void placeExtras(BaseGui gui) {
        this.extraItems.forEach((key, value) -> value.place(gui, EMPTY));
    }

    /**
     * Place the item in the GUI
     *
     * @param gui      The GUI to place the item in
     * @param key      The key of the item to place
     * @param function The function to run when the item is clicked
     */
    public void placeItem(BaseGui gui, String key, GuiAction<InventoryClickEvent> function) {
        this.placeItem(gui, key, StringPlaceholders.empty(), function);
    }

    /**
     * Place the item in the GUI
     *
     * @param gui          The GUI to place the item in
     * @param key          The key of the item to place
     * @param placeholders The placeholders to apply to the item
     * @param function     The function to run when the item is clicked
     */
    public void placeItem(BaseGui gui, String key, StringPlaceholders placeholders, GuiAction<InventoryClickEvent> function) {
        GuiItem item = this.items.get(key.toLowerCase());
        if (item == null) {
            FishingPlugin.get().getLogger().warning("Failed to place item with key: " + key + " in menu: " + this.name + ". Item not found.");
            return;
        }

        item.place(gui, placeholders, function);
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.title = config.getString("title", this.name);
        this.rows = config.getInt("rows", 6);
        this.pageSize = config.getInt("page-size", 0);

        CommentedConfigurationSection items = config.getConfigurationSection("items");
        if (items != null) {
            items.getKeys(false).forEach(key -> {
                CommentedConfigurationSection item = this.pullSection(items, key);

                GuiItem guiItem = new GuiItem();
                guiItem.loadSettings(item);
                this.items.put(guiItem.name().toLowerCase(), guiItem);
            });
        }

        CommentedConfigurationSection extraItems = config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            extraItems.getKeys(false).forEach(key -> {
                CommentedConfigurationSection item = this.pullSection(extraItems, key);
                GuiItem guiItem = new GuiItem();
                guiItem.loadSettings(item);

                this.extraItems.put(key, guiItem);
            });
        }
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("title", this.title);
        config.set("rows", this.rows);
        config.set("page-size", this.pageSize);

        // Save the items
        CommentedConfigurationSection items = this.pullSection(config, "items");
        this.items.forEach((key, value) -> {
            CommentedConfigurationSection item = this.pullSection(items, key);
            value.saveSettings(item);
        });

        // Save the extra items
        CommentedConfigurationSection extraItems = this.pullSection(config, "extra-items");
        this.extraItems.forEach((key, value) -> {
            CommentedConfigurationSection item = this.pullSection(extraItems, key);
            value.saveSettings(item);
        });
    }

    /**
     * @return The path to the configuration file
     */
    public @NotNull Path configPath() {
        return Path.of("menus", this.name + ".yml");
    }

}
