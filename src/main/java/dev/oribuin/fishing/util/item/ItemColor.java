package dev.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.config.Configurable;

public class ItemColor implements Configurable {

    private Color color;

    /**
     * Create a new color for the item builder
     *
     * @param color The color to set
     */
    public ItemColor(Color color) {
        this.color = color;
    }

    /**
     * Create a new color for the item builder
     */
    public Color create() {
        return this.color;
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
        config.set("color", this.toHex(this.color));
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
        this.color = this.fromHex(config.getString("color"));
    }

    /**
     * Convert a color to a hex string for the item builder
     *
     * @param color The color to convert
     *
     * @return The hex string of the color
     */
    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Convert a hex string to a color for the item builder
     *
     * @param hex The hex string to convert
     *
     * @return The color of the hex string
     */
    public Color fromHex(String hex) {
        if (hex == null || hex.isEmpty()) return null;

        return Color.fromRGB(
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16)
        );
    }

}
