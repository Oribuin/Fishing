package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.ConditionCheckEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.condition.impl.BiomeCondition;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

/**
 * The functionality of this augment is provided in BiomeCondition.java
 */
public class AugmentBiomeDisrupt extends Augment {

    private String chanceFormula = "%level% * 0.20"; // 20% per level

    public AugmentBiomeDisrupt() {
        super("biome_disruption", "&7When a player catches a fish, there is", "&7a chance to ignore the biome restrictions.");

        this.maxLevel = 3;
    }

    /**
     * The functionality provided when a condition is checked, used to modify the result of the condition
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    @Override
    public void onConditionCheck(ConditionCheckEvent event, int level) {
        if (!this.enabled) return;
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
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Biome Disruption] - When a player catches a fish, there is a chance to ignore the biome restrictions.",
                "",
                "chance-formula: The formula to calculate the chance to ignore the biome restrictions"
        );
    }

}
