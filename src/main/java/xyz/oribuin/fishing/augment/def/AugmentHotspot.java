package xyz.oribuin.fishing.augment.def;

import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.FishContext;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.condition.Weather;

public class AugmentHotspot extends Augment {

    /**
     * Create a new augment instance with a name, description, and display item
     *
     * @param name        The name of the augment
     * @param description The description of the augment
     * @param displayItem The display item of the augment
     */
    public AugmentHotspot(String name, String description, ItemStack displayItem) {
        super(name, description, displayItem);
    }

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param context The context of the fish event
     */
    @Override
    public void onInitialCatch(FishContext context) {
        if (!Weather.CLEAR.isState(context.hook().getLocation())) return;

        // TODO: Add functionality to increase the amount of fish caught
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

    }
}
