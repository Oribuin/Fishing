package xyz.oribuin.fishing.fish.condition.impl;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import xyz.oribuin.fishing.fish.Fish;

import java.util.List;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see xyz.oribuin.fishing.fish.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook) to see how this is used
 */
@SuppressWarnings({ "deprecation", "removal" })
public class BiomeCondition implements CatchCondition {

    /**
     * A condition that checks if the player is fishing in a specific biome
     */
    private BiomeCondition() {}

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
        return fish.condition().biomes() != null && !fish.condition().biomes().isEmpty();
    }

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link xyz.oribuin.fishing.fish.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)}
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
        List<String> biomes = fish.condition().biomes();

        // 1.21.3+ Biome Check
        if (NMSUtil.getVersionNumber() > 21 && NMSUtil.getMinorVersionNumber() >= 3) {
            String value = this.keyValue(loc.getBlock().getBiome().getKey());
            return biomes.stream().anyMatch(value::equalsIgnoreCase);
        }

        // 1.21.2 and below Biome Check
        NamespacedKey current = Bukkit.getUnsafe().getBiomeKey(
                loc.getWorld(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );

        return biomes.stream().anyMatch(x -> this.keyValue(current).equalsIgnoreCase(x));
    }

    /**
     * Get the key value of the biome
     *
     * @param key The key to get the value of
     *
     * @return The value of the key
     */
    private String keyValue(NamespacedKey key) {
        return key.value();
    }
}
