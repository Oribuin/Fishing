package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.augment.Augment;

import java.util.List;

public class AugmentPerception extends Augment {

    private String formula = "%entropy% + %level% * 0.05";

    /**
     * Create a new augment instance with a name and description
     */
    public AugmentPerception() {
        super("perception", "Increases the base entropy earned from catching fish.");
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
                "Augment [Perception] - Increases the base entropy earned from catching fish.",
                "",
                "formula: The formula to calculate the additional entropy earned per level"
        );
    }

}
