package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Increases the base minecraft xp earned from catching fish.
 */
@ConfigSerializable
public class AugmentGenius extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "<level> * 0.05";

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentGenius() {
        super("genius", "<gray>Increases the base minecraft xp", "<gray>earned from catching fish.");

        this.setMaxLevel(3);
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
        Placeholders plc = Placeholders.of(
                "level", level,
                "xp", event.getBaseNaturalExp()
        );
        double xp = FishUtils.evaluate(plc.applyString(this.formula));
        event.setNaturalExp((int) (event.getNaturalExp() + xp));
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    //    @Override
    //    public List<String> comments() {
    //        return List.of(
    //                "Augment [Genius] - Increases the base plugin xp earned from catching fish.",
    //                "",
    //                "formula: The formula to calculate the additional xp earned per level"
    //        );
    //    }

}
