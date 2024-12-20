package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.FishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

public class AugmentSage extends Augment {

    private String formula = "(%entropy% + %level%) * 0.03";

    /**
     * Create a new augment instance with a name and description
     */
    public AugmentSage() {
        super("sage", "&7Increases the base plugin xp", "&7earned from catching fish.");
    }

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    @Override
    public void onFishCatch(FishCatchEvent event, int level) {
        if (!this.enabled) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level, "xp", event.getFishExp());
        double xp = FishUtils.evaluate(plc.apply(this.formula));
        event.setFishExp((int) xp);
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.formula = config.getString("formula", this.formula);
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        config.set("formula", this.formula);
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Sage] - Increases the base plugin xp earned from catching fish.",
                "",
                "formula: The formula to calculate the additional xp earned per level"
        );
    }

}
