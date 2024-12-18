package xyz.oribuin.fishing.util.item;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;

@SuppressWarnings("UnstableApiUsage")
public final class ItemEdible implements Configurable {

    private int nutrition;
    private float saturation;
    private boolean requireHunger;

    /**
     * Define the nutritional values of an item that can be eaten
     *
     * @param nutrition     The amount of hunger restored
     * @param saturation    The saturation of the item
     * @param requireHunger If the item requires hunger to be eaten
     */
    public ItemEdible(int nutrition, float saturation, boolean requireHunger) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.requireHunger = requireHunger;
    }

    /**
     * Create a new potion effect from the builder
     *
     * @return The potion effect
     */
    public FoodProperties create() {
        return FoodProperties.food()
                .nutrition(this.nutrition)
                .saturation(this.saturation)
                .canAlwaysEat(!this.requireHunger)
                .build();
    }

    /**
     * Create a new potion effect from the builder
     *
     * @param config The configuration section to load the potion effect from
     *
     * @return The potion effect
     */
    public static ItemEdible of(CommentedConfigurationSection config) {
        ItemEdible effect = new ItemEdible(1, 1, false);
        effect.loadSettings(config);
        return effect;
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("nutrition", this.nutrition);
        config.set("saturation", this.saturation);
        config.set("require-hunger", this.requireHunger);
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.nutrition = config.getInt("nutrition", 1);
        this.saturation = config.getInt("saturation", 1);
        this.requireHunger = config.getBoolean("require-hunger", false);
    }

    @Override
    public String toString() {
        return "ItemEdible{" +
               "nutrition=" + nutrition +
               ", saturation=" + saturation +
               ", requireHunger=" + requireHunger +
               '}';
    }

    public int nutrition() {
        return this.nutrition;
    }

    public void nutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    public float saturation() {
        return this.saturation;
    }

    public void saturation(float saturation) {
        this.saturation = saturation;
    }

    public boolean requireHunger() {
        return this.requireHunger;
    }

}