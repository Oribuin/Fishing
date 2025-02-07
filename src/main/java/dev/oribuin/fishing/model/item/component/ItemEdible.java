package dev.oribuin.fishing.model.item.component;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.config.Configurable;

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
        config.set("nutrition", this.nutrition);
        config.set("saturation", this.saturation);
        config.set("require-hunger", this.requireHunger);
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