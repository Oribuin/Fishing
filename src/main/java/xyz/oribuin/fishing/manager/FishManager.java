package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.block.Biome;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.fish.condition.Time;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FishManager extends Manager {

    private final Map<String, Fish> fishTypes = new HashMap<>();

    public FishManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.rosePlugin.getManager(TierManager.class)
                .getQualityTypes()
                .forEach((s, tier) -> {
                    CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(FishUtils.createFile(this.rosePlugin, tier.getTierFile()));
                    CommentedConfigurationSection section = config.getConfigurationSection("fish");

                    // Make sure the section is not null
                    if (section == null) {
                        this.rosePlugin.getLogger().warning("Failed to load fish in tier: " + tier.getName());
                        return;
                    }

                    // Load all the fish from the config
                    section.getKeys(false).forEach(key -> {
                        Fish fish = this.load(config, tier, key);
                        if (fish != null) this.fishTypes.put(key, fish);
                    });
                });
    }

    /**
     * Load a fish from the tier config file
     *
     * @param config The config file
     * @param tier   The tier of the fish
     * @param key    The key of the fish
     *
     * @return The fish
     */
    public Fish load(CommentedFileConfiguration config, Tier tier, String key) {
        String path = ("fish." + key + ".").toLowerCase();
        String name = config.getString(path + "name");

        // Make sure the name is not null
        if (name == null) {
            this.rosePlugin.getLogger().warning("Failed to load fish with key: " + key + " in tier: " + tier.getName());
            return null;
        }

        // Load additional values from the config
        Fish fish = new Fish(name, tier.getName());

        // Catch Conditions
        fish.setDisplayName(config.getString(path + "display-name", name));
        fish.setDescription(config.getStringList(path + "description"));
        fish.setModelData(config.getInt(path + "model-data", -1));

        // Catch Conditions
        fish.setBiomes(FishUtils.getEnumList(Biome.class, config.getStringList(path + "biomes")));
        fish.setWeather(FishUtils.getEnum(Weather.class, config.getString(path + "weather")));
        fish.setTime(FishUtils.getEnum(Time.class, config.getString(path + "time")));
        return fish;
    }

    /**
     * Try to catch a fish from the tier based on the player's fishing rod and fish hook
     *
     * @param player  The player to check
     * @param rod     The fishing rod the player is using
     * @param hook    The fishhook the player is using
     *
     * @return The fish the player caught
     */
    public List<Fish> tryCatch(Player player, ItemStack rod, FishHook hook) {
        List<Fish> result = new ArrayList<>();
        // TODO: Check for augments on the fishing rod
        // TODO: Provide the player with entropy on catch, sometimes
        // TODO: Give statistics to the player
        result.add(this.generateFish(player, rod, hook));
        return result;
    }

    /**
     * Select a random fish from the tier based on the player's fishing rod and fish hook
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return The fish the player caught
     */
    private Fish generateFish(Player player, ItemStack rod, FishHook hook) {
        // Pick the quality of the fish based on the tier
        TierManager manager = this.rosePlugin.getManager(TierManager.class);
        double qualityChance = FishUtils.RANDOM.nextDouble(100);

        // Obtain the quality of the 
        Optional<Tier> quality = manager.getQualityTypes()
                .values()
                .stream()
                .filter(t -> qualityChance <= t.getChance())
                .findFirst();

        if (quality.isEmpty()) return null;

        // Make sure the quality is not null
        List<Fish> fishList = this.getFishByTier(quality.get()).stream()
                .filter(f -> f.canCatch(player, rod, hook))
                .toList();

        if (fishList.isEmpty()) return null;

        // Pick a random fish from the list
        return fishList.get(FishUtils.RANDOM.nextInt(fishList.size()));
    }

    @Override
    public void disable() {

    }

    /**
     * Get all the fish in a specific tier of fish
     *
     * @param tier The tier of fish
     *
     * @return The list of fish in the tier
     */
    public List<Fish> getFishByTier(Tier tier) {
        return this.fishTypes.values().stream().filter(fish -> fish.getTier().equalsIgnoreCase(tier.getName())).toList();
    }

    public Map<String, Fish> getFishTypes() {
        return fishTypes;
    }

}
