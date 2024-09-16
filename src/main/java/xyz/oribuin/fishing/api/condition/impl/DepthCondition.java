package xyz.oribuin.fishing.api.condition.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

public class DepthCondition implements CatchCondition {

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
        if (fish.condition().waterDepth() == null) return true;

        int hookDepth = hook.getLocation().getBlockY();
        for (int i = 0; i < fish.condition().waterDepth(); i++) {
            if (hookDepth == i) return true;

            Block relative = hook.getLocation().getBlock().getRelative(0, -i, 0);
            if (relative.isLiquid()) return true;
        }

        return false;
    }

}
