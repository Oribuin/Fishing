package xyz.oribuin.fishing.fish;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.HexUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.fish.condition.Condition;
import xyz.oribuin.fishing.fish.condition.Time;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.manager.TierManager;
import xyz.oribuin.fishing.storage.util.PersistKeys;
import xyz.oribuin.fishing.util.FishUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fish implements Configurable {

    private final String name; // The name of the fish
    private final String tier; // The tier of the fish
    private String displayName; // The display name of the fish
    private List<String> description; // The description of the fish
    private int modelData; // The model data of the fish
    private ItemStack itemStack; // The item stack of the fish
    private Condition condition; // The requirements to catch the fish

    /**
     * Create a new name of fish with a name and quality
     *
     * @param name The name of the fish
     * @param tier The quality of the fish
     */
    public Fish(String name, String tier) {
        Objects.requireNonNull(name, "Fish name cannot be null.");
        Objects.requireNonNull(tier, "Fish tier cannot be null.");

        this.name = name;
        this.tier = tier;
        this.condition = new Condition();
        this.displayName = StringUtils.capitalize(name.toLowerCase().replace("_", " "));
        this.description = new ArrayList<>();
        this.modelData = -1;
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        Fish fish = new Fish(name, tier);

        // Catch Conditions
        this.displayName = config.getString("display-name", StringUtils.capitalize(this.name));
        this.description = config.getStringList("description");
        this.modelData = config.getInt("model-data", -1);

        // Catch Conditions
        Condition condition = new Condition();
        condition.biomes(config.getStringList("biomes"));
        condition.weather(FishUtils.getEnum(Weather.class, config.getString("weather")));
        condition.time(FishUtils.getEnum(Time.class, config.getString("time")));
        condition.worlds(config.getStringList("worlds"));
        condition.environment(FishUtils.getEnum(World.Environment.class, config.getString("environment")));
        condition.waterDepth((Integer) config.get("water-depth"));
        condition.iceFishing(config.getBoolean("ice-fishing"));
        condition.lightLevel((Integer) config.get("light-level"));
        condition.height(FishUtils.getHeight(config.getString("height")));
        condition.boatFishing(config.getBoolean("boat-fishing"));
        fish.condition(condition);
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set(this.name + ".name", this.name);
        config.set(this.name + ".display-name", this.displayName);
        config.set(this.name + ".description", this.description);
        config.set(this.name + ".model-data", this.modelData);

        // Conditions for the fish
        config.set(this.name + ".biomes", this.condition.biomes().stream().map(Enum::name).toList());
        config.set(this.name + ".worlds", this.condition.worlds());
        config.set(this.name + ".ice-fishing", this.condition.iceFishing());
        config.set(this.name + "boat-fishing", this.condition.boatFishing());

        // ugly :)
        if (this.condition.weather() != null) config.set(this.name + ".weather", this.condition.weather().name());
        if (this.condition.time() != null) config.set(this.name + ".time", this.condition.time().name());
        if (this.condition.environment() != null) config.set(this.name + ".environment", this.condition.environment().name());
        if (this.condition.waterDepth() != null) config.set(this.name + ".water-depth", this.condition.waterDepth());
        if (this.condition.lightLevel() != null) config.set(this.name + ".light-level", this.condition.lightLevel());
        if (this.condition.height() != null) config.set(this.name + ".height", this.condition.height().getLeft() + "-" + condition.height().getRight());
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return this.tier().tierFile().toPath();
    }

    /**
     * Create and obtain the itemstack of the fish, We only want to cache the itemstack if it's been used
     *
     * @return The item stack of the fish
     */
    public ItemStack createItemStack() {
        if (this.itemStack != null)
            return this.itemStack.clone();

        // Get the tier of the fish
        Tier fishTier = FishingPlugin.get().getManager(TierManager.class).get(this.tier);
        if (fishTier == null) return null;

        // Add all the information to the item stack
        ItemStack itemStack = fishTier.baseDisplay().build();
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(Component.text(HexUtils.colorify(this.displayName)));
            itemMeta.setCustomModelData(this.modelData);
            // TODO: Set the lore of the item

            // fish data :-)
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(PersistKeys.FISH_TYPE, PersistentDataType.STRING, this.name);
            container.set(PersistKeys.FISH_TYPE, PersistentDataType.STRING, this.tier);
        });

        return itemStack.clone(); // Clone the item stack to prevent any changes
    }

    /**
     * Obtain the tier of the fish based on the tier name
     *
     * @return The tier of the fish
     */
    public Tier tier() {
        return FishingPlugin.get().getManager(TierManager.class).get(this.tier);
    }

    public String tierName() {
        return this.tier;
    }

    public ItemStack itemStack() {
        return this.itemStack;
    }

    public void itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String name() {
        return name;
    }

    public String displayName() {
        return displayName;
    }

    public void displayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> description() {
        return description;
    }

    public void description(List<String> description) {
        this.description = description;
    }

    public int modelData() {
        return modelData;
    }

    public void modelData(int modelData) {
        this.modelData = modelData;
    }

    public Condition condition() {
        return condition;
    }

    public void condition(Condition condition) {
        this.condition = condition;
    }


}
