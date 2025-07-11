package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.FishGutEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * Increases the base plugin entropy earned from gutting fish.
 */
public class AugmentFineSlicing extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "(%entropy% + %level%) * 0.05");

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentFineSlicing() {
        super("fine_slicing", "&7Increases the entropy ", "&7gained from gutting fish.");

        this.maxLevel(12);
        this.register(FishGutEvent.class, this::onFishGut);
    }

    /**
     * The functionality provided when a player has gutted a fish, Use this to modify the rewards given to the player once gutted
     *
     * @param event The event that was called when the fish was gutted
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onFishGut(FishGutEvent event, int level) {
        StringPlaceholders plc = StringPlaceholders.of("level", level, "entropy", event.getEntropy());
        double entropy = FishUtils.evaluate(plc.apply(FORMULA.value()));
        event.setEntropy((int) entropy);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
//    @Override
//    public List<String> comments() {
//        return List.of(
//                "Augment [Fine Slicing] - Increases the entropy gained from gutting fish.",
//                "",
//                "formula: The formula to calculate the additional entropy earned per level"
//        );
//    }

}
