package xyz.oribuin.fishing.augment.impl;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.inventory.ItemStack;
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
     * Load the augment from a configuration file
     *
     * @param config The configuration file
     */
    @Override
    public void load(CommentedConfigurationSection config) {
        this.chancePerLevel = config.getDouble("chance-per-level", 5); // 5% Chance per level
    }

    /**
     * Save the default values of the augment to a configuration file
     */
    @Override
    public Map<String, Object> save() {
        return new HashMap<>() {{
            this.put("chance-per-level", chancePerLevel);
        }};

    }

}
