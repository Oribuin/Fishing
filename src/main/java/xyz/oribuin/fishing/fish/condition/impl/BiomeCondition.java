package xyz.oribuin.fishing.fish.condition.impl;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

import java.util.List;


@SuppressWarnings({ "deprecation", "removal" })
public class BiomeCondition implements CatchCondition {

    /**
     * Check if the requirements are met to run the condition
     *
     * @param fish The fish to check
     *
     * @return Results in true if the condition should run
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return fish.condition().biomes() != null && !fish.condition().biomes().isEmpty();
    }

    /**
     * Check if the player can catch the fish with the current conditions
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
