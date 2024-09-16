package xyz.oribuin.fishing.api.condition.impl;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

import java.util.List;

public class WorldCondition implements CatchCondition {

    /**
     * Check if the requirements are met to run the condition
     *
     * @param fish The fish to check
     *
     * @return Results in true if the condition should run
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return fish.condition().worlds() != null && !fish.condition().worlds().isEmpty();
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
        List<String> worlds = fish.condition().worlds();
        return worlds.contains(hook.getLocation().getWorld().getName());
    }

}
