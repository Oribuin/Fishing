package xyz.oribuin.fishing.augment;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.fishing.augment.impl.AugmentBiomeDisrupt;
import xyz.oribuin.fishing.augment.impl.AugmentCallOfTheSea;
import xyz.oribuin.fishing.augment.impl.AugmentHotspot;
import xyz.oribuin.fishing.augment.impl.AugmentIntellect;
import xyz.oribuin.fishing.augment.impl.AugmentPerception;
import xyz.oribuin.fishing.augment.impl.AugmentPrecisionCutting;
import xyz.oribuin.fishing.augment.impl.AugmentSage;
import xyz.oribuin.fishing.augment.impl.AugmentSaturate;
import xyz.oribuin.fishing.util.math.RomanNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The registry for all augments in the plugin, This is where all augments are registered and loaded.
 * <p>
 * To register an augment, use {@link #register(Augment)} to add the augment to the registry.
 * To get an augment from the registry, use {@link #from(String)} to get the augment by its name.
 * To get all augments on a fish itemstack, use {@link #from(ItemStack)} to get all augments on the itemstack.
 * To save augments to an itemstack, use {@link #save(ItemStack, Map)} to save the augments to the itemstack.
 * To get all augments in the registry, use {@link #all()} to get all augments in the registry.
 */
public class AugmentRegistry {

    private static final Map<String, Augment> augments = new HashMap<>();

    /**
     * Initialize all the default augments into the registry to be loaded
     *
     * This method should be called when the plugin is enabled to load all the augments into the registry
     */
    public static void init() {
        augments.clear();

        register(new AugmentBiomeDisrupt());
        register(new AugmentCallOfTheSea());
        register(new AugmentHotspot());
        register(new AugmentIntellect());
        register(new AugmentPerception());
        register(new AugmentPrecisionCutting());
        register(new AugmentSage());
        register(new AugmentSaturate());
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     * Calls {@link Augment#reload()} to load the augment.
     *
     * @param augment The {@link Augment} to register
     */
    public static void register(Augment augment) {
        augment.reload(); // Load the augment
        augments.put(augment.name(), augment); // Register the augment
    }

    /**
     * Get all the active augments on a fishing rod
     *
     * @param itemStack The {@link ItemStack} to load the augments from
     *
     * @return The augments and what level they are at
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

            result.put(augment, Math.min(level, augment.maxLevel())); // Use the maximum level of the augment
        });

        return result;
    }

    /**
     * Get an augment from the registry by its name
     *
     * @param name The name of the augment
     *
     * @return The augment
     */
    public static Augment from(String name) {
        return augments.get(name);
    }

    /**
     * Save a map of augments to an itemstack and update the lore of the itemstack
     *
     * @param itemStack The {@link ItemStack} to save the augments to
     * @param augments  The augments and their levels
     */
    @SuppressWarnings("deprecation")
    public static void save(ItemStack itemStack, Map<Augment, Integer> augments) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        augments.forEach((augment, level) -> {
            int newLevel = Math.min(level, augment.maxLevel());
            container.set(augment.key(), PersistentDataType.INTEGER, newLevel);

            String current = container.getOrDefault(augment.loreKey(), PersistentDataType.STRING, augment.displayLine());
            StringPlaceholders placeholders = StringPlaceholders.of(
                    "level", newLevel,
                    "level_roman", RomanNumber.toRoman(newLevel)
            );

            container.set(augment.loreKey(), PersistentDataType.STRING, placeholders.apply(augment.displayLine()));

            // Modify the lore of the item
            List<String> lore = new ArrayList<>();
            List<String> itemLore = meta.getLore();
            String formatted = HexUtils.colorify(current);
            boolean found = false;
            if (itemLore != null) {
                lore.addAll(itemLore);
            }

            for (int index = 0; index < lore.size(); index++) {
                String line = lore.get(index);
                if (line == null) continue;
                if (found) break;

                // Check if the line contains the augment lore key
                if (line.contains(formatted)) {
                    lore.set(index, HexUtils.colorify(placeholders.apply(augment.displayLine())));
                    found = true;
                }
            }

            // If the augment was not found in the lore, add it to the end
            if (!found) {
                lore.add(HexUtils.colorify(placeholders.apply(augment.displayLine())));
            }

            meta.setLore(lore);
        });

        itemStack.setItemMeta(meta);
    }

    /**
     * Obtain all the augments in the registry as a map
     * @return The map of all augments in the registry
     */
    public static Map<String, Augment> all() {
        return augments;
    }

}
