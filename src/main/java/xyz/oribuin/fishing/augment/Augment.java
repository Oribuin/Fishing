package xyz.oribuin.fishing.augment;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.fish.Fish;

public abstract class Augment implements Listener {

    private final String name;
    private final String description;
    private final ItemStack displayItem;

    /**
     * Create a new augment instance with a name, description, and display item
     *
     * @param name        The name of the augment
     * @param description The description of the augment
     * @param displayItem The display item of the augment
     */
    public Augment(String name, String description, ItemStack displayItem) {
        this.name = name;
        this.description = description;
        this.displayItem = displayItem;
    }

    /**
     * The functionality provided by the augment when a player catches a fish
     * This is run before the fish are generated, Used to modify the amount of fish caught
     *
     * @param context The context of the fish event
     */
    public abstract void onInitialCatch(FishContext context);

    /**
     * The functionality provided by the augment when a player obtains a fish from the initial catch
     * This method is run for each fish caught
     *
     * @param context The context of the fish event
     * @param fish    The fish that was caught
     * @param stack   The item stack of the fish
     */
    public abstract void onFishCatch(FishContext context, Fish fish, ItemStack stack);

    /**
     * @return The name of the augment
     */
    public String getName() {
        return name;
    }

    /**
     * @return The description of the augment
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The display item of the augment
     */
    public ItemStack getDisplayItem() {
        return displayItem;
    }

}
