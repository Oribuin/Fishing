package dev.oribuin.fishing.model.fish;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.model.item.ItemConstruct;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

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
        this.baseDisplay = ItemConstruct.of(Material.COD);
        this.tierFile = FishUtils.createFile(FishingPlugin.get(), "tiers", name + ".yml");
        this.config = CommentedFileConfiguration.loadConfiguration(this.tierFile);
        this.fish = new HashMap<>();
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.money = config.getDouble("money", 0.0);
        this.chance = config.getDouble("chance", 0.0);
        this.entropy = config.getInt("entropy", 0);
        this.baseDisplay = ItemConstruct.of(Material.STONE);
        this.baseDisplay.loadSettings(this.pullSection(config, "display-item"));

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
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("money", this.money);
        config.set("chance", this.chance);
        config.set("entropy", this.entropy);
        config.set("fish-exp", this.fishExp);
        config.set("natural-exp", this.naturalExp);

        this.baseDisplay = ItemConstruct.deserialize(this.pullSection(config, "display-item"));

        // Save all the fish from the config
        for (Map.Entry<String, Fish> entry : this.fish.entrySet()) {
            entry.getValue().saveSettings(this.pullSection(config, "fish." + entry.getKey()));
        }
    }

    public Fish getFish(String name) {
        return this.fish.get(name);
    }

    public StringPlaceholders placeholders() {
        return StringPlaceholders.builder()
                .add("tier", this.name)
                .add("price", this.money)
                .add("chance", this.chance)
                .add("entropy", this.entropy)
                .add("fish_xp", this.fishExp)
                .add("natural_xp", this.naturalExp)
                .add("fish_count", this.fish.size())
                .build();
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

    public Map<String, Fish> fish() {
        return this.fish;
    }

    public void fish(Map<String, Fish> fish) {
        this.fish = fish;
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
        this.baseDisplay = baseDisplay != null ? baseDisplay : ItemConstruct.of(Material.COD);
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
