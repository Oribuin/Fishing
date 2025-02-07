package dev.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.augment.Augment;
import dev.oribuin.fishing.fish.condition.impl.BiomeCondition;
import dev.oribuin.fishing.util.FishUtils;

import java.util.List;

/**
 * When a player catches a fish, there is a chance to ignore the biome restrictions.
 *
 * @see dev.oribuin.fishing.fish.condition.impl.BiomeCondition Where the condition is checked
 */
public class AugmentBiomeBlend extends Augment {

    private String chanceFormula = "%level% * 0.15"; // 20% per level

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentBiomeBlend() {
        super("biome_Blend", "&7When a player catches a fish, there is", "&7a chance to ignore the biome restrictions.");

        this.maxLevel(3);
        this.register(ConditionCheckEvent.class, this::onConditionCheck);
    }

    /**
     * The functionality provided when the plugin checks if a player could catch a fish. Use this to modify the outcome of the check
     * <p>
     * Use {@link ConditionCheckEvent#result(boolean)} change the result of the condition check
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was gutted
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onConditionCheck(ConditionCheckEvent event, int level) {
        if (!(event.condition() instanceof BiomeCondition)) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
        if (Math.random() > chance) return;

        event.result(true);
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
        super.loadSettings(config);

        this.chanceFormula = config.getString("chance-formula", this.chanceFormula);
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
        super.saveSettings(config);

        config.set("chance-formula", this.chanceFormula);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Biome Blend] - When a player catches a fish, there is a chance to ignore the biome restrictions.",
                "",
                "chance-formula: The formula to calculate the chance to ignore the biome restrictions"
        );
    }

}
