package xyz.oribuin.fishing.api.recipe;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;


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
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.amount = config.getInt("amount", 1);
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
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
