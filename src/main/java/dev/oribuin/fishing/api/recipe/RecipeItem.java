package dev.oribuin.fishing.api.recipe;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.config.Configurable;

public abstract class RecipeItem<T> implements Configurable {

    private final Class<T> type; // This is the type of the ite;
    protected T item;
    protected int amount;

    public RecipeItem(Class<T> type) {
        this.type = type;
    }

    /**
     * Check if the item is the same as the recipe item
     *
     * @param item The item to check
     *
     * @return If the item is the same as the recipe item
     */
    public abstract boolean check(ItemStack item);

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
        this.amount = config.getInt("amount", 1);
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
        config.set("amount", this.amount);
    }

    /**
     * Get the item of the recipe item
     *
     * @return The item of the recipe item
     */
    public final T item() {
        return this.item;
    }

    /**
     * Get the amount of the recipe item
     *
     * @return The amount of the recipe item
     */
    public final Class<T> type() {
        return this.type;
    }

    /**
     * Get the amount of the recipe item
     *
     * @return The amount of the recipe item
     */
    public final int amount() {
        return this.amount;
    }

}
