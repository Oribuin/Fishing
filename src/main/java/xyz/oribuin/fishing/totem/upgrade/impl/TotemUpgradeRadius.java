package xyz.oribuin.fishing.totem.upgrade.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.api.economy.Cost;
import xyz.oribuin.fishing.api.economy.Currency;
import xyz.oribuin.fishing.totem.Totem;
import xyz.oribuin.fishing.api.totem.TotemUpgrade;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotemUpgradeRadius extends TotemUpgrade implements Configurable {

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
            return new Cost(Currency.ENTROPY, 0);
        }

        return this.tiers.get(level).cost();
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Totem Upgrade [Radius] - Increases the effective range of the totem",
                "This upgrade will increase the radius of the totem by a set amount"
        );
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.tiers.clear();

        CommentedConfigurationSection tiers = config.getConfigurationSection("tiers");
        if (tiers == null) return;

        tiers.getKeys(false).forEach(key -> {
            CommentedConfigurationSection section = tiers.getConfigurationSection(key);
            if (section == null) return;

            Currency currency = FishUtils.getEnum(Currency.class, section.getString("currency"), Currency.ENTROPY);
            int amount = section.getInt("cost", 0);
            int newRadius = section.getInt("radius", 0);

            Cost cost = new Cost(currency, amount);

            // Add the tier to the list
            try {
                int level = Integer.parseInt(key);
                this.tiers.put(level, new Tier(cost, newRadius));
            } catch (IllegalArgumentException ex) {
                FishingPlugin.get().getLogger().warning("Failed to load tier: " + key + " in upgrade: " + this.name() + ", The tier must be a number.");
            }
        });
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        this.tiers.forEach((level, tier) -> {
            CommentedConfigurationSection section = config.createSection("tiers." + level);
            section.set("currency", tier.cost().currency().name());
            section.set("cost", tier.cost().amount());
            section.set("radius", tier.newRadius());
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
