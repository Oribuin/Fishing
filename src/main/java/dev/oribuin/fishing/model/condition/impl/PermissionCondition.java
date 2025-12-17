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
public class PermissionCondition extends CatchCondition {

    @Comment("The required permissions for catching this fish")
    private List<String> permissions = new ArrayList<>();

    @Comment("If the minimum amount of permissions succeed, the rest will be ignored")
    private int minimumPermissions = 0;

    /**
     * /**
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
        return this.enabled && !this.permissions.isEmpty();
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
        int success = this.permissions.stream()
                .map(x -> this.checkPermission(player, x))
                .mapToInt(x -> x ? 1 : 0)
                .sum();

        int minimum = this.minimumPermissions;
        return minimum == 0 ? success == this.permissions.size() : success >= minimum;
    }

    /**
     * Check if a player has a specific permission, if the permission starts with "!" it will check if the player doesn't have the permission
     *
     * @param player     The player to check
     * @param permission The permission to check
     *
     * @return true if the player has the permission
     */
    public boolean checkPermission(Player player, String permission) {
        return permission.startsWith("!") ? !player.hasPermission(permission.substring(1)) : player.hasPermission(permission);
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public Placeholders placeholders() {
        return Placeholders.of(
                "permissions", String.join(", ", this.permissions),
                "minimum_required", this.minimumPermissions
        );
    }

}
