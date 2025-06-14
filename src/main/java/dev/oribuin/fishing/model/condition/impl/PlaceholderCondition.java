package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.oribuin.fishing.model.condition.ConditionRegistry;
import dev.oribuin.fishing.model.condition.PlaceholderCheck;
import dev.oribuin.fishing.model.fish.Fish;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
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
public class PlaceholderCondition extends CatchCondition {

    private final List<PlaceholderCheck> checks = new ArrayList<>();
    private int minimum = 0; // 0 = all need to pass

    public PlaceholderCondition() {}

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
        return !this.checks.isEmpty();
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
        StringPlaceholders.Builder builder = StringPlaceholders.builder();
        builder.addAll(fish.placeholders());
        builder.addAll(fish.tier().placeholders());
        builder.add("player", player.getName());
        StringPlaceholders built = builder.build();

        int success = 0;
        int required = this.minimum <= 0 ? this.checks.size() : this.minimum;
        for (PlaceholderCheck check : this.checks) {
            boolean result = check.attempt(player, built);
            if (!result && check.required()) return false; // check is required to pass for everything else to go through
            if (result) success++;

            // Stop checking if the required amount of checks has passed
            if (success >= required) return true;
        }

        return false;
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        CommentedConfigurationSection section = this.pullSection(config, "placeholder-conditions");
        this.minimum = section.getInt("minimum", 0);

        section.getKeys(false).forEach(key -> {
            PlaceholderCheck check = PlaceholderCheck.create(this.pullSection(section, key));
            this.checks.add(check);
        });
    }

}
