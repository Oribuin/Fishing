package dev.oribuin.fishing.model.augment.impl;

import dev.oribuin.fishing.api.config.Option;
import dev.oribuin.fishing.api.event.impl.ConditionCheckEvent;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.condition.impl.BiomeCondition;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;

import static dev.rosewood.rosegarden.config.SettingSerializers.STRING;

/**
 * When a player catches a fish, there is a chance to ignore the biome restrictions.
 *
 * @see dev.oribuin.fishing.model.condition.impl.BiomeCondition Where the condition is checked
 */
public class AugmentBiomeBlend extends   Augment {

    private final Option<String> CHANCE_FORMULA = new Option<>(STRING, "%level% * 0.15");

    /**
     * Create a new type of augment with a name and description.
     * <p>
     * Augment names must be unique and should be in snake_case, this will be used to identify the augment in the plugin, once implemented it should not be changed.
     */
    public AugmentBiomeBlend() {
        super("biome_blend", "&7When a player catches a fish, there is", "&7a chance to ignore the biome restrictions.");

        this.maxLevel(3);
        this.register(ConditionCheckEvent.class, this::onConditionCheck);
    }
    
    /**
     * The functionality provided when the plugin checks if a player could catch a fish. Use this to modify the outcome of the check
     * <p>
     * Use {@link ConditionCheckEvent#result(boolean)} change the result of the condition check
     * Use {@link FishGenerateEvent#addIncrease(double)} to change the chances of catching a fish
     *
     * @param event The event that was called when the fish was gutted
     * @param level The level of the ability that was used, if applicable (0 if not)
     */
    @Override
    public void onConditionCheck(ConditionCheckEvent event, int level) {
        if (!(event.condition() instanceof BiomeCondition)) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(CHANCE_FORMULA.value()));
        if (this.random.nextDouble(100) <= chance) return;

        event.result(true);
    }

    /**
     * Information about the augment which will be displayed in top of the augment configuration file
     *
     * @return The comments for the augment
     */
//    @Override
//    public List<String> comments() {
//        return List.of(
//                "Augment [Biome Blend] - When a player catches a fish, there is a chance to ignore the biome restrictions.",
//                "",
//                "chance-formula: The formula to calculate the chance to ignore the biome restrictions"
//        );
//    }

}
