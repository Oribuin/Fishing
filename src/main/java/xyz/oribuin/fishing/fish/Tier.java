package xyz.oribuin.fishing.fish;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.io.File;
import java.util.Objects;


public class Tier {

    // Basic Values
    private final ItemConstruct baseDisplay;
    private File tierFile;
    private CommentedFileConfiguration config;

    // Fish values
    private final String name;
    private final double money;
    private final double chance;
    private final int entropy;
    private float fishExp;
    private float naturalExp;

    /**
     * Create a new quality name for a fish, Creates a new file for the quality
     *
     * @param name        The name of the quality
     * @param money       How much the fish of this quality is worth in money
     * @param chance      The chance of getting this quality
     * @param baseDisplay The base display item for the quality of fish
     */
    public Tier(String name, double money, double chance, int entropy, ItemConstruct baseDisplay) {
        Objects.requireNonNull(name, "Quality name cannot be null.");
        Objects.requireNonNull(baseDisplay, "Base display item cannot be null.");

        this.name = name;
        this.money = money;
        this.chance = chance;
        this.entropy = entropy;
        this.fishExp = 0.0f;
        this.naturalExp = 0.0f;
        this.baseDisplay = baseDisplay;
        this.tierFile = FishUtils.createFile(FishingPlugin.get(), "tiers", name + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(this.tierFile);
    }

    /**
     * Create a new quality name for a fish, Does not create a new file for the quality
     *
     * @param name        The name of the quality
     * @param money       How much the fish of this quality is worth in money
     * @param chance      The chance of getting this quality
     * @param entropy     The entropy of the quality
     * @param baseDisplay The base display item for the quality of fish
     * @param tierFile    The file where the relational data is stored
     */
    public Tier(String name, double money, double chance, int entropy, ItemConstruct baseDisplay, File tierFile) {
        Objects.requireNonNull(name, "Quality name cannot be null.");
        Objects.requireNonNull(baseDisplay, "Base display item cannot be null.");

        this.name = name;
        this.money = money;
        this.chance = chance;
        this.entropy = entropy;
        this.fishExp = 0.0f;
        this.naturalExp = 0.0f;
        this.baseDisplay = baseDisplay;
        this.tierFile = tierFile;
        this.config = CommentedFileConfiguration.loadConfiguration(this.tierFile);
    }

    /**
     * @return The file where the relational data is stored
     */
    public File tierFile() {
        return this.tierFile;
    }

    public CommentedFileConfiguration config() {
        return this.config;
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

    public ItemConstruct baseDisplay() {
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
