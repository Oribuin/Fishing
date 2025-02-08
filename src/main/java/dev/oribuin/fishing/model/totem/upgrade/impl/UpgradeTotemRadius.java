package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.economy.Cost;
import dev.oribuin.fishing.model.economy.CurrencyRegistry;
import dev.oribuin.fishing.model.economy.Currency;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeTotemRadius extends TotemUpgrade implements Configurable {

    private final Map<Integer, Tier> tiers = new HashMap<>();

    /**
     * The name of the totem upgrade to be displayed to the player
     *
     * @return The name of the upgrade
     */
    @Override
    public String name() {
        return "Radius";
    }

    /**
     * Apply the upgrade to the totem object
     *
     * @param totem The totem object to apply the upgrade to
     * @param level The level of the upgrade
     */
    @Override
    public void applyTo(Totem totem, int level) {
        Tier tier = this.tiers.get(level);
        if (tier == null) {
            FishingPlugin.get().getLogger().warning("Failed to apply upgrade: " + this.name() + " to totem, The tier does not exist.");
            return;
        }

        totem.radius(tier.newRadius());
    }

    /**
     * Get the cost of the upgrade for a specific level
     *
     * @param level The level of the upgrade
     *
     * @return The cost of the upgrade
     */
    @Override
    public Cost costFor(int level) {
        Tier tier = this.tiers.get(level);
        if (tier == null) {
            FishingPlugin.get().getLogger().warning("Failed to get cost for upgrade: " + this.name() + ", The tier does not exist.");
            return new Cost(CurrencyRegistry.ENTROPY, 0);
        }

        return this.tiers.get(level).cost();
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Totem Upgrade [Radius] - Increases the effective range of the totem",
                "",
                "This upgrade will increase the radius of the totem by a set amount"
        );
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
        this.tiers.clear();

        CommentedConfigurationSection tiers =  this.pullSection(config, "tiers");

        tiers.getKeys(false).forEach(key -> {
            CommentedConfigurationSection section = this.pullSection(tiers, key);
            int newRadius = section.getInt("radius", 0);

            // Add the tier to the list
            try {
                int level = Integer.parseInt(key);
                this.tiers.put(level, new Tier(Cost.of(this.pullSection(section, "cost")), newRadius));
            } catch (IllegalArgumentException ex) {
                FishingPlugin.get().getLogger().warning("Failed to load tier: " + key + " in upgrade: " + this.name() + ", The tier must be a number.");
            }
        });
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
        this.tiers.forEach((level, tier) -> {
            CommentedConfigurationSection section = this.pullSection(config, "tiers." + level);
            section.set("radius", tier.newRadius());

            CommentedConfigurationSection costSection = this.pullSection(section, "cost");
            tier.cost().saveSettings(costSection);  // Save the cost of the tier
        });
    }

    /**
     * Each tier of the upgrade and the cost to upgrade to that tier
     *
     * @param cost      The cost to upgrade to the tier
     * @param newRadius The new radius of the totem
     */
    public record Tier(Cost cost, int newRadius) {
    }

}
