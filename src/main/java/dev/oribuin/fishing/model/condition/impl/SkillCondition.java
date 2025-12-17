package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
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
import java.util.Map;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
@ConfigSerializable
public class SkillCondition extends CatchCondition {

    @Comment("The list of required skills and the level for this fish to be caught")
    private Map<String, Integer> requiredSkills = new HashMap<>();

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
        return this.enabled && !this.requiredSkills.isEmpty();
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
        // TODO: Add back Data Manager
        //        Fisher fisher = FishingPlugin.get().getManager(DataManager.class).get(player.getUniqueId());
        ////        if (fisher == null) return false; //  get fisher from cache
        //
        //        return fisher.skills().entrySet().stream().allMatch(entry -> {
        //            int required = requiredSkills.value().getOrDefault(entry.getKey(), 0);
        //            return required > 0 && entry.getValue() >= required;
        //        });
        return false;
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        return Placeholders.of("skills",
                this.requiredSkills.isEmpty()
                        ? "None"
                        : String.join(", ", this.requiredSkills.keySet())
        );
    }

}
