package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.FishGutEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

public class AugmentPrecisionCutting extends Augment {

    private String formula = "(%entropy% + %level%) * 0.05";

    /**
     * Create a new augment instance with a name and description
     */
    public AugmentPrecisionCutting() {
        super("precision_cutting", "&7Increases the entropy ", "&7gained from gutting fish.");
    }

    /**
     * The functionality provided when a collection of fish are gutted by the player
     *
     * @param event The context of the fish event
     * @param level The level of the augment that was used
     */
    @Override
    public void onFishGut(FishGutEvent event, int level) {
        if (!this.enabled) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level, "entropy", event.getEntropy());
        double entropy = FishUtils.evaluate(plc.apply(this.formula));
        event.setEntropy((int) entropy);
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

        this.formula = config.getString("formula", this.formula);
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
                "Augment [Precision Cutting] - Increases the entropy gained from gutting fish.",
                "",
                "formula: The formula to calculate the additional entropy earned per level"
        );
    }

}
