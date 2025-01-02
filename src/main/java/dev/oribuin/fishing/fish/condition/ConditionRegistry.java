package dev.oribuin.fishing.fish.condition;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.oribuin.fishing.api.condition.CatchCondition;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.fish.Fish;
import dev.oribuin.fishing.fish.condition.impl.BiomeCondition;
import dev.oribuin.fishing.fish.condition.impl.BoatCondition;
import dev.oribuin.fishing.fish.condition.impl.DepthCondition;
import dev.oribuin.fishing.fish.condition.impl.EnvironmentCondition;
import dev.oribuin.fishing.fish.condition.impl.HeightCondition;
import dev.oribuin.fishing.fish.condition.impl.IceFishingCondition;
import dev.oribuin.fishing.fish.condition.impl.LightLevelCondition;
import dev.oribuin.fishing.fish.condition.impl.TimeCondition;
import dev.oribuin.fishing.fish.condition.impl.WeatherCondition;
import dev.oribuin.fishing.fish.condition.impl.WorldCondition;

import java.util.HashMap;
import java.util.Map;

public class ConditionRegistry {

    private static final Map<String, CatchCondition> CONDITIONS = new HashMap<>();

    static {
        register("biome", new BiomeCondition());
        register("boat", new BoatCondition());
        register("depth", new DepthCondition());
        register("environment", new EnvironmentCondition());
        register("height", new HeightCondition());
        register("ice_fishing", new IceFishingCondition());
        register("light_level", new LightLevelCondition());
        register("time", new TimeCondition());
        register("world", new WorldCondition());
        register("weather", new WeatherCondition());
    }

    /**
     * Register a new condition to the fishing plugin
     *
     * @param name      The name of the condition
     * @param condition The condition to register
     */
    public static void register(String name, CatchCondition condition) {
        CONDITIONS.put(name, condition);
    }

    /**
     * Get a condition by its name from the fishing plugin
     *
     * @param name The name of the condition
     *
     * @return The condition
     */
    public CatchCondition get(String name) {
        return CONDITIONS.get(name);
    }

    /**
     * Get a condition by its class from the fishing plugin
     *
     * @param clazz The class of the condition
     *
     * @return The condition
     */
    public static CatchCondition get(Class<?> clazz) {
        return CONDITIONS.values().stream().filter(condition -> condition.getClass().equals(clazz)).findFirst().orElse(null);
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
        for (Map.Entry<String, CatchCondition> entry : CONDITIONS.entrySet()) {

            // Make sure the condition should run
            boolean shouldRun = entry.getValue().shouldRun(fish);
            if (!shouldRun) continue;

            // Check the condition
            boolean result = entry.getValue().check(fish, player, rod, hook);
            ConditionCheckEvent event = new ConditionCheckEvent(player, rod, hook, entry.getValue(), result);
            event.callEvent(); // Call the event

            if (event.isCancelled()) continue;
            if (!event.result()) return false;
        }

        return true;
    }

}
