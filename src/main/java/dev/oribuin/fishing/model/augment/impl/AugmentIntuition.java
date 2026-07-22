package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Increases the entropy earned from catching fish, based on the level of the augment.
 */
@ConfigSerializable
public class AugmentIntuition extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "(<entropy> + <level>) * 0.05";

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentIntuition() {
        super("Intuition", "<gray>Increases the entropy ", "<gray>earned from catching fish.");

        this.setMaxLevel(5);
        this.registerListener(FishCatchEvent.class, this::onFishCatch);
    }

    /**
     * The functionality provided when a player has finished catching a fish, Use this to modify the rewards given to the player once caught
     * <p>
     * Use {@link FishCatchEvent#setCatchEntropy(int)} to change the entropy received
     * Use {@link FishCatchEvent#setNaturalExp(float)} to change the minecraft experience received
     * Use {@link FishCatchEvent#setCatchExp(int)} to change the fishing experience received
     *
     * @param event The event that was called when the fish was caught
     */
    @Override
    public void onFishCatch(FishCatchEvent event) {
        Placeholders plc = Placeholders.of("level", level, "entropy", event.getBaseCatchEntropy());
        double entropy = FishUtils.evaluate(plc.applyString(this.formula));
        event.setCatchEntropy((int) entropy);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    //    @Override
    //    public List<String> comments() {
    //        return List.of(
    //                "Augment [Intuition] - Increases the base entropy earned from catching fish.",
    //                "",
    //                "formula: The formula to calculate the additional entropy earned per level"
    //        );
    //    }

}
