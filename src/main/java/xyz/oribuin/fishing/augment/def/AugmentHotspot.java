package xyz.oribuin.fishing.augment.def;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.FishContext;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.condition.Weather;

import java.util.HashMap;
import java.util.Map;

public class AugmentHotspot extends Augment {

    private double chancePerLevel = 5.0;
    private int minFish = 1;
    private int maxFish = 3;

    public AugmentHotspot() {
        super("hotspot", "Increases the amount of fish caught when the weather is clear");
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
        if (!Weather.CLEAR.isState(event.getHook().getLocation())) return;

        int chanceToTrigger = (int) (this.chancePerLevel * level);
        if (Math.random() * 100 > chanceToTrigger) return;

        int fishCaught = this.minFish + (int) (Math.random() * (this.maxFish - this.minFish));
        event.setAmountToCatch(event.getAmountToCatch() + fishCaught);
        event.getPlayer().sendActionBar(Component.text("You have caught more fish due to the Hotspot augment!"));

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
     * Load the augment from a configuration file
     *
     * @param config The configuration file
     */
    @Override
    public void load(CommentedConfigurationSection config) {
        this.chancePerLevel = config.getDouble("chance-per-level", 5); // 5% Chance per level
        this.minFish = config.getInt("min-fish", 1); // Minimum fish caught
        this.maxFish = config.getInt("max-fish", 3); // Maximum fish caught
    }

    /**
     * Save the default values of the augment to a configuration file
     */
    @Override
    public Map<String, Object> save() {
        return new HashMap<>() {{
            this.put("chance-per-level", chancePerLevel);
            this.put("min-fish", minFish);
            this.put("max-fish", maxFish);
        }};
    }

}
