package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.condition.ConditionRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@ConfigSerializable
public class AugmentCondition extends CatchCondition {

    @Comment("The required augments to catch a specified fish")
    private Map<String, Integer> augments = new HashMap<>(Map.of("example", 1));
    

    /**
     * Decides whether the condition should be checked in the first place
     * <p>
     * This is to prevent unnecessary checks on fish that don't have the condition type.
     *
     * @param fish The fish to check for
     *
     * @return true if the fish has the condition applied. @see {@link #check(Fish, Player, ItemStack, FishHook)} for the actual condition check
     */
    @Override
    public boolean shouldRun(Fish fish) {
        return this.enabled && !this.augments.isEmpty();
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
        Map<Augment, Integer> playerAugments = AugmentRegistry.from(rod);
        return this.augments.entrySet().stream().allMatch(entry -> {
            Augment augment = AugmentRegistry.from(entry.getKey());
            return playerAugments.containsKey(augment) && playerAugments.get(augment) >= entry.getValue();
        });
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        List<String> augments = this.augments.keySet().stream().toList();

        return Placeholders.of("augments", augments.isEmpty()
                ? "None"
                : String.join(", ", augments)
        );
    }

}
