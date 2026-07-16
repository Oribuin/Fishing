package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.item.ConstructComponent;
import dev.oribuin.fishing.config.item.ConstructType;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.persistent.PDCSerializable;
import dev.oribuin.fishing.util.Placeholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

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
    protected String description; // The description of the upgrade
    protected ItemConstruct icon; // The icon of the upgrade
    protected int defaultLevel; // The default level of the upgrade
    protected int maxLevel; // The maximum level of the upgrade
    protected String permission; // The permission required to purchase the upgrade

    private static final ItemConstruct BASE_UPGRADE = ItemConstruct.of(Material.HEART_OF_THE_SEA) // Upgrades will choose their own item, idgaf
            .setName("<white>[<#94bc80><bold><name></bold><white>]")
            .setLore(
                    "<gray><description>",
                    "",
                    "<#94bc80>Information",
                    " <#94bc80>- <white>Current: <#94bc80><level>",
                    " <#94bc80>- <white>Max Level: <#94bc80><max_level>",
                    ""
            )
            .setProperty(ConstructType.GLOWING, ConstructComponent::setEnabled);

    public TotemUpgrade() {
        this.enabled = true;
        this.name = StringUtils.capitalize(this.getIdentifier().get());
        this.description = "Allows the totem to do something new";
        this.defaultLevel = 1;
        this.maxLevel = 1;
        this.icon = BASE_UPGRADE.clone();
        this.permission = "fishing.upgrade." + this.name.toLowerCase();
        this.level = this.defaultLevel;
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
        totem.writeContainer(display.getPersistentDataContainer());
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
        return Placeholders.builder()
                .add("enabled", this.enabled)
                .add("name", this.name)
                .add("description", this.description)
                .add("default_level", this.defaultLevel)
                .add("max_level", this.maxLevel)
                .add("level", this.level)
                .add("permission", this.permission)
                .build();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

}
