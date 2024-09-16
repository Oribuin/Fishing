package xyz.oribuin.fishing.api.condition.impl;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

public class HeightCondition implements CatchCondition {

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
        if (fish.condition().height() == null) return true;

        int minHookHeight = fish.condition().height().getLeft();
        int maxHookHeight = fish.condition().height().getRight();
        int hookHeight = hook.getLocation().getBlockY();

        return hookHeight >= minHookHeight && hookHeight <= maxHookHeight;
    }

}
