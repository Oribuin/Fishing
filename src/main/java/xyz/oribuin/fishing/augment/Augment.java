package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.Map;

public abstract class Augment implements Listener {

    public static NamespacedKey AUGMENTS_KEY = new NamespacedKey(FishingPlugin.get(), "augments");

    protected final String name;
    protected String description;
    protected ItemStack displayItem;
    protected int maxLevel;
    protected boolean enabled;

    /**
     * Create a new augment instance with a name and description
     *
     * @param name        The name of the augment
     * @param description The description of the augment
     */
    public Augment(String name, String description) {
        this.name = name;
        this.description = description;
        this.maxLevel = 1;
        this.enabled = true;
    }

    /**
     * Create a new augment instance with a name and empty description
     *
     * @param name The name of the augment
     */
    public Augment(String name) {
        this(name, "No Description");
    }

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param event The initial fish catch event
     * @param level The level of the augment that was used
     */
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
    }

    /**
     * The functionality provided when a fish is generated for the player
     *
     * @param context The context of the fish event
     */
    public void onGenerate(FishContext context, double qualityChance) {
    }

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param context The context of the fish event
     * @param fish    The fish that was caught
     * @param stack   The item stack of the fish
     */
    public void onFishCatch(FishContext context, Fish fish, ItemStack stack) {
    }

    /**
     * Load the augment from a configuration file
     *
     * @param config The configuration file
     */
    public abstract void load(CommentedConfigurationSection config);

    /**
     * Save the default values of the augment to a configuration file
     */
    public abstract Map<String, Object> save();

    /**
     * Load the default values of the augment from a configuration file
     *
     * @param config The configuration file
     */
    public final void loadDefaults(CommentedConfigurationSection config) {
        this.enabled = config.getBoolean(".enabled", true);
        this.maxLevel = config.getInt(".max-level", 1);
        this.description = config.getString(".description", this.description);
        this.displayItem = FishUtils.deserialize(config, ".display-item");
        this.load(config);
    }

    /**
     * Save the default values of the augment to a configuration file
     *
     * @param config The configuration file
     */
    public final void saveDefaults(CommentedConfigurationSection config) {
        config.set(".enabled", this.enabled);
        config.set(".max-level", this.maxLevel);
        config.set(".description", this.description);
        config.set(".display-item", this.displayItem);
        this.save().forEach(config::set);
    }

    /**
     * @return If the augment is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set if the augment is enabled
     *
     * @param enabled If the augment is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return The name of the augment
     */
    public final String getName() {
        return name;
    }

    /**
     * @return The description of the augment
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Set the description of the augment
     *
     * @param description The description of the augment
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The display item of the augment
     */
    public final ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * Set the display item of the augment
     *
     * @param displayItem The display item of the augment
     */
    public final void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * @return The max level of the augment
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Set the max level of the augment
     *
     * @param maxLevel The max level of the augment
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

}
