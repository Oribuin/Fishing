package dev.oribuin.fishing.model.totem.upgrade;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.util.Placeholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.List;

/**
 * A totem upgrade is an upgrade that can be applied to a totem to enhance its abilities
 * <p>
 * TODO: Allow support for tiered costs for upgrades
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public abstract class TotemUpgrade extends FishEventHandler {

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
        return new ItemConstruct(Material.HEART_OF_THE_SEA)
                .setName("&f[&#4f73d6&l%name%&f]")
                .setLore(
                        "<gray>%description%",
                        "",
                        "&#4f73d6Information",
                        " &#4f73d6- <gray>Current: &f%level%",
                        " &#4f73d6- <gray>Max Level: &f%max_level%",
                        ""
                )
                .setGlowing(true);
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
    public Placeholders placeholders(Totem totem) {
        Placeholders.Builder base = Placeholders.builder();
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
