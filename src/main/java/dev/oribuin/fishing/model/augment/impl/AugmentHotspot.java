package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.Weather;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * When the weather is clear, there is a chance to catch multiple fish in a single catch.
 */
@ConfigSerializable
public class AugmentHotspot extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "<level> * 0.05"; // 5% per level

    @Comment("The minimum fish to be spawned in")
    private int minimumFish = 1;

    @Comment("The maximum fish to be spawned in")
    private int maximumFish = 3;

    @Comment("The message sent when a player catches more fish")
    private TextMessage caughtMore = new TextMessage("<#94bc80><bold>Fish</bold> <gray>| <white>You have caught additional fish from HotSpot");

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentHotspot() {
        super("hotspot", "<gray>Increases the amount of fish", "<gray>caught when the weather is clear");

        this.setMaxLevel(15);
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

        Placeholders plc = Placeholders.of("level", level);
        double chance = FishUtils.evaluate(plc.applyString(this.formula));
        if (this.random.nextDouble(100) <= chance) return;

        int fishCaught = this.minimumFish + (int) (Math.random() * (this.maximumFish - this.minimumFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);

        this.caughtMore.send(event.getPlayer(), "additional", fishCaught);
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
