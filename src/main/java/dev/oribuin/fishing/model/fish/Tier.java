package dev.oribuin.fishing.model.fish;

import dev.oribuin.fishing.config.ConfigHandler;
import dev.oribuin.fishing.item.ItemConstruct;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@ConfigSerializable
public class Tier {

    @Comment("The name of the tier (Typically the id)")
    private String name;
    @Comment("The chance of catching a fish of this tier")
    private double chance;
    @Comment("The entropy gained from catching this tier of fish")
    private int catchEntropy;
    @Comment("The entropy gained from gutting this tier of fish")
    private int gutEntropy;
    @Comment("The money gained from catching this tier of fish")
    private double catchMoney;
    @Comment("The money gained from selling this tier of fish")
    private double sellMoney;
    @Comment("The fishing experience gained from catching this tier of fish")
    private int catchExperience;
    @Comment("The minecraft experienced gained from catching this tier of fish")
    private float naturalExperience;
    @Comment("The base itemstack model for this tier of fish")
    private ItemConstruct item;
    @Comment("The list of fish that are available in this tier")
    private Map<String, Fish> fish;
    private transient ConfigHandler<Tier> configHandler;
    
    /**
     * Create a new tier config file from the plugin
     */
    public Tier() {
        this.name = "unknown";
        this.chance = 60.0;
        this.catchEntropy = 5;
        this.gutEntropy = 20;
        this.catchMoney = 0;
        this.sellMoney = 25;
        this.catchExperience = 100;
        this.naturalExperience = 10;
        this.item = new ItemConstruct(Material.COD);
    }

    /**
     * Create a new tier config file from    the plugin
     *
     * @param config The config file to create from
     */
    public Tier(File config) {
        this();
        this.configHandler = new ConfigHandler<>(Tier.class, config);
    }

    @Override
    public String toString() {
        return "Tier{" +
               "fish=" + fish +
               ", naturalExperience=" + naturalExperience +
               ", item=" + item +
               ", catchExperience=" + catchExperience +
               ", sellMoney=" + sellMoney +
               ", catchMoney=" + catchMoney +
               ", gutEntropy=" + gutEntropy +
               ", catchEntropy=" + catchEntropy +
               ", chance=" + chance +
               ", name='" + name + '\'' +
               '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public int getCatchEntropy() {
        return catchEntropy;
    }

    public void setCatchEntropy(int catchEntropy) {
        this.catchEntropy = catchEntropy;
    }

    public int getGutEntropy() {
        return gutEntropy;
    }

    public void setGutEntropy(int gutEntropy) {
        this.gutEntropy = gutEntropy;
    }

    public double getCatchMoney() {
        return catchMoney;
    }

    public void setCatchMoney(double catchMoney) {
        this.catchMoney = catchMoney;
    }

    public double getSellMoney() {
        return sellMoney;
    }

    public void setSellMoney(double sellMoney) {
        this.sellMoney = sellMoney;
    }

    public int getCatchExperience() {
        return catchExperience;
    }

    public void setCatchExperience(int catchExperience) {
        this.catchExperience = catchExperience;
    }

    public float getNaturalExperience() {
        return naturalExperience;
    }

    public void setNaturalExperience(float naturalExperience) {
        this.naturalExperience = naturalExperience;
    }

    public ItemConstruct getItem() {
        return item;
    }

    public void setItem(ItemConstruct item) {
        this.item = item;
    }

    public Map<String, Fish> getFish() {
        return fish;
    }

    public Tier setFish(Map<String, Fish> fish) {
        this.fish = fish;
        return this;
    }
    
    public ConfigHandler<Tier> getConfigHandler() {
        return configHandler;
    }

    public void setConfigHandler(ConfigHandler<Tier> configHandler) {
        this.configHandler = configHandler;
    }
}
