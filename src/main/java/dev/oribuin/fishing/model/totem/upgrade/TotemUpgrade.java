package dev.oribuin.fishing.model.totem.upgrade;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * A totem upgrade is an upgrade that can be applied to a totem to enhance its abilities
 * <p>
 * TODO: Allow support for tiered costs for upgrades
 */
public abstract class TotemUpgrade extends FishEventHandler implements Configurable {

    private final String name; // The name of the upgrade
    private List<String> description; // The description of the upgrade
    private boolean enabled; // If the upgrade is enabled
    private ItemConstruct icon; // The icon of the upgrade
    private int defaultLevel; // The default level of the upgrade
    private int maxLevel; // The maximum level of the upgrade
    private String permission; // The permission required to purchase the upgrade

    /**
     * Create a new totem upgrade
     *
     * @param name        The name of the upgrade
     * @param description The description of the upgrade
     */
    public TotemUpgrade(String name, String... description) {
        this.name = name.toLowerCase();
        this.description = List.of(description);
        this.enabled = true;
        this.icon = defaultItem();
        this.defaultLevel = 0;
        this.maxLevel = 1;
        this.permission = "fishing.upgrade." + this.name;
    }

    /**
     * Apply the upgrade to the totem at the specified level
     *
     * @param totem The totem to apply the upgrade to
     * @param level The level of the upgrade
     */
    public void initialize(Totem totem, int level) {
        totem.applyProperty(DataType.INTEGER, this.key(), level);
    }

    /**
     * Upgrade the totem to the specified level of the upgrade
     *
     * @param player The person who is upgrading the totem
     * @param totem  The totem to upgrade
     *
     * @return If the upgrade was successful
     */
    public boolean levelup(Player player, Totem totem) {
        int level = totem.getProperty(this.key(), this.defaultLevel);
        if (level >= this.maxLevel) {
            player.sendMessage("You have reached the maximum level for this upgrade.");
            return false;
        }

        if (!player.hasPermission(this.permission)) {
            player.sendMessage("You do not have permission to purchase this upgrade.");
            return false;
        }

        // TODO: Cost check here

        level++;
        totem.applyProperty(DataType.INTEGER, this.key(), level); // Apply the upgrade to the totem
        totem.update(); // Update the totem to apply the changes 
        player.sendMessage("You have successfully upgraded your totem.");
        return true;
    }

    /**
     * The default {@link ItemConstruct} for the upgrade when displayed in a GUI
     *
     * @return The default {@link ItemConstruct} for the upgrade
     */
    public static ItemConstruct defaultItem() {
        return ItemConstruct.of(Material.HEART_OF_THE_SEA)
                .name("&f[&#4f73d6&l%name%&f]")
                .lore(
                        "&7%description%",
                        "",
                        "&#4f73d6Information",
                        " &#4f73d6- &7Current: &f%level%",
                        " &#4f73d6- &7Max Level: &f%max_level%",
                        ""
                )
                .glowing(true)
                .additionalTooltip(false);
    }

    /**
     * The totem upgrade placeholders for the upgrade.
     * All upgrades are added to the totems placeholders as "upgrade_<name>_<placeholder>"
     * <p>
     * Example: upgrade_radius_value
     *
     * @param totem The totem to apply the upgrade to
     *
     * @return The value of the upgrade
     */
    public StringPlaceholders placeholders(Totem totem) {
        StringPlaceholders.Builder base = StringPlaceholders.builder();
        base.add("name", StringUtils.capitalize(this.name));
        base.add("max_level", this.maxLevel);
        base.add("description", String.join("\n", this.description));

        if (totem != null) {
            base.add("level", totem.getProperty(this.key(), this.defaultLevel));
            base.add("next_level", Math.min(totem.getProperty(this.key(), this.defaultLevel) + 1, this.maxLevel));
            // todo: base.add("cost" , cost);
        }

        return base.build();
    }

    /**
     * Get the namespace key of the upgrade for use in the configuration file
     *
     * @return The namespace key of the upgrade
     */
    public NamespacedKey key() {
        return new NamespacedKey(FishingPlugin.get(), "upgrade_" + this.name);
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("totem", "upgrade", this.name.toLowerCase() + ".yml");
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
        config.set("description", this.description);
        config.set("max-level", this.maxLevel);
        config.set("permission", this.permission);

        this.icon.saveSettings(this.pullSection(config, "icon"));
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
        this.enabled = config.getBoolean("enabled", this.enabled);
        this.description = config.getStringList("description");
        this.maxLevel = config.getInt("max-level", this.maxLevel);
        this.permission = config.getString("permission", this.permission);

        this.icon = ItemConstruct.deserialize(this.pullSection(config, "icon"));
    }

    /**
     * Get the name of the upgrade
     *
     * @return The name of the upgrade
     */
    public String name() {
        return this.name;
    }

    /**
     * Get the description of the upgrade
     *
     * @return The description of the upgrade
     */
    public List<String> description() {
        return this.description;
    }

    /**
     * Set the description of the upgrade
     *
     * @param description The description of the upgrade
     */
    public void description(List<String> description) {
        this.description = description;
    }

    /**
     * Get if the upgrade is enabled
     *
     * @return If the upgrade is enabled
     */
    public boolean enabled() {
        return this.enabled;
    }

    /**
     * Set if the upgrade is enabled
     *
     * @param enabled If the upgrade is enabled
     */
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the icon of the upgrade
     *
     * @return The icon of the upgrade
     */
    public ItemConstruct icon() {
        return this.icon;
    }

    /**
     * Set the icon of the upgrade
     *
     * @param icon The icon of the upgrade
     */
    public void icon(ItemConstruct icon) {
        this.icon = icon;
    }

    /**
     * Get the default level of the upgrade
     *
     * @return The level of the upgrade
     */
    public int defaultLevel() {
        return this.defaultLevel;
    }

    /**
     * Set the default level of the upgrade
     *
     * @param defaultLevel The default level of the upgrade
     */
    public void defaultLevel(int defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    /**
     * Get the maximum level of the upgrade
     *
     * @return The maximum level of the upgrade
     */
    public int maxLevel() {
        return this.maxLevel;
    }

    /**
     * Set the maximum level of the upgrade
     *
     * @param maxLevel The maximum level of the upgrade
     */
    public void maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Get the permission required to purchase the upgrade
     *
     * @return The permission required to purchase the upgrade
     */
    public String permission() {
        return this.permission;
    }

    /**
     * Set the permission required to purchase the upgrade
     *
     * @param permission The permission required to purchase the upgrade
     */
    public void permission(String permission) {
        this.permission = permission;
    }

}
