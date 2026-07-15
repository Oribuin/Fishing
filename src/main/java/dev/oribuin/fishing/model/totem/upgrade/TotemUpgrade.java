package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.persistent.PDCSerializable;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.oribuin.fishing.storage.util.KeyRegistry.TOTEM_UPGRADE_LEVEL;

/**
 * A totem upgrade is an upgrade that can be applied to a totem to enhance its abilities
 * <p>
 * TODO: Allow support for tiered costs for upgrades
 */

public abstract class TotemUpgrade extends FishEventHandler implements PDCSerializable {

    protected transient int level;
    protected boolean enabled; // If the upgrade is enabled
    protected String name; // The name of the upgrade
    protected List<String> description; // The description of the upgrade
    protected ItemConstruct icon; // The icon of the upgrade
    protected int defaultLevel; // The default level of the upgrade
    protected int maxLevel; // The maximum level of the upgrade
    protected String permission; // The permission required to purchase the upgrade

    public TotemUpgrade() {
        this.enabled = true;
        this.name = this.getIdentifier().get();
        this.description = new ArrayList<>();
        this.defaultLevel = 1;
        this.maxLevel = 1;
        this.permission = "fishing.upgrade." + this.name.toLowerCase();
    }

    /**
     * Upgrade the totem to the specified level of the upgrade
     *
     * @param player The person who is upgrading the totem
     * @param totem  The totem to upgrade
     *
     * @return If the upgrade was successful
     */
    public boolean increaseLevel(Player player, Totem totem) {
        ArmorStand display = totem.getDisplay();
        if (display == null) {
            player.sendMessage("no totem display to upgrade todo add message for this");
            return false;
        }

        if (this.level >= this.maxLevel) {
            PluginMessages.get().getHitMaxLevel().send(player,
                    "level", this.level,
                    "max", this.maxLevel
            );
            return false;
        }

        if (!player.hasPermission(this.permission)) {
            PluginMessages.get().getNoPermission().send(player);
            return false;
        }

        // TODO: Minimum level for upgrade (maybe tiered e.g. level 5 totem = max level 2 upgrade)
        // TODO: Cost check here
        this.level++;
        this.writeContainer(display.getPersistentDataContainer());
        PluginMessages.get().getTotem().getUpgradeLevelUp().send(player,
                "level", this.level,
                "max", this.maxLevel,
                "upgrade", this.name
        );
        return true;
    }

    /**
     * Get the additional placeholders for a totem upgrade
     *
     * @param totem The totem with the upgrade
     *
     * @return The resulting placeholders
     */
    public @NotNull Placeholders getPlaceholders(@NotNull Totem totem) {
        //     protected transient int level;
        //    protected boolean enabled; // If the upgrade is enabled
        //    protected String name; // The name of the upgrade
        //    protected List<String> description; // The description of the upgrade
        //    protected ItemConstruct icon; // The icon of the upgrade
        //    protected int defaultLevel; // The default level of the upgrade
        //    protected int maxLevel; // The maximum level of the upgrade
        //    protected String permission; // The permission required to purchase the upgrade
        return Placeholders.empty();
    }

    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    public abstract Supplier<String> getIdentifier();

    /**
     * Load and deserialize data from a data container
     *
     * @param container The container to read from
     */
    @Override
    public void readContainer(PersistentDataContainer container) {
        this.level = container.getOrDefault(TOTEM_UPGRADE_LEVEL.key(), TOTEM_UPGRADE_LEVEL, this.defaultLevel);
    }

    /**
     * Write data into a data container
     *
     * @param container The container to write into
     */
    @Override
    public void writeContainer(PersistentDataContainer container) {
        container.set(TOTEM_UPGRADE_LEVEL.key(), TOTEM_UPGRADE_LEVEL, this.level);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public ItemConstruct getIcon() {
        return icon;
    }

    public void setIcon(ItemConstruct icon) {
        this.icon = icon;
    }

    public int getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(int defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
    //
    //    /**
    //     * The totem upgrade placeholders for the upgrade.
    //     * All upgrades are added to the totems placeholders as "upgrade_<name>_<placeholder>"
    //     * <p>
    //     * Example: upgrade_radius_value
    //     *
    //     * @param totem The totem to apply the upgrade to
    //     *
    //     * @return The value of the upgrade
    //     */
    //    public Placeholders getPlaceholders(Totem totem) {
    //        Placeholders.Builder base = Placeholders.builder();
    //        base.add("name", StringUtils.capitalize(this.name));
    //        base.add("max_level", this.maxLevel);
    //        base.add("description", String.join("\n", this.description));
    //
    //        if (totem != null) {
    //            base.add("level", totem.getProperty(this.getKey(), this.defaultLevel));
    //            base.add("next_level", Math.min(totem.getProperty(this.getKey(), this.defaultLevel) + 1, this.maxLevel));
    //            // todo: base.add("cost" , cost);
    //        }
    //
    //        return base.build();
    //    }

    //    /**
    //     * Get the namespace key of the upgrade for use in the configuration file
    //     *
    //     * @return The namespace key of the upgrade
    //     */
    //    public NamespacedKey getKey() {
    //        return new NamespacedKey(FishingPlugin.get(), "upgrade_" + this.name);
    //    }
    //
    //    /**
    //     * Get the name of the upgrade
    //     *
    //     * @return The name of the upgrade
    //     */
    //    public String getName() {
    //        return this.name;
    //    }
    //
    //    /**
    //     * Get the description of the upgrade
    //     *
    //     * @return The description of the upgrade
    //     */
    //    public List<String> getDescription() {
    //        return this.description;
    //    }
    //
    //    /**
    //     * Set the description of the upgrade
    //     *
    //     * @param description The description of the upgrade
    //     */
    //    public void setDescription(List<String> description) {
    //        this.description = description;
    //    }
    //
    //    /**
    //     * Get if the upgrade is enabled
    //     *
    //     * @return If the upgrade is enabled
    //     */
    //    public boolean isEnabled() {
    //        return this.enabled;
    //    }
    //
    //    /**
    //     * Set if the upgrade is enabled
    //     *
    //     * @param enabled If the upgrade is enabled
    //     */
    //    public void setEnabled(boolean enabled) {
    //        this.enabled = enabled;
    //    }
    //
    //    /**
    //     * Get the icon of the upgrade
    //     *
    //     * @return The icon of the upgrade
    //     */
    //    public ItemConstruct getIcon() {
    //        return this.icon;
    //    }
    //
    //    /**
    //     * Set the icon of the upgrade
    //     *
    //     * @param icon The icon of the upgrade
    //     */
    //    public void setIcon(ItemConstruct icon) {
    //        this.icon = icon;
    //    }
    //
    //    /**
    //     * Get the default level of the upgrade
    //     *
    //     * @return The level of the upgrade
    //     */
    //    public int getDefaultLevel() {
    //        return this.defaultLevel;
    //    }
    //
    //    /**
    //     * Set the default level of the upgrade
    //     *
    //     * @param defaultLevel The default level of the upgrade
    //     */
    //    public void setDefaultLevel(int defaultLevel) {
    //        this.defaultLevel = defaultLevel;
    //    }
    //
    //    /**
    //     * Get the maximum level of the upgrade
    //     *
    //     * @return The maximum level of the upgrade
    //     */
    //    public int getMaxLevel() {
    //        return this.maxLevel;
    //    }
    //
    //    /**
    //     * Set the maximum level of the upgrade
    //     *
    //     * @param maxLevel The maximum level of the upgrade
    //     */
    //    public void setMaxLevel(int maxLevel) {
    //        this.maxLevel = maxLevel;
    //    }
    //
    //    /**
    //     * Get the permission required to purchase the upgrade
    //     *
    //     * @return The permission required to purchase the upgrade
    //     */
    //    public String getPermission() {
    //        return this.permission;
    //    }
    //
    //    /**
    //     * Set the permission required to purchase the upgrade
    //     *
    //     * @param permission The permission required to purchase the upgrade
    //     */
    //    public void setPermission(String permission) {
    //        this.permission = permission;
    //    }

}
