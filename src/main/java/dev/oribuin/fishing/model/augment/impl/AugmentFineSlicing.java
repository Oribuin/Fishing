package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.event.impl.FishGutEvent;
import dev.oribuin.fishing.config.TextMessage;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import static dev.oribuin.fishing.config.impl.PluginMessages.PREFIX;

/**
 * Increases the base plugin entropy earned from gutting fish.
 */
@ConfigSerializable
public class AugmentFineSlicing extends Augment {

    @Comment("The required formula for the augment to trigger")
    private String formula = "(<entropy> + <level>) * 0.05";

    @Comment("The message sent when a player has gained additional entropy from gutting")
    private TextMessage additionalGut = new TextMessage(PREFIX + "You have gained an additional <#93bc80><additional><white> entropy from <#93bc80>Fine Slicing");


    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentFineSlicing() {
        super("fine_slicing", "<gray>Increases the entropy ", "<gray>gained from gutting fish.");

        this.setMaxLevel(12);
        this.registerListener(FishGutEvent.class, this::onFishGut);
    }

    /**
     * The functionality provided when a player has gutted a fish, Use this to modify the rewards given to the player once gutted
     *
     * @param event The event that was called when the fish was gutted
     */
    @Override
    public void onFishGut(FishGutEvent event) {
        Placeholders plc = Placeholders.of(
                "level", level,
                "entropy", event.getBaseEntropy()
        );
        double additional = FishUtils.evaluate(plc.applyString(this.formula));
        event.setEntropy((int) (event.getEntropy() + additional));
        this.additionalGut.send(event.getPlayer(), "additional", (int) additional);
    }

}
