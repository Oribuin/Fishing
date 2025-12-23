package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.condition.ConditionRegistry;
import dev.oribuin.fishing.model.condition.PlaceholderCheck;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@ConfigSerializable
public class PlaceholderCondition extends CatchCondition {

    @Comment("The minimum amount of checks that need to pass. 0 = All checks need to pass")
    private int minimumPlaceholders = 0;

    @Comment("The required placeholders that need to succeed")
    private List<PlaceholderCheck> placeholders = new ArrayList<>();

    /**
     * Decides whether the condition should be checked in the first place,
     * <p>R
     * This is to prevent unnecessary checks on fish that don't have the condition type.
     *
     * @param fish The fish to check for
     *
     * @return true if the fish has the condition applied. @see {@link #check(Fish, Player, ItemStack, FishHook)} for the actual condition check
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return this.enabled && !this.placeholders.isEmpty();
    }

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link ConditionRegistry#check(Fish, Player, ItemStack, FishHook)}
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
        Placeholders.Builder builder = Placeholders.builder();
        builder.addAll(fish.placeholders());
        //        builder.addAll(fish.getTierInstance()).placeholders()); // todo: tier placeholders
        builder.add("player", player.getName());
        Placeholders built = builder.build();

        int success = 0;
        int minimum = this.minimumPlaceholders;
        List<PlaceholderCheck> checks = this.placeholders;
        int required = minimum <= 0 ? checks.size() : minimum;
        for (PlaceholderCheck check : checks) {
            boolean result = check.attempt(player, built);
            if (!result && check.required()) return false; // check is required to pass for everything else to go through
            if (result) success++;

            // Stop checking if the required amount of checks has passed
            if (success >= required) return true;
        }

        return false;
    }

}
