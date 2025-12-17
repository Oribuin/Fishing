package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Increases the base plugin xp earned from catching fish.
 */
@ConfigSerializable
public class AugmentEnlightened extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "(%xp% + %level%) * 0.03";

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentEnlightened() {
        super("enlightened", "<gray>Increases the base plugin xp", "<gray>earned from catching fish.");

        this.setMaxLevel(5);
        this.register(FishCatchEvent.class, this::onFishCatch);
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
        Placeholders plc = Placeholders.of(
                "level", level,
                "xp", event.baseFishExp()
        );

        double xp = FishUtils.evaluate(plc.applyString(this.formula));
        event.fishExp((int) xp);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    //    @Override
    //    public List<String> comments() {
    //        return List.of(
    //                "Augment [Enlightened] - Increases the base plugin xp earned from catching fish.",
    //                "",
    //                "formula: The formula to calculate the additional xp earned per level"
    //        );
    //    }

}
