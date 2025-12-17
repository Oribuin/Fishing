package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishGutEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Increases the base plugin entropy earned from gutting fish.
 */
@ConfigSerializable
public class AugmentFineSlicing extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "(%entropy% + %level%) * 0.05";

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentFineSlicing() {
        super("fine_slicing", "<gray>Increases the entropy ", "<gray>gained from gutting fish.");

        this.setMaxLevel(12);
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
        Placeholders plc = Placeholders.of(
                "level", level,
                "entropy", event.getEntropy()
        );
        double entropy = FishUtils.evaluate(plc.applyString(this.formula));
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
