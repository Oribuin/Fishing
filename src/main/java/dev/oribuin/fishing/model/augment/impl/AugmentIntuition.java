package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * Increases the entropy earned from catching fish, based on the level of the augment.
 */
public class AugmentIntuition extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "(%entropy% + %level%) * 0.05");

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentIntuition() {
        super("Intuition", "&7Increases the entropy ", "&7earned from catching fish.");

        this.maxLevel(5);
        this.register(FishCatchEvent.class, this::onFishCatch);
    }

    /**
     * The functionality provided when a player has finished catching a fish, Use this to modify the rewards given to the player once caught
     * <p>
     * Use {@link FishCatchEvent#entropy(int)} to change the entropy received
     * Use {@link FishCatchEvent#naturalExp(float)} to change the minecraft experience received
     * Use {@link FishCatchEvent#fishExp(int)} to change the fishing experience received
     *
     * @param event The event that was called when the fish was caught
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onFishCatch(FishCatchEvent event, int level) {
        StringPlaceholders plc = StringPlaceholders.of("level", level, "entropy", event.baseEntropy());
        double entropy = FishUtils.evaluate(plc.apply(FORMULA.value()));
        event.entropy((int) entropy);
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
