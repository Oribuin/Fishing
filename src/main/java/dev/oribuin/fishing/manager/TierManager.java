package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.util.FishUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is responsible for loading all the tiers from the plugin's data folder and storing them in a map for easy access
 */
public class TierManager implements Manager {

    private final FishingPlugin plugin;
    private final Map<String, Tier> tiers;

    public TierManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.tiers = new HashMap<>();
        this.reload(this.plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        File tierFolder = new File(this.plugin.getDataFolder(), "tiers");

        // Load all the tiers from the config files in the folder
        File[] content = tierFolder.listFiles();
        if (content == null) {
            FishUtils.createFile(this.plugin, "tiers", "bronze.yml");
            FishUtils.createFile(this.plugin, "tiers", "silver.yml");
            FishUtils.createFile(this.plugin, "tiers", "gold.yml");
            FishUtils.createFile(this.plugin, "tiers", "diamond.yml");
            FishUtils.createFile(this.plugin, "tiers", "platinum.yml");
            FishUtils.createFile(this.plugin, "tiers", "mythic.yml");
            content = tierFolder.listFiles();
        }

        if (content == null) return;

        for (File file : content) {
            if (file.isDirectory()) return; // we're not subdirectories
            if (!file.getName().endsWith(".yml")) return; // it's not a yml file

            Tier tier = new Tier(file).getConfigHandler().getConfig();

            if (tier.getFish() == null || tier.getFish().isEmpty()) {
                this.plugin.getLogger().warning("Tier file[" + file.getName() + "] does not have any fish located, This will be skipped");
                return;
            }


            // update the "tier" on all the fish & set tier name
            Map<String, Fish> current = new HashMap<>(tier.getFish());
            for (Map.Entry<String, Fish> entry : current.entrySet()) {
                entry.getValue().setName(entry.getKey().toLowerCase());
                entry.getValue().setTier(tier.getName().toLowerCase());
            }
            
            tier.setFish(current);
            this.tiers.put(tier.getName().toLowerCase(), tier);
        }
        
        this.plugin.getLogger().info("Loaded a total of [" + this.tiers.size() + "] tiers with [" + this.getAllFish().size() + "] fish");
        
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
        for (Tier tier : new HashMap<>(this.tiers).values()) {
            tier.getConfigHandler().unload();
        }
        
        this.tiers.clear();
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
        tiers.sort(Comparator.comparingDouble(Tier::getChance)); // sort by chance
        Collections.reverse(tiers); // Put highest rarity first

        // Select the new fish :3
        return tiers.stream()
                .filter(x -> chance <= x.getChance())
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
     * @see Tier#getFish() for all the fish caches
     */
    public Fish getFish(String key) {
        return this.tiers.values().stream()
                .map(x -> x.getFish().get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get every single fish in the entire plugin from all the tiers
     *
     * @return All the fish from all the tiers
     */
    public List<Fish> getAllFish() {
        List<Fish> fish = new ArrayList<>();
        this.tiers.values().forEach(x -> fish.addAll(x.getFish().values()));
        return fish;
    }

    /**
     * @return All the stored tiers in the plugin
     */
    public Map<String, Tier> getTiers() {
        return this.tiers;
    }


}
