package dev.oribuin.fishing.api.condition;

import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.fish.Fish;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A condition that is checked when a player is trying to catch a fish
 * <p>
 * First, {@link #shouldRun(Fish)} is called to check if the fish has the condition type
 * If the fish has the condition type, {@link #check(Fish, Player, ItemStack, FishHook)} is called to check if the player meets the condition to catch the fish
 *
 * @see dev.oribuin.fishing.fish.condition.ConditionRegistry#check(List, Fish, Player, ItemStack, FishHook)  to see how this is used
 */
public abstract class CatchCondition implements Configurable {

    /**
     * Decides whether the condition should be checked in the first place,
     * <p>R
     * This is to prevent unnecessary checks on fish that don't have the condition type.
     *
     * @param fish The fish to check for
     *
     * @return true if the fish has the condition applied. @see {@link #check(Fish, Player, ItemStack, FishHook)} for the actual condition check
     */
    public abstract boolean shouldRun(Fish fish);

    /**
     * Check if the player meets the condition to catch the fish or not, Requires {@link #shouldRun(Fish)} to return true before running
     * <p>
     * To see how this is used, check {@link dev.oribuin.fishing.fish.condition.ConditionRegistry#check(List, Fish, Player, ItemStack, FishHook)}
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
    public abstract boolean check(Fish fish, Player player, ItemStack rod, FishHook hook);

    /**
     * All the placeholders that can be used in the configuration file for this configurable class
     *
     * @return The placeholders
     */
    public StringPlaceholders placeholders() {
        return StringPlaceholders.empty();
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
    public abstract void loadSettings(@NotNull CommentedConfigurationSection config);

    /**
     * Create a new condition object. This is used to create a new condition object from a configuration section.
     *
     * @param clazz The class of the condition
     * @param base  The configuration section to load the settings from
     * @param <T>   The type of the condition
     *
     * @return The new condition object
     */
    public static <T extends CatchCondition> T create(Class<T> clazz, CommentedConfigurationSection base) {
        try {
            T condition = clazz.getDeclaredConstructor().newInstance();
            if (base != null) condition.loadSettings(base);
            return condition;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create a new condition object. This will be an empty condition object with no settings loaded.
     *
     * @param clazz The class of the condition
     * @param <T>   The type of the condition
     *
     * @return The new condition object
     */
    public static <T extends CatchCondition> T create(Class<T> clazz) {
        return create(clazz, null);
    }

}
