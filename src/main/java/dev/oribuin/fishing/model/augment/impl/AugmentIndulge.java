package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.FLOAT;
import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * Increases the player's saturation level when they catch a fish.
 */
public class AugmentIndulge extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "%level% * 0.15"); // 15% per level
    private final Option<Float> SATURATION = new Option<>(FLOAT, 5.0f);

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentIndulge() {
        super("indulge", "&7Restores a player's saturation", "&7when they catch a fish");

        this.maxLevel(3);
        this.register(InitialFishCatchEvent.class, this::onInitialCatch);
    }

    /**
     * The functionality provided when a player is first starting to catch a fish, Use this to determine how many fish should be generated.
     * <p>
     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to set the amount of fish to catch
     * <p>
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was caught
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
        if (event.getPlayer().getFoodLevel() >= 20.0) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(FORMULA.value()));
        if (this.random.nextDouble(100) <= chance) return;

        event.getPlayer().setSaturation(Math.min(20f, event.getPlayer().getSaturation() + SATURATION.value()));
        event.getPlayer().sendMessage("You have been saturated!"); // todo: use locale
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
//    @Override
//    public List<String> comments() {
//        return List.of(
//                "Augment [Indulge] - Fully saturates the player when they catch a fish",
//                "",
//                "chance-formula: The formula to calculate the chance of the player being fully saturated",
//                "saturation: The saturation level to set the player to"
//        );
//    }

}
