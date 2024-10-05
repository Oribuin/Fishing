package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.event.FishGenerateEvent;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.Tier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishManager extends Manager {

    private final Map<String, Fish> fishTypes = new HashMap<>();

    public FishManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.rosePlugin.getManager(TierManager.class)
                .getTiers()
                .forEach((s, tier) -> {
                    CommentedFileConfiguration config = tier.config();
                    CommentedConfigurationSection section = config.getConfigurationSection("fish");

                    // Make sure the section is not null
                    if (section == null) {
                        this.rosePlugin.getLogger().warning("No fish have been found in the " + tier.name() + " tier configuration file. Creating default fish.");
                        section = config.createSection("fish");

                        Fish defaultFish = new Fish(tier.name() + "-default", tier.name());
                        defaultFish.saveSettings(section);

                        try {
                            config.save(tier.tierFile());
                        } catch (Exception e) {
                            this.rosePlugin.getLogger().warning("Failed to save default fish to the " + tier.name() + " tier configuration file.");
                        }
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
        CommentedConfigurationSection section = config.getConfigurationSection("fish." + key);
        if (section == null) return null;

        String name = section.getString("name");

        // Make sure the name is not null
        if (name == null) {
            this.rosePlugin.getLogger().warning("Failed to load fish with key: " + key + " in tier: " + tier.name());
            return null;
        }

        // Load additional values from the config
        Fish fish = new Fish(name, tier.name());
        fish.loadSettings(section);
        return fish;
    }

    /**
     * Try to catch a fish from the tier based on the player's fishing rod and fishhook
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return The fish the player caught
     */
    public List<Fish> tryCatch(Player player, ItemStack rod, FishHook hook) {
        List<Fish> result = new ArrayList<>();
        Map<Augment, Integer> augments = AugmentRegistry.from(rod);

        InitialFishCatchEvent event = new InitialFishCatchEvent(player, rod, hook);
        event.callEvent();

        // Run the augments onInitialCatch method
        augments.forEach((augment, integer) -> augment.onInitialCatch(event, integer));

        // Cancel the event if it is cancelled
        if (event.isCancelled()) return result;

        for (int i = 0; i < event.getAmountToCatch(); i++) {
            result.add(this.generateFish(player, rod, hook));
        }

        return result;
    }

    /**
     * Fires the {@link FishGenerateEvent} and returns the fish
     * This generates its own fish that can be overridden by augments or other plugins.
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return The fish the player caught
     */
    private Fish generateFish(Player player, ItemStack rod, FishHook hook) {
        FishGenerateEvent event = new FishGenerateEvent(player, rod, hook);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        return event.getFish();
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
        return this.fishTypes.values()
                .stream()
                .filter(fish -> fish.tierName().equalsIgnoreCase(tier.name()))
                .toList();
    }

    public Map<String, Fish> getFishTypes() {
        return fishTypes;
    }

}
