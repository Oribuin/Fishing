package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.util.FishUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TierManager extends Manager {

    private final Map<String, Tier> qualityTypes = new HashMap<>();
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

        section.getKeys(false)
                .stream()
                .map(this::load).
                filter(Objects::nonNull)
                .forEach(tier -> this.qualityTypes.put(tier.getName(), tier));

        // Load all the tiers from the configuration file
        for (String key : section.getKeys(false)) {
            Tier tier = this.load(key);
            if (tier == null) continue;

            this.qualityTypes.put(tier.getName(), tier);
        }
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
        ItemStack baseDisplay = FishUtils.deserialize(this.config, path + ".display");

        // Make sure the base display item is not null
        if (baseDisplay == null) {
            this.rosePlugin.getLogger().severe("Failed to load the base display item for the tier: " + key);
            Bukkit.getPluginManager().disablePlugin(this.rosePlugin);
            return null;
        }

        // Create a new tier
        Tier tier = new Tier(key.toLowerCase(), money, chance, entropy, baseDisplay);

        try {
            File tierDirectory = new File(this.rosePlugin.getDataFolder(), "tiers");
            if (!tierDirectory.exists()) tierDirectory.mkdirs();

            File tierFile = new File(tierDirectory, key.toLowerCase() + ".yml");
            if (!tierFile.exists()) {
                tierFile.createNewFile();
            }

        } catch (IOException ex) {
            this.rosePlugin.getLogger().warning("Failed to save the tier file for the tier: " + key);
            return null;
        }

        return tier;
    }

    @Override
    public void disable() {

    }

    public Map<String, Tier> getQualityTypes() {
        return this.qualityTypes;
    }


}
