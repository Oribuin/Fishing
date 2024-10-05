package xyz.oribuin.fishing.fish;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.FishUtils;
import xyz.oribuin.fishing.util.ItemConstruct;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Tier implements Configurable {

    // Basic Values
    private final File tierFile;
    private final CommentedFileConfiguration config;

    // Fish values
    private final String name;
    private ItemConstruct baseDisplay;
    private double money;
    private double chance;
    private int entropy;
    private int fishExp;
    private float naturalExp;
    private Map<String, Fish> fish;

    /**
     * Create a new quality name for a fish, Creates a new file for the quality
     *
     * @param name The name of the quality
     */
    public Tier(String name) {
        Objects.requireNonNull(name, "Quality name cannot be null.");

        this.name = name;
        this.baseDisplay = ItemConstruct.EMPTY;
        this.tierFile = FishUtils.createFile(FishingPlugin.get(), "tiers", name + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(this.tierFile);
        this.fish = new HashMap<>();
    }

    /**
     * Create a new quality name for a fish, Does not create a new file for the quality
     *
     * @param name     The name of the quality
     * @param tierFile The file where the relational data is stored
     */
    public Tier(String name, File tierFile) {
        Objects.requireNonNull(name, "Quality name cannot be null.");

        this.name = name;
        this.money = 0.0;
        this.chance = 0.0;
        this.entropy = 0;
        this.fishExp = 0;
        this.naturalExp = 0.0f;
        this.baseDisplay = ItemConstruct.EMPTY;
        this.tierFile = tierFile;
        this.config = CommentedFileConfiguration.loadConfiguration(this.tierFile);
        this.fish = new HashMap<>();
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.money = config.getDouble("money", 0.0);
        this.chance = config.getDouble("chance", 0.0);
        this.entropy = config.getInt("entropy", 0);
        this.baseDisplay = ItemConstruct.deserialize(config.getConfigurationSection("display"));
        this.fishExp = config.getInt("fish-exp", 0);
        this.naturalExp = (float) config.getDouble("natural-exp", 0.0);

        CommentedConfigurationSection section = config.getConfigurationSection("fish");
        if (section == null) return;

        // Load all the fish from the config
        for (String key : section.getKeys(false)) {
            CommentedConfigurationSection fishSection = section.getConfigurationSection(key);
            if (fishSection == null) continue;

            Fish fish = new Fish(key, this.name);
            fish.loadSettings(fishSection);
            this.fish.put(key, fish);
        }
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("money", this.money);
        config.set("chance", this.chance);
        config.set("entropy", this.entropy);
        config.set("fish-exp", this.fishExp);
        config.set("natural-exp", this.naturalExp);

        this.baseDisplay.serialize(config);

        CommentedConfigurationSection section = config.getConfigurationSection("fish");
        if (section == null) section = config.createSection("fish");

        // Save all the fish from the config
        for (Map.Entry<String, Fish> entry : this.fish.entrySet()) {
            CommentedConfigurationSection fishSection = section.getConfigurationSection(entry.getKey());
            if (fishSection == null) fishSection = section.createSection(entry.getKey());
            entry.getValue().saveSettings(fishSection);
        }
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

    public void money(double money) {
        this.money = money;
    }

    public double chance() {
        return chance;
    }

    public void chance(double chance) {
        this.chance = chance;
    }

    public int entropy() {
        return entropy;
    }

    public void entropy(int entropy) {
        this.entropy = entropy;
    }

    public ItemConstruct baseDisplay() {
        return baseDisplay;
    }

    public void baseDisplay(ItemConstruct baseDisplay) {
        this.baseDisplay = baseDisplay != null ? baseDisplay : ItemConstruct.EMPTY;
    }

    public int fishExp() {
        return fishExp;
    }

    public void fishExp(int fishExp) {
        this.fishExp = fishExp;
    }

    public float naturalExp() {
        return naturalExp;
    }

    public void naturalExp(float naturalExp) {
        this.naturalExp = naturalExp;
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return this.tierFile.toPath();
    }

}
