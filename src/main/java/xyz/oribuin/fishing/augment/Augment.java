package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.api.event.FishEventHandler;
import xyz.oribuin.fishing.economy.Cost;
import xyz.oribuin.fishing.economy.Currencies;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Augments are upgrades that can be crafted and applied to fishing rods to give them unique abilities to help the player produce more fish.
 * <p>
 * Use this class to create a new augment for the plugin. Any augments created should be registered using {@link AugmentRegistry#register(Augment)}
 * <p>
 * All augment classes should be titled AugmentName and named in snake_case.
 */
public abstract class Augment extends FishEventHandler implements Listener, Configurable {

    protected final String name;
    protected boolean enabled;
    protected List<String> description;
    protected ItemConstruct displayItem;
    protected String displayLine;
    protected int maxLevel;
    protected int requiredLevel;
    protected String permission;
    protected Cost price;

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     * <p>
     *
     * @param name        The unique name and identifier of the augment
     * @param description The description of the augment that will be displayed in the GUI
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
        this.price = Cost.of(Currencies.ENTROPY.get(), 25000);
    }

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     * <p>
     *
     * @param name The unique name and identifier of the augment
     */
    public Augment(String name) {
        this(name, "No Description");
    }

    /**
     * The file path to a {@link CommentedFileConfiguration} file, This path by default will be relative {@link #parentFolder()}.
     * <p>
     * This by default is only used in the {@link #reload()} method to load the configuration file
     * <p>
     * This an optional method and should only be used if the Configurable class is its own file (E.g. {@link xyz.oribuin.fishing.augment.Augment} class)
     *
     * @return The path to the configuration file
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("augments", this.name + ".yml");
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
        config.addComments(this.comments().toArray(new String[0]));
        config.set("enabled", this.enabled);
        config.set("max-level", this.maxLevel);
        config.set("required-level", this.requiredLevel);
        config.set("description", this.description);
        config.set("display-line", this.displayLine);

        this.displayItem.saveSettings(this.pullSection(config, "display-item")); // Save the display item
        this.price.saveSettings(this.pullSection(config, "price")); // Save the cost
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
        this.enabled = config.getBoolean("enabled", true);
        this.maxLevel = config.getInt("max-level", 1);
        this.requiredLevel = config.getInt("required-level", 1);
        this.description = config.getStringList("description");
        this.displayLine = config.getString("display-line", "&c" + StringUtils.capitalize(this.name.replace("_", " ")) + " %level_roman%");

        this.displayItem.loadSettings(this.pullSection(config, "display-item"));
        this.price.loadSettings(this.pullSection(config, "price"));
    }

    /**
     * The default item for the augment
     *
     * @return The default item
     */
    private ItemConstruct defaultItem() {
        List<String> lore = new ArrayList<>(this.description);
        lore.addAll(List.of(
                "",
                "&#4f73d6Information",
                " &#4f73d6- &7Required Level: &f%required_level%",
                " &#4f73d6- &7Max Level: &f%max_level%",
                ""
        ));

        return ItemConstruct.of(Material.FIREWORK_STAR)
                .name("&f[&#4f73d6&l%display_name%&f]")
                .lore(lore)
                .glowing(true);
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
                .add("display_name", FishUtils.capitalizeFully(this.name.replace("_", " ")))
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
