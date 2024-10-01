package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.ItemConstruct;

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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Tier load(String key) {
        String path = "tiers." + key.toLowerCase() + ".";

        // Load the tier from the configuration file
        double chance = this.config.getDouble(path + "chance", 0);
        double money = this.config.getDouble(path + "money", 0);
        int entropy = this.config.getInt(path + "entropy", 0);

        CommentedConfigurationSection section = this.config.getConfigurationSection(path + "display");
        if (section == null) {
            this.rosePlugin.getLogger().severe("Failed to load the base display item for the tier: " + key + ". Display section is null.");
            return null;
        }

        ItemConstruct construct = ItemConstruct.deserialize(section);
        if (construct == null) {
            this.rosePlugin.getLogger().severe("Failed to load the base display item for the tier: " + key + ". Display construct is null.");
            return null;
        }

        // Make sure the base display item is not null
        if (construct.build() == null) {
            this.rosePlugin.getLogger().severe("Failed to load the base display item for the tier: " + key + ". Display item is null.");
            return null;
        }

        // Make sure there is actually a chance to obtain the tier
        if (chance <= 0) {
            this.rosePlugin.getLogger().severe("The chance for the tier: " + key + " is invalid. Please double check your configuration file.");
            return null;
        }

        // Create a new tier, Config files are created when a tier is instantiated
        Tier tier = new Tier(key.toLowerCase(), money, chance, entropy, construct);
        tier.fishExp((float) this.config.getDouble(path + "fish-exp", 0));
        tier.naturalExp((float) this.config.getDouble(path + "natural-exp", 0));
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
