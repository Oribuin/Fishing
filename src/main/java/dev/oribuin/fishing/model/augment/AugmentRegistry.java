package dev.oribuin.fishing.model.augment;

import com.google.common.base.Supplier;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.model.augment.impl.AugmentBiomeBlend;
import dev.oribuin.fishing.model.augment.impl.AugmentEnlightened;
import dev.oribuin.fishing.model.augment.impl.AugmentFineSlicing;
import dev.oribuin.fishing.model.augment.impl.AugmentGenius;
import dev.oribuin.fishing.model.augment.impl.AugmentHotspot;
import dev.oribuin.fishing.model.augment.impl.AugmentIndulge;
import dev.oribuin.fishing.model.augment.impl.AugmentIntuition;
import dev.oribuin.fishing.model.augment.impl.AugmentRainDance;
import dev.oribuin.fishing.util.math.RomanNumber;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
     * <p>
     * This should not be called outside of {@link AugmentRegistry#init()}
     */
    private AugmentRegistry() {}

    /**
     * Initialize all the default augments into the registry to be loaded
     * <p>
     * This method should be called when the plugin is enabled to load all the augments into the registry
     */
    public static void init() {
        augments.clear();

        register(AugmentBiomeBlend::new);
        register(AugmentRainDance::new);
        register(AugmentHotspot::new);
        register(AugmentGenius::new);
        register(AugmentIntuition::new);
        register(AugmentFineSlicing::new);
        register(AugmentEnlightened::new);
        register(AugmentIndulge::new);
    }

    /**
     * Call an event in the plugin to be used by the augments
     *
     * @param augments The augments to call the event with
     * @param event    The event to call
     */
    public static void callEvent(Map<Augment, Integer> augments, Event event) {
        if (!event.callEvent()) return; // Call the event through bukkit to allow other plugins to listen to the event

        Map<Augment, Integer> applicable = new HashMap<>(augments);
        applicable.keySet().removeIf(x -> !x.events().containsKey(event.getClass()));

        // Form a Map.Entry<Augment, Integer> with a EventWrapper to call the event
        applicable.entrySet()
                .stream()
                .map(entry -> new AugmentEventWrapper(entry.getKey(), entry.getValue(), event))
                .sorted(Comparator.comparingInt(o -> o.wrapper.order().getSlot()))
                .forEach(x -> x.wrapper.accept(event, x.level()));
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     * Calls {@link Augment#reload()} to load the augment.
     *
     * @param supplier The {@link Augment} to register
     */
    public static void register(Supplier<Augment> supplier) {
        Augment augment = supplier.get();
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
     *
     * @return The map of all augments in the registry
     */
    public static Map<String, Augment> all() {
        return augments;
    }


    /**
     * Wrapper for an augment to be registered with a function
     */
    public static final class AugmentEventWrapper {

        private final Augment augment;
        private final int level;
        private final FishEventHandler.EventWrapper<?> wrapper;

        /**
         * Wrapper for an augment to be registered with a function in an event, Used to make the stream less annoying
         *
         * @param augment The augment to be registered
         * @param level   The level of the augment
         * @param event   The event to be registered
         *
         * @see AugmentRegistry#callEvent(Map, Event) Where this is used
         */
        public AugmentEventWrapper(Augment augment, int level, Event event) {
            this.augment = augment;
            this.level = level;
            this.wrapper = augment.getWrapper(event.getClass());
        }

        /**
         * Get the augment that was registered
         *
         * @return The {@link Augment} that was registered
         */
        public Augment augment() {
            return augment;
        }

        /**
         * Get the level of the augment that was registered
         *
         * @return The level of the augment that was registered
         */
        public int level() {
            return level;
        }

        /**
         * Get the event wrapper that was registered
         *
         * @return The event wrapper that was registered
         */
        public FishEventHandler.EventWrapper<?> wrapper() {
            return wrapper;
        }

    }

}
