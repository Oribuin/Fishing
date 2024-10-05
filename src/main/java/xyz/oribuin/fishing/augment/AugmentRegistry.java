package xyz.oribuin.fishing.augment;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.fishing.augment.impl.AugmentBiomeDisrupt;
import xyz.oribuin.fishing.augment.impl.AugmentCallOfTheSea;
import xyz.oribuin.fishing.augment.impl.AugmentHotspot;
import xyz.oribuin.fishing.augment.impl.AugmentSaturate;

import java.util.HashMap;
import java.util.Map;

public class AugmentRegistry {

    private static final Map<String, Augment> augments = new HashMap<>();


    /**
     * The plugin initializes all the augments and registers them
     */
    public static void init() {
        augments.clear();

        register(new AugmentBiomeDisrupt());
        register(new AugmentCallOfTheSea());
        register(new AugmentHotspot());
        register(new AugmentSaturate());
    }

    /**
     * Register an augment into the registry to be loaded
     *
     * @param augment The augment to register
     */
    public static void register(Augment augment) {
        augment.reload(); // Load the augment
        augments.put(augment.name(), augment); // Register the augment
    }

    /**
     * Load all the augments that currently exist on the itemstack
     *
     * @param itemStack The itemstack to load the augments from
     *
     * @return The augments and their levels
     */
    public static Map<Augment, Integer> from(ItemStack itemStack) {
        Map<Augment, Integer> result = new HashMap<>();
        // Implementation here
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return result;

        // Load the augments from the item meta
        PersistentDataContainer container = meta.getPersistentDataContainer();
        augments.forEach((name, augment) -> {
            Integer level = container.get(augment.key(), PersistentDataType.INTEGER);
            if (level == null || level <= 0) return;

            result.put(augment, level);
        });

        return result;
    }

    /**
     * Save the augments to the itemstack meta
     *
     * @param itemStack The itemstack to save the augments to
     * @param augments  The augments and their levels
     */
    public static void save(ItemStack itemStack, Map<Augment, Integer> augments) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        augments.forEach((augment, level) -> {
            container.set(augment.key(), PersistentDataType.INTEGER, level);
        });

        itemStack.setItemMeta(meta);
    }

    public static Map<String, Augment> all() {
        return augments;
    }

}
