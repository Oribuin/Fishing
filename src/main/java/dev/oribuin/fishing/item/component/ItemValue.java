package dev.oribuin.fishing.item.component;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.config.Configurable;

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
        if (config.get("value") == null) return;

        this.value = (T) config.get("value");
        this.tooltip = config.getBoolean("tooltip");
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
