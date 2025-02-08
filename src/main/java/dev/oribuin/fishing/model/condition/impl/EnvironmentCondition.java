package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.World;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
public class EnvironmentCondition extends CatchCondition {

    private List<World.Environment> environments = new ArrayList<>();

    /**
     * A condition that is checked when a player is fishing in a specific environment
     */
    public EnvironmentCondition() {}

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
        return !this.environments.isEmpty();
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
        return this.environments.contains(hook.getLocation().getWorld().getEnvironment());
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public StringPlaceholders placeholders() {
        List<String> environments = this.environments.stream()
                .map(x -> x.name().toLowerCase().replace("_", " "))
                .toList();

        return StringPlaceholders.builder()
                .add("environments", this.environments.isEmpty() ? "All" : String.join(", ", environments))
                .build();
    }

    /**
     * Load the settings for the condition from a configuration section
     *
     * @param section The configuration section to load the settings from
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection section) {
        this.environments = section.getStringList("environments").stream()
                .map(x -> FishUtils.getEnum(World.Environment.class, x))
                .toList();
    }

}
