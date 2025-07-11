package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Message;
import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.config.TextMessage;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.Weather;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.INTEGER;
import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * When the weather is clear, there is a chance to catch multiple fish in a single catch.
 */
public class AugmentHotspot extends Augment {

    private final Option<String> FORMULA = new Option<>(STRING, "%level% * 0.05"); // 5% per level
    private final Option<Integer> MIN_FISH = new Option<>(INTEGER, 1, "Minimum fish to be spawned");
    private final Option<Integer> MAX_FISH = new Option<>(INTEGER, 3, "Maximum fish to be spawned");
    private final Message CAUGHT_MORE = TextMessage.ofConfig("<#4f73d6><bold>Fish</bold> <gray>| <white>You have caught additional fish from HotSpot");
    
    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentHotspot() {
        super("hotspot", "&7Increases the amount of fish", "&7caught when the weather is clear");

        this.maxLevel(15);
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
        if (!Weather.CLEAR.isState(event.getHook().getLocation())) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(FORMULA.value()));
        if (this.random.nextDouble(100) <= chance) return;

        int fishCaught = MIN_FISH.value() + (int) (Math.random() * (MAX_FISH.value() - MIN_FISH.value()));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        
        CAUGHT_MORE.send(event.getPlayer(), StringPlaceholders.of("additional", fishCaught));
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
//    @Override
//    public List<String> comments() {
//        return List.of(
//                "Augment [Hotspot] - When the weather is clear, there is a chance to catch multiple fish",
//                "in a single catch.",
//                "",
//                "chance-formula: The formula to calculate the chance this augment triggers",
//                "min-fish: The minimum additional fish caught",
//                "max-fish: The maximum additional fish caught"
//        );
//    }

}
