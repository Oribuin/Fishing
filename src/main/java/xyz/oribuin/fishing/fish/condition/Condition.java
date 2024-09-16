package xyz.oribuin.fishing.fish.condition;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.ArrayList;
import java.util.List;

/**
 * FishCondition that must be met for a fish to be caught by a player
 */
public class Condition {

    private List<Biome> biomes = new ArrayList<>();
    private Weather weather = null;
    private Time time = Time.ALL_DAY;
    private List<String> worlds = new ArrayList<>();
    private World.Environment environment = null;
    private Integer waterDepth = null;
    private boolean iceFishing = false;
    private Pair<Integer, Integer> height = null;
    private Integer lightLevel = null;

    public List<Biome> biomes() {
        return biomes;
    }

    public Condition biomes(List<Biome> biomes) {
        this.biomes = biomes;
        return this;
    }

    public Weather weather() {
        return weather;
    }

    public Condition weather(Weather weather) {
        this.weather = weather;
        return this;
    }

    public Time time() {
        return time;
    }

    public Condition time(Time time) {
        this.time = time;
        return this;
    }

    public List<String> worlds() {
        return worlds;
    }

    public Condition worlds(List<String> worlds) {
        this.worlds = worlds;
        return this;
    }

    public World.Environment environment() {
        return environment;
    }

    public Condition environment(World.Environment environment) {
        this.environment = environment;
        return this;
    }

    public Integer waterDepth() {
        return waterDepth;
    }

    public Condition waterDepth(Integer waterDepth) {
        this.waterDepth = waterDepth;
        return this;
    }

    public boolean iceFishing() {
        return iceFishing;
    }

    public Condition iceFishing(boolean iceFishing) {
        this.iceFishing = iceFishing;
        return this;
    }

    public Pair<Integer, Integer> height() {
        return height;
    }

    public Condition height(Pair<Integer, Integer> height) {
        this.height = height;
        return this;
    }

    public Integer lightLevel() {
        return lightLevel;
    }

    public Condition lightLevel(Integer lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

}