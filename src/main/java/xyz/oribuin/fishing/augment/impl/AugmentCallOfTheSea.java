package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.condition.Weather;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;

public class AugmentCallOfTheSea extends Augment {

    private String chanceFormula = "%level% * 0.05"; // 5% per level
    private int minFish = 1;
    private int maxFish = 3;

    public AugmentCallOfTheSea() {
        super("call_of_the_sea", "Increases the amount of fish caught when the weather is raining");
    }

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param event The initial fish catch event
     * @param level The level of the augment that was used
     */
    @Override
    public void onInitialCatch(InitialFishCatchEvent event, int level) {
        if (!this.enabled) return;
        if (Weather.CLEAR.isState(event.getHook().getLocation())) return;

        StringPlaceholders plc = StringPlaceholders.of("level", level);
        double chance = FishUtils.evaluate(plc.apply(this.chanceFormula));
        if (Math.random() > chance) return;

        int fishCaught = this.minFish + (int) (Math.random() * (this.maxFish - this.minFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        event.getPlayer().sendActionBar(Component.text("You have caught more fish due to the Call of the Sea augment!"));
        // TODO: Tell player that they have caught more fish
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.chanceFormula = config.getString("chance-formula", this.chanceFormula); // 5% per level
        this.minFish = config.getInt("min-fish", 1); // Minimum fish caught
        this.maxFish = config.getInt("max-fish", 3); // Maximum fish caught
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        super.saveSettings(config);

        config.set("chance-formula", this.chanceFormula);
        config.set("min-fish", this.minFish);
        config.set("max-fish", this.maxFish);
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    @Override
    public List<String> comments() {
        return List.of(
                "Augment [Call Of The Sea] - When it is raining, there is a chance to catch multiple fish",
                "in a single catch.",
                "",
                "chance-formula: The formula to calculate the chance this augment triggers",
                "min-fish: The minimum additional fish caught",
                "max-fish: The maximum additional fish caught"
        );
    }

}
