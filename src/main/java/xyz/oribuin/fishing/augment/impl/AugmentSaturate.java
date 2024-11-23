package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

public class AugmentSaturate extends Augment {

    private String chanceFormula = "%level% * 0.15"; // 15% per level

    public AugmentSaturate() {
        super("saturate", "Fully saturates the player when they catch a fish");
    }

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param event The initial fish catch event
     * @param level The level of the augment that was used
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
        if (event.getPlayer().getFoodLevel() >= 20.0) return;
        if (!this.enabled) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
        if (Math.random() * 100 > chance) return;

        event.getPlayer().setFoodLevel(20);
        event.getPlayer().sendMessage("You have been fully saturated!"); // todo: use locale
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
                "Augment [Saturate] - Fully saturates the player when they catch a fish",
                "",
                "chance-formula: The formula to calculate the chance of the player being fully saturated"
        );
    }

}
