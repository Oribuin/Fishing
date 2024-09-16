package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.ConditionCheckEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.condition.impl.BiomeCondition;

/**
 * The functionality of this augment is provided in BiomeCondition.java
 */
public class AugmentBiomeDisrupt extends Augment {

    private String chanceFormula = "%level% * 0.20"; // 20% per level

    public AugmentBiomeDisrupt() {
        super("biome_disruption", "When a player catches a fish, there is a chance to ignore the biome restrictions.");
    }

    /**
     * The functionality provided when a condition is checked, used to modify the result of the condition
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    @Override
    public void onConditionCheck(ConditionCheckEvent event, int level) {
        if (!(event.getCondition() instanceof BiomeCondition)) return;

        if (this.shouldIgnoreBiome(level)) {
            event.setResult(true);
        }
    }

    /**
     * Should the biome restrictions be ignored for the player
     *
     * @param level The level of the augment
     *
     * @return Results in true if the biome restrictions should be ignored
     */
    public boolean shouldIgnoreBiome(int level) {
        return Math.random() * 100 < (int) (level * 0.20);
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedFileConfiguration config) {
        super.loadSettings(config);

        this.chanceFormula = config.getString("chance-formula", this.chanceFormula);
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedFileConfiguration config) {
        super.saveSettings(config);

        config.set("chance-formula", this.chanceFormula);
    }

}
