package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.Weather;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * When it is raining, there is a chance to catch multiple fish in a single catch.
 */
@ConfigSerializable
public class AugmentRainDance extends Augment {

    private static final Map<UUID, Long> ADDITIONAL = new HashMap<>();

    @Comment("The required formula for the augment to trigger")
    private String formula = "<level> * 0.05"; // 5% per level

    @Comment("The minimum fish to be spawned in")
    private int minimumFish = 1;

    @Comment("The maximum fish to be spawned in")
    private int maximumFish = 3;

    @Comment("The message sent when a player has caught additional fish")
    private TextMessage gotAdditional = TextMessage.ofActionBar("<#93bc80>[<white>Rain Dance granted you additional fish<#93bc80>]");


    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentRainDance() {
        super("rain_dance", "<gray>Increases the amount of fish", "<gray>caught when the weather is raining");

        this.setMaxLevel(15);
        this.registerListener(InitialFishCatchEvent.class, this::onInitialCatch);
        this.registerListener(FishCatchEvent.class, this::onFishCatch);
    }

    /**
     * The functionality provided when a player is first starting to catch a fish, Use this to determine how many fish should be generated.
     * <p>
     * Use {@link InitialFishCatchEvent#setAmountToCatch(int)} to set the amount of fish to catch
     * <p>
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was caught
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event) {
        if (Weather.CLEAR.isState(event.getHook().getLocation())) return;

        Placeholders plc = Placeholders.of("level", level);
        double chance = FishUtils.evaluate(plc.applyString(this.formula));
        if (this.random.nextDouble(100) <= chance) return;

        int fishCaught = this.minimumFish + (int) (Math.random() * (this.maximumFish - this.minimumFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        ADDITIONAL.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }


    /**
     * The functionality provided when a player has finished catching a fish, Use this to modify the rewards given to the player once caught
     * <p>
     * Use {@link FishCatchEvent#setCatchEntropy(int)} to change the entropy received
     * <p>
     * Use {@link FishCatchEvent#setNaturalExp(float)} to change the minecraft experience received
     * <p>
     * Use {@link FishCatchEvent#setCatchExp(int)} to change the fishing experience received
     *
     * @param event The event that was called when the fish was caught
     */
    @Override
    public void onFishCatch(FishCatchEvent event) {
        if (Weather.CLEAR.isState(event.getHook().getLocation())) return;
        Long last = ADDITIONAL.get(event.getPlayer().getUniqueId());
        if (last == null) return;

        ADDITIONAL.remove(event.getPlayer().getUniqueId());

        // require their last thing to be less than < 3 seconds ago
        if (System.currentTimeMillis() - last >= TimeUnit.SECONDS.toMillis(3)) return;

        this.gotAdditional.send(event.getPlayer());
    }
    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
    //    @Override
    //    public List<String> comments() {
    //        return List.of(
    //                "Augment [Rain Dance] - When it is raining, there is a chance to catch multiple fish",
    //                "in a single catch.",
    //                "",
    //                "chance-formula: The formula to calculate the chance this augment triggers",
    //                "min-fish: The minimum additional fish caught",
    //                "max-fish: The maximum additional fish caught"
    //        );
    //    }

}
