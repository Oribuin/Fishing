package xyz.oribuin.fishing.fish.condition.impl;

import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.fish.Fish;

public class IceFishingCondition implements CatchCondition {

    private static final int MIN_RADIUS = 2;
    private static final int MAX_RADIUS = 3;

    /**
     * Check if the requirements are met to run the condition
     *
     * @param fish The fish to check
     *
     * @return Results in true if the condition should run
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return fish.condition().iceFishing();
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
        Location loc = hook.getLocation(); // Get the location of the fish hook

        // Make sure the hook is surrounded by ice blocks
        for (int x = -MAX_RADIUS; x <= MAX_RADIUS; x++) {
            for (int z = -MAX_RADIUS; z <= MAX_RADIUS; z++) {
                if (Math.abs(x) < MIN_RADIUS && Math.abs(z) < MIN_RADIUS) continue;

                Location check = loc.clone().add(x, 0, z);

                // if the block is liquid, continue
                if (check.getBlock().isLiquid()) continue;
                if (!Tag.ICE.isTagged(check.getBlock().getType())) return false;
            }
        }

        return true;
    }

}