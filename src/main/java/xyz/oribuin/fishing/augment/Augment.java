package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.api.event.FishEventHandler;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Augment extends FishEventHandler implements Listener, Configurable {

    protected final String name;
    protected boolean enabled;
    protected List<String> description;
    protected ItemConstruct displayItem;
    protected String displayLine;
    protected int maxLevel;
    protected int requiredLevel;
    protected String permission;

    /**
     * Create a new augment instance with a name and description
     *
     * @param name        The name of the augment
     * @param description The description of the augment
     */
    public Augment(String name, String... description) {
        this.enabled = true;
        this.name = name;
        this.description = new ArrayList<>(List.of(description));
        this.maxLevel = 5;
        this.requiredLevel = 1;
        this.displayItem = this.defaultItem();
        this.displayLine = "&c" + StringUtils.capitalize(this.name.replace("_", " ")) + " %level_roman%";
        this.permission = "fishing.augment." + name;
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
        config.set("description", this.description);
        config.set("display-line", this.displayLine);

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
        this.description = config.getStringList("description");
        this.displayLine = config.getString("display-line", "&c" + StringUtils.capitalize(this.name.replace("_", " ")) + " %level_roman%");

        ItemConstruct construct = ItemConstruct.deserialize(config.getConfigurationSection("display-item"));
        if (construct == null) {
            FishingPlugin.get().getLogger().warning("Failed to load display item for augment: " + this.name);
            return;
        }

        this.displayItem = construct;
    }

    /**
     * The default item for the augment
     *
     * @return The default item
     */
    private ItemConstruct defaultItem() {
        return ItemConstruct.of(Material.FIREWORK_STAR)
                .name("&f[&#4f73d6&l%display_name%&f]")
                .lore("&7%description%",
                        "",
                        "&#4f73d6Information",
                        " &#4f73d6- &7Required Level: &f%required_level%",
                        " &#4f73d6- &7Max Level: &f%max_level%",
                        ""
                )
                .glow(true);
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
     * Get the namespace key for the augment lore, used to identify the augment in the lore of an item
     *
     * @return The namespace key
     */
    public NamespacedKey loreKey() {
        return new NamespacedKey(FishingPlugin.get(), this.name + "-lore");
    }

    /**
     * The placeholders for the augment when it is used
     *
     * @return The placeholders
     */
    public StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("id", this.name)
                .add("display_name", StringUtils.capitalize(this.name.replace("_", " ")))
                .add("max_level", this.maxLevel)
                .add("required_level", this.requiredLevel)
                .add("description", String.join("\n", this.description))
                .add("display_line", this.displayLine)
                .add("permission", this.permission)
                .add("enabled", this.enabled)
                .build();
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return this.description.isEmpty() ? List.of("No Description") : this.description;
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
    public final List<String> description() {
        return description;
    }

    /**
     * Set the description of the augment
     *
     * @param description The description of the augment
     */
    public void description(List<String> description) {
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

    /**
     * @return The lore line of the augment
     */
    public String displayLine() {
        return displayLine;
    }

    /**
     * Set the lore line of the augment
     *
     * @param loreLine The lore line of the augment
     */
    public void displayLine(String loreLine) {
        this.displayLine = loreLine;
    }

}
