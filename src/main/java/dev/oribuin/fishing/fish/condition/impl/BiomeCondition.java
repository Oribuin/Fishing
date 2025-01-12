package dev.oribuin.fishing.fish.condition.impl;

import dev.oribuin.fishing.api.condition.CatchCondition;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.fish.Fish;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.fish.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@SuppressWarnings({ "deprecation", "removal" })
public class BiomeCondition extends CatchCondition {

    private List<String> biomes = new ArrayList<>(); // List of biomes to check for

    /**
     * A condition that checks if the player is fishing in a specific biome
     */
    public BiomeCondition() {}

    /**
     * Decides whether the condition should be checked in the first place,
     * <p>
     * This is to prevent unnecessary checks on fish that don't have the condition type.
     *
     * @param fish The fish to check for
     *
     * @return true if the fish has the condition applied. @see {@link #check(Fish, Player, ItemStack, FishHook)} for the actual condition check
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return this.biomes.isEmpty();
    }

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link dev.oribuin.fishing.fish.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)}
     * <p>
     * All conditions are passed through {@link ConditionCheckEvent} to overwrite the result if needed
     *
     * @param fish   The fish the player is trying to catch
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return Results in true if the player can catch the fish
     */
    @Override
    public boolean check(Fish fish, Player player, ItemStack rod, FishHook hook) {
        Location loc = hook.getLocation();

        // 1.21.3+ Biome Check
        String biomeKey;
        if (NMSUtil.getVersionNumber() > 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            biomeKey = this.keyValue(loc.getBlock().getBiome().getKey());
        } else {
            biomeKey = this.keyValue(Bukkit.getUnsafe().getBiomeKey(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }

        String finalBiomeKey = biomeKey;
        return this.biomes.stream().anyMatch(x -> {
            if (x.startsWith("!")) return !x.substring(1).equalsIgnoreCase(finalBiomeKey);
            else return x.equalsIgnoreCase(finalBiomeKey);
        });
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public StringPlaceholders placeholders() {
        return StringPlaceholders.of("biomes", this.biomes.isEmpty() ? "Any" : String.join(", ", this.biomes));
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
        this.biomes = config.getStringList("biomes");
    }

    /**
     * Get the key value of the biome
     *
     * @param key The key to get the value of
     *
     * @return The value of the key
     */
    private String keyValue(NamespacedKey key) {
        if (key == null) return null;

        return key.value();
    }

}