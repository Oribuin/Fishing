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
        if (!(event.getCondition() instanceof BiomeCondition)) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
        if (Math.random() > chance) return;

        event.setResult(true);
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.chanceFormula = config.getString("chance-formula", this.chanceFormula);
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
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
