package dev.oribuin.fishing.model.condition.impl;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.condition.CatchCondition;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.model.condition.ConditionRegistry#check(Fish, Player, ItemStack, FishHook)  to see how this is used
 */
public class DepthCondition extends CatchCondition {

    private Integer waterDepth = 0;

    /**
     * A condition that is checked when a fishing rod has certain water depth
     */
    public DepthCondition() {}

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
        return this.waterDepth != null && this.waterDepth > 0;
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
        int hookDepth = hook.getLocation().getBlockY();
        for (int i = 0; i < this.waterDepth; i++) {
            if (hookDepth == i) return true;

            Block relative = hook.getLocation().getBlock().getRelative(0, -i, 0);
            if (relative.isLiquid()) return true;
        }

        return false;
    }

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    @Override
    public StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("water_depth", this.waterDepth)
                .build();
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
        this.waterDepth = config.getInt("water-depth", 0);
    }

}
