package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Increases the player's saturation level when they catch a fish.
 */
@ConfigSerializable
public class AugmentIndulge extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "%level% * 0.15"; // 15% per level

    @Comment("The amount of saturation that is given per fish caught")
    private float saturation = 5.0f;

    @Comment("The message sent when a player is fed by indulge")
    private TextMessage saturated = new TextMessage("<#4f73d6><bold>Fish</bold> <gray>| <white>You have slightly indulged in the fish you caught");


    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentIndulge() {
        super("indulge", "<gray>Restores a player's saturation", "<gray>when they catch a fish");

        this.setMaxLevel(3);
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

        Placeholders plc = Placeholders.of("level", level);
        double chance = FishUtils.evaluate(plc.applyString(this.formula));
        if (this.random.nextDouble(100) <= chance) return;

        event.getPlayer().setSaturation(Math.min(10f, event.getPlayer().getSaturation() + this.saturation));
        this.saturated.send(event.getPlayer());
    }

}
