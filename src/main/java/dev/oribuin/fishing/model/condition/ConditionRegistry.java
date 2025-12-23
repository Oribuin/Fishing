package dev.oribuin.fishing.model.condition;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.condition.impl.AugmentCondition;
import dev.oribuin.fishing.model.condition.impl.BiomeCondition;
import dev.oribuin.fishing.model.condition.impl.BoatCondition;
import dev.oribuin.fishing.model.condition.impl.DepthCondition;
import dev.oribuin.fishing.model.condition.impl.EnvironmentCondition;
import dev.oribuin.fishing.model.condition.impl.HeightCondition;
import dev.oribuin.fishing.model.condition.impl.IceFishingCondition;
import dev.oribuin.fishing.model.condition.impl.LightLevelCondition;
import dev.oribuin.fishing.model.condition.impl.PermissionCondition;
import dev.oribuin.fishing.model.condition.impl.PlaceholderCondition;
import dev.oribuin.fishing.model.condition.impl.SkillCondition;
import dev.oribuin.fishing.model.condition.impl.TimeCondition;
import dev.oribuin.fishing.model.condition.impl.WeatherCondition;
import dev.oribuin.fishing.model.condition.impl.WorldCondition;
import dev.oribuin.fishing.model.fish.Fish;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The registry for all the catch conditions in the fishing plugin
 */
public class ConditionRegistry {

    private static final Map<String, Supplier<CatchCondition>> conditions = new HashMap<>();

    /**
     * The default constructor for the condition registry, should be empty
     */
    public ConditionRegistry() {}

    static {
        register(AugmentCondition::new);
        register(BiomeCondition::new);
        register(BoatCondition::new);
        register(DepthCondition::new);
        register(EnvironmentCondition::new);
        register(HeightCondition::new);
        register(IceFishingCondition::new);
        register(LightLevelCondition::new);
        register(PermissionCondition::new);
        register(PlaceholderCondition::new);
        register(SkillCondition::new);
        register(TimeCondition::new);
        register(WorldCondition::new);
        register(WeatherCondition::new);
    }

    /**
     * Register a new condition to the fishing plugin
     *
     * @param supplier The condition to register
     */
    public static void register(Supplier<CatchCondition> supplier) {
        CatchCondition catchCondition = supplier.get();
        if (catchCondition == null) return;

        conditions.put(catchCondition.getClass().getSimpleName().toLowerCase(), supplier);
    }

    /**
     * Get a catch condition supplier from the registered list
     *
     * @param name The name of the condition
     *
     * @return The condition supplier if available
     */
    public static CatchCondition from(String name) {
        Supplier<CatchCondition> supplier = conditions.get(name.toLowerCase());
        if (supplier == null) return null;

        return supplier.get();
    }

    /**
     * Runs a fish context through all the conditions to check if the player can catch the fish or not.
     *
     * @param fish   The fish the player is trying to catch
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return Results in true if the player can catch the fish
     */
    public static boolean check(Fish fish, Player player, ItemStack rod, FishHook hook) {
        for (CatchCondition condition : fish.getConditions()) {
            if (!condition.shouldRun(fish)) {
                continue; // Don't run the condition they don't have 
            }

            // Check the condition
            boolean result = condition.check(fish, player, rod, hook);
            ConditionCheckEvent event = new ConditionCheckEvent(player, rod, hook, condition, result);
            event.callEvent(); // Call the event

            if (event.isCancelled()) continue;
            if (!event.result()) return false;
        }

        return true;
    }

}
