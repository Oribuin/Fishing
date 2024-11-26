package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
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

    public TierManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        File tierFolder = new File(this.rosePlugin.getDataFolder(), "tiers");

        // Load all the tiers from the config files in the folder
        File[] content = tierFolder.listFiles();
        if (content == null) {
            FishUtils.createFile(this.rosePlugin, "tiers", "bronze.yml");
            FishUtils.createFile(this.rosePlugin, "tiers", "silver.yml");
            FishUtils.createFile(this.rosePlugin, "tiers", "gold.yml");
            FishUtils.createFile(this.rosePlugin, "tiers", "diamond.yml");
            FishUtils.createFile(this.rosePlugin, "tiers", "platinum.yml");
            FishUtils.createFile(this.rosePlugin, "tiers", "mythic.yml");
            content = tierFolder.listFiles();
        }

        if (content == null) return;

        for (File file : content) {
            if (file.isDirectory()) return; // we're not subdirectories
            if (!file.getName().endsWith(".yml")) return; // it's not a yml file

            CommentedFileConfiguration tierConfig = CommentedFileConfiguration.loadConfiguration(file);

            Tier tier = new Tier(file.getName().replace(".yml", ""));
            tier.loadSettings(tierConfig); // Load the tier settings

            this.tiers.put(tier.name(), tier);
        }
    }

    /**
     * Get the quality of fish dependent on the chance provided, filters through all chances
     * sorted for rarest -> common, seeing if chance <= tier chance. When no tier is selected it will return null.
     * Usually, a null tier means a player won't get a custom fish
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
