package dev.oribuin.fishing.augment;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.economy.Currencies;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.model.item.ItemConstruct;

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
public abstract class Augment extends FishEventHandler implements Configurable {

    private final String name;
    private boolean enabled;
    private List<String> description;
    private ItemConstruct displayItem;
    private String displayLine;
    private int maxLevel;
    private int requiredLevel;
    private String permission;
    private Cost price;

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
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
     * This an optional method and should only be used if the Configurable class is its own file (E.g. {@link dev.oribuin.fishing.augment.Augment} class)
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
     * The base itemstack design for the augment, this will be handed to players and shown in the codex
     * <p>
     * TODO: Replace this method with a global itemstack registry
     *
     * @return The default {@link ItemConstruct} for the augment
     */
    private final ItemConstruct defaultItem() {
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
     * The {@link NamespacedKey} for the augment, used to identify the augment in the plugin
     *
     * @return The namespace key, typically this will be "fishing:augment_name"
     */
    public final NamespacedKey key() {
        return new NamespacedKey(FishingPlugin.get(), this.name);
    }

    /**
     * The {@link NamespacedKey} for the lore of the augment, used to identify which line in the item description belongs to the augment
     *
     * @return The namespace key for the lore of the augment, typically this will be "fishing:augment_name-lore"
     */
    public final NamespacedKey loreKey() {
        return new NamespacedKey(FishingPlugin.get(), this.name + "-lore");
    }

    /**
     * All the placeholders that can be used when displaying information about the augment
     *
     * @return The {@link StringPlaceholders} for the augment
     */
    public final StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("enabled", this.enabled)
                .add("id", this.name)
                .add("display_name", FishUtils.capitalizeFully(this.name.replace("_", " ")))
                .add("max_level", this.maxLevel)
                .add("required_level", this.requiredLevel)
                .add("description", String.join("\n", this.description))
                .add("display_line", this.displayLine)
                .add("permission", this.permission)
                .build();
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return this.description.isEmpty() ? List.of("No Description") : this.description;
    }

    /**
     * Checks if the augment is enabled
     *
     * @return true if the augment is enabled
     */
    public final boolean enabled() {
        return enabled;
    }

    /**
     * Set if the augment is enabled
     *
     * @param enabled If the augment is enabled
     */
    public final void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * the name of the augment
     *
     * @return The augment name / id
     */
    public final String name() {
        return name;
    }

    /**
     * The description of the augment
     *
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
    public final void description(List<String> description) {
        this.description = description;
    }

    /**
     * The augments' display item that will be shown in the codex and given to players
     *
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
     * The maximum level this augment can reach when applied to a fishing rod
     *
     * @return The max level of the augment
     */
    public final int maxLevel() {
        return maxLevel;
    }

    /**
     * Set the max level of the augment
     *
     * @param maxLevel The max level of the augment
     */
    public final void maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * The required fishing level to use and apply the augment
     *
     * @return The required level of the augment
     */
    public final int requiredLevel() {
        return requiredLevel;
    }

    /**
     * Set the required level of the augment
     *
     * @param requiredLevel The required level of the augment
     */
    public final void requiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    /**
     * The line that will be described in the lore of the fishing rod when the augment is applied
     *
     * @return The display line of the augment
     */
    public final String displayLine() {
        return displayLine;
    }

    /**
     * Set the display line of the augment
     *
     * @param loreLine The lore line of the augment
     */
    public final void displayLine(String loreLine) {
        this.displayLine = loreLine;
    }

    /**
     * The required permission to use the augment in the plugin
     *
     * @return The permission required to use the augment
     */
    public final String permission() {
        return permission;
    }

    /**
     * Set the permission required to use the augment
     *
     * @param permission The permission required to use the augment
     */
    public final void permission(String permission) {
        this.permission = permission;
    }

    /**
     * The cost of the augment in the plugin
     * <p>
     * TODO: Needs to be moved into {@link dev.oribuin.fishing.api.recipe.Recipe} class when implemented
     *
     * @return The cost of the augment
     */
    public final Cost price() {
        return price;
    }

    /**
     * Set the cost of the augment
     *
     * @param price The cost of the augment
     */
    public final void price(Cost price) {
        this.price = price;
    }

}

