package dev.oribuin.fishing.fish;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.condition.CatchCondition;
import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.augment.Augment;
import dev.oribuin.fishing.fish.condition.ConditionRegistry;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.storage.util.PersistKeys;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class Fish implements Configurable {

    private final String name; // The name of the fish
    private final String tier; // The tier of the fish
    private String displayName; // The display name of the fish
    private List<String> description; // The description of the fish
    private int modelData; // The model data of the fish
    private ItemStack itemStack; // The item stack of the fish
    private List<CatchCondition> conditions; // The conditions to catch the fish

    /**
     * Create a new name of fish with a name and quality
     *
     * @param name The name of the fish
     * @param tier The quality of the fish
     */
    public Fish(@NotNull String name, @NotNull String tier) {
        this.name = name;
        this.tier = tier;
        this.displayName = StringUtils.capitalize(name.toLowerCase().replace("_", " "));
        this.description = new ArrayList<>();
        this.modelData = -1;
        this.conditions = new ArrayList<>();
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
        this.displayName = config.getString("display-name", StringUtils.capitalize(this.name));
        this.description = config.getStringList("description");
        this.modelData = config.getInt("model-data", -1);

        // Catch Conditions for the fish
        this.conditions = ConditionRegistry.loadConditions(this, this.pullSection(config, "conditions"));
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
        config.set(this.name + ".name", this.name);
        config.set(this.name + ".display-name", this.displayName);
        config.set(this.name + ".description", this.description);
        config.set(this.name + ".model-data", this.modelData);
    }

    /**
     * The file path to a {@link CommentedFileConfiguration} file, This path by default will be relative {@link #parentFolder()}.
     * <p>
     * This by default is only used in the {@link #reload()} method to load the configuration file
     * <p>
     * This an optional method and should only be used if the Configurable class is its own file (E.g. {@link Augment} class)
     *
     * @return The path to the configuration file
     */
    @Override
    public @Nullable Path configPath() {
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
        StringPlaceholders.Builder placeholders = StringPlaceholders.builder();
        placeholders.addAll(this.placeholders());
        placeholders.addAll(this.tier().placeholders());

        ItemStack itemStack = fishTier.baseDisplay().build(placeholders.build());
        itemStack.editMeta(itemMeta -> {
            if (this.modelData > 0) itemMeta.setCustomModelData(this.modelData);

            // fish data :-)
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(PersistKeys.FISH_TYPE, PersistentDataType.STRING, this.name);
            container.set(PersistKeys.FISH_TYPE, PersistentDataType.STRING, this.tier);
        });

        this.itemStack = itemStack;
        return this.itemStack.clone(); // Clone the item stack to prevent any changes
    }

    public StringPlaceholders placeholders() {
        StringPlaceholders.Builder builder = StringPlaceholders.builder()
                .add("id", this.name)
                .add("name", this.displayName)
                .add("tier", this.tier)
                .add("description", String.join("\n", this.description));

        // Add all the placeholders from the conditions
        this.conditions.forEach(condition -> builder.addAll(condition.placeholders()));
        return builder.build();
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

    public List<CatchCondition> conditions() {
        return conditions;
    }

    public void conditions(List<CatchCondition> conditions) {
        this.conditions = conditions;
    }

}
