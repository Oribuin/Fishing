package xyz.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;

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
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("color", this.toHex(this.color));
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
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
