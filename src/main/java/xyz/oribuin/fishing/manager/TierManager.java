package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.util.FishUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TierManager extends Manager {

    private final Map<String, Tier> tiers = new HashMap<>();
    private CommentedFileConfiguration config;

    public TierManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        File qualityFile = FishUtils.createFile(this.rosePlugin, "tiers.yml");
        this.config = CommentedFileConfiguration.loadConfiguration(qualityFile);
        CommentedConfigurationSection section = this.config.getConfigurationSection("tiers");

        // Make sure the section is not null
        if (section == null) {
            this.rosePlugin.getLogger().severe("No tiers have been found in the tiers.yml configuration file, Please double check your configuration file.");
            Bukkit.getPluginManager().disablePlugin(this.rosePlugin);
            return;
        }

        // Load all the tiers from the configuration file
        for (String key : section.getKeys(false)) {
            Tier tier = this.load(key);
            if (tier == null) continue;

            this.tiers.put(tier.name(), tier);
        }
    }

    /**
     * Get the quality of fish dependent on the chance provided, filters through all chances
     * sorted for rarest -> common, seeing if chance <= tier chance. When no tier is selected it will return null.
     * Usually, a null tier means a player wont get a custom fish
     *
     * @param chance The chance of obtaining the fish
     *
     * @return The fish that can be provided.
     */
    @Nullable
    public Tier selectTier(double chance) {
        List<Tier> tiers = new ArrayList<>(this.tiers.values());
        tiers.sort(Comparator.comparingDouble(Tier::chance)); // sort by chance
        Collections.reverse(tiers); // Put highest rarity first

        // Select the new fish :3
        return tiers.stream()
                .filter(x -> chance <= x.chance())
                .findFirst()
                .orElse(null);
    }

    /**
     * Load a tier from the configuration file
     *
     * @param key The key to load
     *
     * @return The loaded tier
     */
    public Tier load(String key) {
        CommentedConfigurationSection section = this.config.getConfigurationSection("tiers." + key.toLowerCase());
        if (section == null) {
            this.rosePlugin.getLogger().severe("Failed to load the tier: " + key + ". Section is null.");
            return null;
        }

        Tier tier = new Tier(key.toLowerCase());
        tier.loadSettings(section);
        return tier;
    }

    public Tier get(String key) {
        return this.tiers.get(key);
    }

    @Override
    public void disable() {
        this.tiers.clear(); // Clear the cached list of tiers
    }

    public Map<String, Tier> getTiers() {
        return this.tiers;
    }


}
