package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.FishContext;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.condition.Weather;

public class AugmentCallOfTheSea extends Augment {

    private double chancePerLevel = 5.0;
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
        if (Weather.CLEAR.isState(event.getHook().getLocation())) return;

        int chanceToTrigger = (int) (this.chancePerLevel * level);
        if (Math.random() * 100 > chanceToTrigger) return;

        int fishCaught = this.minFish + (int) (Math.random() * (this.maxFish - this.minFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        event.getPlayer().sendActionBar(Component.text("You have caught more fish due to the Call of the Sea augment!"));
        // TODO: Tell player that they have caught more fish
    }

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param context The context of the fish event
     * @param fish    The fish that was caught
     * @param stack   The item stack of the fish
     */
    @Override
    public void onFishCatch(FishContext context, Fish fish, ItemStack stack) {
        // Unused
    }

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        super.loadSettings(config);

        this.chancePerLevel = config.getDouble("chance-per-level", 5); // 5% Chance per level
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

        config.set("chance-per-level", this.chancePerLevel);
        config.set("min-fish", this.minFish);
        config.set("max-fish", this.maxFish);
    }

}
