package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is responsible for loading all the tiers from the plugin's data folder and storing them in a map for easy access
 *
 * @see #reload() How the tiers are loaded
 * @see Tier#loadSettings(CommentedConfigurationSection) How Tier Settings are loaded
 */
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
     * Selects a tier based on the chance number provided by the player, this will return the tier
     * <p>
     * This will prioritise the rarest tier first, up to the most common tier. This way it won't almost always return the lowest rarity
     *
     * @param chance The chance of obtaining the fish
     *
     * @return The quality of fish that can be provided, or null if no tier was selected
     *
     * @see FishGenerateEvent to see how this is used in the plugin
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
     * Pull a reference to a cached tier based on the unique key provided
     * <p>
     * Used to get the tier from a fish or other classes
     *
     * @param key The identifier of the tier
     *
     * @return The {@link Tier} from the key
     */
    public Tier get(String key) {
        return this.tiers.get(key);
    }

    /**
     * Get a fish from all tiers based on the fish identifier provided
     *
     * @param key The identifier of the fish
     *
     * @return The fish instance, which are cached in the tier object
     *
     * @see Tier#fish() for all the fish caches
     */
    public Fish getFish(String key) {
        return this.tiers.values().stream()
                .map(x -> x.getFish(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get every single fish in the entire plugin from all the tiers
     *
     * @return All the fish from all the tiers
     */
    public List<Fish> fish() {
        List<Fish> fish = new ArrayList<>();
        this.tiers.values().forEach(x -> fish.addAll(x.fish().values()));
        return fish;
    }

    @Override
    public void disable() {
        this.tiers.clear(); // Clear the cached list of tiers
    }

    /**
     * @return All the stored tiers in the plugin
     */
    public Map<String, Tier> tiers() {
        return this.tiers;
    }


}
