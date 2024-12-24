package xyz.oribuin.fishing.fish.condition.impl;

import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.CatchCondition;
import xyz.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import xyz.oribuin.fishing.fish.Fish;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see xyz.oribuin.fishing.fish.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook) to see how this is used
 */
public class IceFishingCondition implements CatchCondition {

    private static final int MIN_RADIUS = 2;
    private static final int MAX_RADIUS = 3;

    /**
     * A condition that is checked when a player is fishing surrounded by ice blocks
     */
    public IceFishingCondition() {}

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
        return fish.condition().iceFishing();
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
