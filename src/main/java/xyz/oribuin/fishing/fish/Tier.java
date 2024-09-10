package xyz.oribuin.fishing.fish;

import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.FishingPlugin;

import java.io.File;
import java.util.Objects;


public class Tier {

    // Basic Values
    private final ItemStack baseDisplay;
    private final File tierFolder;
    private final File tierFile;

    // Fish values
    private final String name;
    private final double money;
    private final double chance;
    private final int entropy;
    private float fishExp;
    private float naturalExp;
    /**
     * Create a new quality name for a fish
     *
     * @param name        The name of the quality
     * @param money       How much the fish of this quality is worth in money
     * @param chance      The chance of getting this quality
     * @param baseDisplay The base display item for the quality of fish
     */
    public Tier(String name, double money, double chance, int entropy, ItemStack baseDisplay) {
        Objects.requireNonNull(name, "Quality name cannot be null.");
        Objects.requireNonNull(baseDisplay, "Base display item cannot be null.");

        this.name = name;
        this.money = money;
        this.chance = chance;
        this.entropy = entropy;
        this.fishExp = 0.0f;
        this.naturalExp = 0.0f;
        this.baseDisplay = baseDisplay;
        this.tierFolder = new File(FishingPlugin.get().getDataFolder(), this.name.toLowerCase());
        this.tierFile = new File(this.tierFolder, "fish.yml");
    }

    /**
     * @return The folder where all relational data is stored
     */
    public File tierFolder() {
        return this.tierFolder;
    }

    /**
     * @return The file where the relational data is stored
     */
    public File tierFile() {
        return this.tierFile;
    }

    public String name() {
        return name;
    }

    public double money() {
        return money;
    }

    public double chance() {
        return chance;
    }

    public int entropy() {
        return entropy;
    }

    public ItemStack baseDisplay() {
        return baseDisplay;
    }

    public float fishExp() {
        return fishExp;
    }

    public void fishExp(float fishExp) {
        this.fishExp = fishExp;
    }

    public float naturalExp() {
        return naturalExp;
    }

    public void naturalExp(float naturalExp) {
        this.naturalExp = naturalExp;
    }

}
