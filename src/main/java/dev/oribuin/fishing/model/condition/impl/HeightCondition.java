package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.util.Placeholders;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@ConfigSerializable
public class HeightCondition extends CatchCondition {

    @Comment("The required world height the fishing bobber has to be in to catch a specified fish")
    private IntPair height = new IntPair(-128, 320);
    
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
        return this.enabled && this.height != null;
    }

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)}
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
        int minHookHeight = this.height.min();
        int maxHookHeight = this.height.max();
        int hookHeight = hook.getLocation().getBlockY();
        return hookHeight >= minHookHeight && hookHeight <= maxHookHeight;
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        return Placeholders.of(
                "min_height", this.height != null ? this.height.min() : "N/A",
                "max_height", this.height != null ? this.height.max() : "N/A"
        );
    }

    @ConfigSerializable
    private record IntPair(Integer min, Integer max) {
        public Pair<Integer, Integer> asPair() {
            return Pair.of(min, max);
        }

    }

}
