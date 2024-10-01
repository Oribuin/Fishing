package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.api.event.FishContext;
import xyz.oribuin.fishing.fish.Fish;

public class AugmentSaturate extends Augment {

    private double chancePerLevel = 25.0;

    public AugmentSaturate() {
        super("saturate", "Fully saturates the player when they catch a fish");
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
        if (event.getPlayer().getSaturation() >= 20.0) return;

        int chanceToTrigger = (int) (this.chancePerLevel * level);
        if (Math.random() * 100 > chanceToTrigger) return;

        event.getPlayer().setSaturation(20.0f);
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
    }

}
