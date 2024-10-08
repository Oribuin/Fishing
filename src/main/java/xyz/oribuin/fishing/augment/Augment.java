package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.api.event.FishEventHandler;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.nio.file.Path;
import java.util.List;

public abstract class Augment extends FishEventHandler implements Listener, Configurable {

    protected final String name;
    protected String description;
    protected ItemConstruct displayItem;
    protected int maxLevel;
    protected int requiredLevel;
    protected boolean enabled;

    /**
     * Create a new augment instance with a name and description
     *
     * @param name        The name of the augment
     * @param description The description of the augment
     */
    public Augment(String name, String description) {
        this.enabled = true;
        this.name = name;
        this.description = description;
        this.maxLevel = 1;
        this.requiredLevel = 1;
        this.displayItem = ItemConstruct.EMPTY;
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
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("augments", this.name + ".yml");
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.addComments(this.comments().toArray(new String[0]));
        config.set("enabled", this.enabled);
        config.set("max-level", this.maxLevel);
        config.set("required-level", this.requiredLevel);
        config.set("description", List.of(this.description.split("\n")));

        CommentedConfigurationSection section = config.getConfigurationSection("display-item");
        if (section == null) section = config.createSection("display-item");

        this.displayItem.serialize(section); // Serialize the display item
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.enabled = config.getBoolean("enabled", true);
        this.maxLevel = config.getInt("max-level", 1);
        this.requiredLevel = config.getInt("required-level", 1);
        this.description = String.join("\n", config.getStringList("description"));

        ItemConstruct construct = ItemConstruct.deserialize(config.getConfigurationSection("display-item"));
        if (construct == null) {
            FishingPlugin.get().getLogger().warning("Failed to load display item for augment: " + this.name);
            return;
        }

        this.displayItem = construct;
    }

    /**
     * Get the namespace key for the augment
     *
     * @return The namespace key
     */
    public NamespacedKey key() {
        return new NamespacedKey(FishingPlugin.get(), this.name);
    }

    /**
     * The placeholders for the augment when it is used
     *
     * @return The placeholders
     */
    public StringPlaceholders placeholders() {
        return StringPlaceholders.empty();
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return this.description.isEmpty() ? List.of("No Description") : List.of(this.description);
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
    public final ItemConstruct displayItem() {
        return displayItem;
    }

    /**
     * Set the display item of the augment
     *
     * @param displayItem The display item of the augment
     */
    public final void displayItem(ItemConstruct displayItem) {
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

    /**
     * @return The required level of the augment
     */
    public int requiredLevel() {
        return requiredLevel;
    }

    /**
     * Set the required level of the augment
     *
     * @param requiredLevel The required level of the augment
     */
    public void requiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

}
