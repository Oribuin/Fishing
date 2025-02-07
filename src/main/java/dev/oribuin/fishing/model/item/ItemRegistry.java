package dev.oribuin.fishing.model.item;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.totem.Totem;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry implements Configurable {

    private static final Map<String, ItemConstruct> CUSTOM_ITEMS = new HashMap<>();
    public static ItemConstruct FISHING_TOTEM = Totem.defaultItem();

    /**
     * Reload the configuration file and load the settings into the configurable class
     */
    public static void init() {
        ItemRegistry registry = new ItemRegistry();
        registry.reload();
    }

    /**
     * Register a custom item to the registry
     *
     * @param key  The key to register the item under
     * @param item The item to register
     */
    public static void registerCustomItem(String key, ItemConstruct item) {
        CUSTOM_ITEMS.put(key.toUpperCase(), item);
    }

    /**
     * Get an {@link ItemConstruct} from the key provided
     *
     * @param key The key to get the item from
     *
     * @return The item construct or null if not found
     */
    public static ItemConstruct from(String key) {
        try {
            if (CUSTOM_ITEMS.containsKey(key.toUpperCase())) return CUSTOM_ITEMS.get(key.toUpperCase());
            // TODO: Check if item is from Oraxen/Nexo/MythicMobs whatever plugin

            Field field = ItemRegistry.class.getDeclaredField(key.toUpperCase());
            return (ItemConstruct) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            FishingPlugin.get().getLogger().warning("Failed to get item: [" + key + "]. Error: " + e.getMessage());
            return null;
        }
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
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getType().equals(ItemConstruct.class)) continue;

            try {
                ItemConstruct item = (ItemConstruct) field.get(this);
                if (item == null) continue;

                // Load the settings from the configuration file
                CommentedConfigurationSection section = config.getConfigurationSection(field.getName().toLowerCase());
                if (section == null) continue;

                item.loadSettings(section);
            } catch (IllegalAccessException e) {
                FishingPlugin.get().getLogger().warning("Failed to load item: [" + field.getName() + "]. Error: " + e.getMessage());
            }
        }

        // Load custom items from the configuration file
        CommentedConfigurationSection customItems = config.getConfigurationSection("custom-items");
        if (customItems == null) return;

        customItems.getKeys(false).forEach(key -> {
            CommentedConfigurationSection section = customItems.getConfigurationSection(key);
            if (section == null) return;

            CUSTOM_ITEMS.put(key.toUpperCase(), ItemConstruct.deserialize(section));
        });
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
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getType().equals(ItemConstruct.class)) continue;

            try {
                ItemConstruct item = (ItemConstruct) field.get(this);
                if (item == null) continue;

                // Save the settings to the configuration file
                CommentedConfigurationSection section = this.pullSection(config, field.getName().toLowerCase());
                item.saveSettings(section);
            } catch (IllegalAccessException e) {
                FishingPlugin.get().getLogger().warning("Failed to save item: [" + field.getName() + "]. Error: " + e.getMessage());
            }
        }

        // Save custom items to the configuration file
        CommentedConfigurationSection customItems = this.pullSection(config, "custom-items");
        CUSTOM_ITEMS.forEach((key, item) -> {
            CommentedConfigurationSection section = this.pullSection(customItems, key);
            item.saveSettings(section);
        });
    }

    /**
     * The file path to a {@link CommentedFileConfiguration} file, This path by default will be relative {@link #parentFolder()}.
     * <p>
     * This by default is only used in the {@link #reload()} method to load the configuration file
     * <p>
     * This an optional method and should only be used if the Configurable class is its own file (E.g. {@link Augment} class)
     *
     * @return The path to the configuration file
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("items.yml");
    }

}
