package dev.oribuin.fishing.model.condition;

import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.model.fish.Fish;
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
import dev.oribuin.fishing.model.condition.impl.TimeCondition;
import dev.oribuin.fishing.model.condition.impl.WeatherCondition;
import dev.oribuin.fishing.model.condition.impl.WorldCondition;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The registry for all the catch conditions in the fishing plugin
 */
public class ConditionRegistry {

    private static final Set<Supplier<CatchCondition>> conditions = new HashSet<>();

    /**
     * The default constructor for the condition registry, should be empty
     */
    public ConditionRegistry() {}

    /**
     * Initialize all the default conditions into the registry to be loaded
     * <p>
     * This method should be called when the plugin is enabled to load all the conditions into the registry
     */
    public static void init() {
        conditions.clear();
        
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
        register(TimeCondition::new);
        register(WorldCondition::new);
        register(WeatherCondition::new);
    }

    /**
     * Register a new condition to the fishing plugin
     *
     * @param condition The condition to register
     */
    public static void register(Supplier<CatchCondition> condition) {
        conditions.add(condition);
    }

    /**
     * Load all the conditions from a configuration section and return them as a list
     *
     * @param fish The fish to load the conditions for
     * @param base The configuration section to load the conditions from
     *
     * @return The condition list
     */
    public static List<CatchCondition> loadConditions(Fish fish, CommentedConfigurationSection base) {
        return conditions.stream()
                .map(conditionSupplier -> {
                    CatchCondition condition = conditionSupplier.get();
                    if (condition == null) return null;
                    
                    condition.loadSettings(base);
                    return condition.shouldRun(fish) ? condition : null;
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
