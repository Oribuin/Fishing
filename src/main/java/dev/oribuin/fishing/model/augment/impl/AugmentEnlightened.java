package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * Increases the base plugin xp earned from catching fish.
 */
public class AugmentEnlightened extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "(%xp% + %level%) * 0.03");
    
    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentEnlightened() {
        super("enlightened", "&7Increases the base plugin xp", "&7earned from catching fish.");

        this.maxLevel(5);
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
        StringPlaceholders plc = StringPlaceholders.of("level", level, "xp", event.baseFishExp());
        double xp = FishUtils.evaluate(plc.apply(FORMULA.value()));
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
