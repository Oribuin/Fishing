package dev.oribuin.fishing.fish.condition;

import dev.oribuin.fishing.api.condition.CatchCondition;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.fish.Fish;
import dev.oribuin.fishing.fish.condition.impl.AugmentCondition;
import dev.oribuin.fishing.fish.condition.impl.BiomeCondition;
import dev.oribuin.fishing.fish.condition.impl.BoatCondition;
import dev.oribuin.fishing.fish.condition.impl.DepthCondition;
import dev.oribuin.fishing.fish.condition.impl.EnvironmentCondition;
import dev.oribuin.fishing.fish.condition.impl.HeightCondition;
import dev.oribuin.fishing.fish.condition.impl.IceFishingCondition;
import dev.oribuin.fishing.fish.condition.impl.LightLevelCondition;
import dev.oribuin.fishing.fish.condition.impl.PermissionCondition;
import dev.oribuin.fishing.fish.condition.impl.PlaceholderCondition;
import dev.oribuin.fishing.fish.condition.impl.TimeCondition;
import dev.oribuin.fishing.fish.condition.impl.WeatherCondition;
import dev.oribuin.fishing.fish.condition.impl.WorldCondition;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConditionRegistry {

    private static final Set<Class<? extends CatchCondition>> CONDITIONS = new HashSet<>();

    static {
        register(AugmentCondition.class);
        register(BiomeCondition.class);
        register(BoatCondition.class);
        register(DepthCondition.class);
        register(EnvironmentCondition.class);
        register(HeightCondition.class);
        register(IceFishingCondition.class);
        register(LightLevelCondition.class);
        register(PermissionCondition.class);
        register(PlaceholderCondition.class);
        register(TimeCondition.class);
        register(WorldCondition.class);
        register(WeatherCondition.class);
    }

    /**
     * Register a new condition to the fishing plugin
     *
     * @param condition The condition to register
     */
    public static void register(Class<? extends CatchCondition> condition) {
        CONDITIONS.add(condition);
    }

    /**
     * Load all the conditions from a configuration section and return them as a list
     *
     * @param base The configuration section to load the conditions from
     *
     * @return The condition list
     */
    public static List<CatchCondition> loadConditions(Fish fish, CommentedConfigurationSection base) {
        return CONDITIONS.stream()
                .map(conditionClass -> {
                    CatchCondition condition = CatchCondition.create(conditionClass, base);
                    return condition != null && condition.shouldRun(fish) ? condition : null;
                })
                .filter(Objects::nonNull)
                .toList();
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
        for (CatchCondition condition : fish.conditions()) {
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
