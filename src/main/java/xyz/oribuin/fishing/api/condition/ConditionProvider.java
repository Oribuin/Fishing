package xyz.oribuin.fishing.api.condition;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.condition.impl.BiomeCondition;
import xyz.oribuin.fishing.api.condition.impl.DepthCondition;
import xyz.oribuin.fishing.api.condition.impl.EnvironmentCondition;
import xyz.oribuin.fishing.api.condition.impl.HeightCondition;
import xyz.oribuin.fishing.api.condition.impl.IceFishingCondition;
import xyz.oribuin.fishing.api.condition.impl.LightLevelCondition;
import xyz.oribuin.fishing.api.condition.impl.TimeCondition;
import xyz.oribuin.fishing.api.condition.impl.WeatherCondition;
import xyz.oribuin.fishing.api.condition.impl.WorldCondition;
import xyz.oribuin.fishing.fish.Fish;

import java.util.HashMap;
import java.util.Map;

public class ConditionProvider {

    private static final Map<String, CatchCondition> CONDITIONS = new HashMap<>();

    static {
        register("biome", new BiomeCondition());
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
     * Check if the player can catch the fish with the current conditions
     *
     * @param fish   The fish the player is trying to catch
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return Results in true if the player can catch the fish
     */
    public static boolean check(Fish fish, Player player, ItemStack rod, FishHook hook) {
        for (CatchCondition condition : CONDITIONS.values()) {
            if (!condition.check(fish, player, rod, hook)) return false;
        }

        return true;
    }

}
