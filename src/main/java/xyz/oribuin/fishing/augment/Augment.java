package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.FishEventHandler;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.Map;

public abstract class Augment extends FishEventHandler implements Listener {

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
    public boolean enabled() {
        return enabled;
    }

    /**
     * Set if the augment is enabled
     *
     * @param enabled If the augment is enabled
     */
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return The name of the augment
     */
    public final String name() {
        return name;
    }

    /**
     * @return The description of the augment
     */
    public final String description() {
        return description;
    }

    /**
     * Set the description of the augment
     *
     * @param description The description of the augment
     */
    public void description(String description) {
        this.description = description;
    }

    /**
     * @return The display item of the augment
     */
    public final ItemStack displayItem() {
        return displayItem;
    }

    /**
     * Set the display item of the augment
     *
     * @param displayItem The display item of the augment
     */
    public final void displayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * @return The max level of the augment
     */
    public int maxLevel() {
        return maxLevel;
    }

    /**
     * Set the max level of the augment
     *
     * @param maxLevel The max level of the augment
     */
    public void maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

}
