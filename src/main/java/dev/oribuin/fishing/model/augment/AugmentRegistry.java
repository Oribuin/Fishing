package dev.oribuin.fishing.model.augment;

import com.google.common.base.Supplier;
import dev.oribuin.fishing.model.augment.impl.AugmentBiomeBlend;
import dev.oribuin.fishing.model.augment.impl.AugmentEnlightened;
import dev.oribuin.fishing.model.augment.impl.AugmentFineSlicing;
import dev.oribuin.fishing.model.augment.impl.AugmentGenius;
import dev.oribuin.fishing.model.augment.impl.AugmentHotspot;
import dev.oribuin.fishing.model.augment.impl.AugmentIndulge;
import dev.oribuin.fishing.model.augment.impl.AugmentIntuition;
import dev.oribuin.fishing.model.augment.impl.AugmentRainDance;
import dev.oribuin.fishing.util.Placeholders;
import dev.oribuin.fishing.util.math.RomanNumber;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry for all augments in the plugin, This is where all augments are registered and loaded.
 * <p>
 * To register an augment, use {@link #register(Supplier)} to add the augment to the registry.
 * To get an augment from the registry, use {@link #from(String)} to get the augment by its name.
 * To get all augments on a fish itemstack, use {@link #from(ItemStack)} to get all augments on the itemstack.
 * To save augments to an itemstack, use {@link #save(ItemStack, Map)} to save the augments to the itemstack.
 * To get all augments in the registry, use {@link #all()} to get all augments in the registry.
 */
public class AugmentRegistry {

    private static final Map<String, Augment> augments = new HashMap<>();

    /**
     * A private constructor to prevent instantiation of the class
     */
    private AugmentRegistry() {}

    static {
        register(AugmentBiomeBlend::new);
        register(AugmentEnlightened::new);
        register(AugmentFineSlicing::new);
        register(AugmentGenius::new);
        register(AugmentHotspot::new);
        register(AugmentIndulge::new);
        register(AugmentIntuition::new);
        //        register(AugmentMakeItRain::new); // TODO: Redo
        register(AugmentRainDance::new);
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     *
     * @param supplier The {@link Augment} to register
     */
    public static void register(Supplier<Augment> supplier) {
        Augment augment = supplier.get();
        augments.put(augment.getName().toLowerCase(), augment); // Register the augment
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

            result.put(augment, Math.min(level, augment.getMaxLevel())); // Use the maximum level of the augment
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
            int newLevel = Math.min(level, augment.getMaxLevel());
            container.set(augment.key(), PersistentDataType.INTEGER, newLevel);

            String current = container.getOrDefault(augment.loreKey(), PersistentDataType.STRING, augment.getDisplayLine());
            Placeholders placeholders = Placeholders.of(
                    "level", newLevel,
                    "level_roman", RomanNumber.toRoman(newLevel)
            );

            // TODO: Redo lore adjustment to start using components
            //            container.set(
            //                    augment.loreKey(), 
            //                    DataType.STRING, 
            //                    placeholders.apply(augment.getDisplayLine()));
            //
            //            // Modify the lore of the item
            //            List<String> lore = new ArrayList<>();
            //            List<String> itemLore = meta.getLore();
            //            String formatted = HexUtils.colorify(current);
            //            boolean found = false;
            //            if (itemLore != null) {
            //                lore.addAll(itemLore);
            //            }
            //
            //            for (int index = 0; index < lore.size(); index++) {
            //                String line = lore.get(index);
            //                if (line == null) continue;
            //                if (found) break;
            //
            //                // Check if the line contains the augment lore key
            //                if (line.contains(formatted)) {
            //                    lore.set(index, HexUtils.colorify(placeholders.apply(augment.getDisplayLine())));
            //                    found = true;
            //                }
            //            }
            //
            //            // If the augment was not found in the lore, add it to the end
            //            if (!found) {
            //                lore.add(HexUtils.colorify(placeholders.apply(augment.getDisplayLine())));
            //            }
            //
            //            meta.setLore(lore);
        });

        itemStack.setItemMeta(meta);
    }

    /**
     * Reload all the augments in the registry
     */
    public static void reload() {
        augments.values().forEach(augment -> {
            augment.getConfigHandler().unload();
            augment.getConfigHandler().save();
        });
    }

    /**
     * Obtain all the augments in the registry as a map
     *
     * @return The map of all augments in the registry
     */
    public static Map<String, Augment> all() {
        return augments;
    }

}
