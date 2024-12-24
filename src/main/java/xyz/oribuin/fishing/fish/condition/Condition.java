package xyz.oribuin.fishing.fish.condition;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * FishCondition that must be met for a fish to be caught by a player
 */
public class Condition {

    private List<String> biomes;
    private Weather weather;
    private Time time;
    private List<String> worlds;
    private World.Environment environment;
    private Integer waterDepth;
    private boolean iceFishing;
    private Pair<Integer, Integer> height;
    private Integer lightLevel;
    private boolean boatFishing;

    public Condition() {
        this.biomes = new ArrayList<>();
        this.worlds = new ArrayList<>();
        this.time = Time.ANY_TIME;
        this.environment = null;
        this.weather = null;
        this.waterDepth = null;
        this.iceFishing = false;
        this.height = null;
        this.lightLevel = null;
        this.boatFishing = false;
    }

    public List<String> biomes() {
        return biomes;
    }

    public Condition biomes(List<String> biomes) {
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

    public boolean boatFishing() {
        return this.boatFishing;
    }

    public void boatFishing(boolean boatFishing) {
        this.boatFishing = boatFishing;
    }

}