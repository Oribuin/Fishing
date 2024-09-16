package xyz.oribuin.fishing.api.condition.impl;

import org.bukkit.block.Biome;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Condition;
import xyz.oribuin.fishing.fish.Fish;

public class BiomeCondition implements CatchCondition {

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
        Condition con = fish.condition();
        if (con.biomes() == null || con.biomes().isEmpty()) return true;

        Biome biome = hook.getLocation().getBlock().getBiome();
        return con.biomes().contains(biome);
    }

}
