package xyz.oribuin.fishing.fish.condition.impl;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.biomeadapter.BiomeAdapter;
import xyz.oribuin.biomeadapter.api.BiomeHandler;
import xyz.oribuin.biomeadapter.api.BiomeWrapper;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

import java.util.List;

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
        BiomeHandler handler = BiomeAdapter.get();
        List<String> biomes = fish.condition().biomes();
        boolean datapackSupported = handler != null && handler.getNoiseBiome(hook.getLocation()) != null;

        // If the NMS Version is not supported, use bukkit's default biome check
        if (!datapackSupported) {
            return biomes.contains(hook.getLocation().getBlock().getBiome().name());
        }

        BiomeWrapper<?> biomeWrapper = handler.getNoiseBiome(hook.getLocation());
        if (biomeWrapper == null) return false;

        // If the biome is null, return false
        String[] split = biomeWrapper.getKey().getNamespace().split(":");
        if (split.length != 2) return false; // If the namespace is not valid, return false

        // Support for "plains" or "minecraft:plains"
        return biomes.contains(split[0]) || biomes.contains(biomeWrapper.getKey().namespace());
    }

}
