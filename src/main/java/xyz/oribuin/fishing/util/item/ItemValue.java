package xyz.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;

/**
 * Defines a config option that has the choice to be a tooltip or not
 *
 * @param <T> The type of the item builder
 */
@SuppressWarnings("unchecked")
public class ItemValue<T> implements Configurable {

    private T value;
    private boolean tooltip;

    /**
     * Create a new item value with a type
     *
     * @param def  The default value of the item value
     */
    public ItemValue(T def) {
        this.value = def;
        this.tooltip = false;
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        if (config.get("value") == null) return;

        this.value = (T) config.get("value");
        this.tooltip = config.getBoolean("tooltip");
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("value", this.value);
        config.set("tooltip", this.tooltip);
    }

    @Override
    public String toString() {
        return "ItemValue{" +
               "type=" + value.getClass().getSimpleName() +
               ", value=" + value +
               ", tooltip=" + tooltip +
               '}';
    }

    /**
     * Get the value of the item builder
     *
     * @return The value of the item builder
     */
    public T value() {
        return this.value;
    }

    /**
     * Set the value of the item builder
     *
     * @param value The value to set
     */
    public void value(T value) {
        this.value = value;
    }

    /**
     * Should the type be displayed as a tooltip
     *
     * @return If the type should be displayed as a tooltip
     */
    public boolean tooltip() {
        return this.tooltip;
    }

    /**
     * Set if the type should be displayed as a tooltip
     *
     * @param tooltip If the type should be displayed as a tooltip
     */
    public void tooltip(boolean tooltip) {
        this.tooltip = tooltip;
    }

}
