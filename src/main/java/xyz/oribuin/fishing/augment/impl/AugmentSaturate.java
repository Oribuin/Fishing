package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.api.FishContext;
import xyz.oribuin.fishing.fish.Fish;

import java.util.HashMap;
import java.util.Map;

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
    public void loadSettings(@NotNull CommentedFileConfiguration config) {
        super.loadSettings(config);

        this.chancePerLevel = config.getDouble("chance-per-level", 5); // 5% Chance per level
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedFileConfiguration config) {
        super.saveSettings(config);

        config.set("chance-per-level", this.chancePerLevel);
    }

}