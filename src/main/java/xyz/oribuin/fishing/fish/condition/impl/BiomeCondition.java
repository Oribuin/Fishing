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

@SuppressWarnings("deprecation")
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

        // Use the old method if the server is not running paper
        if (!NMSUtil.isPaper()) {
            return biomes.contains(loc.getBlock().getBiome().name());
        }

        // Server is running paper
        List<NamespacedKey> biomeKeys = biomes.stream().map(NamespacedKey::fromString).toList();
        NamespacedKey current = Bukkit.getUnsafe().getBiomeKey(
                loc.getWorld(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );

        return biomeKeys.contains(current);
    }

}
