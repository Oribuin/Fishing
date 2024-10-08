package xyz.oribuin.fishing.api.condition;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.fish.Fish;

public interface CatchCondition {

    /**
     * Check if the requirements are met to run the condition
     *
     * @param fish The fish to check
     *
     * @return Results in true if the condition should run
     */
    boolean shouldRun(Fish fish);

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
    boolean check(Fish fish, Player player, ItemStack rod, FishHook hook);

}
