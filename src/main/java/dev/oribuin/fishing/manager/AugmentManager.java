package dev.oribuin.fishing.manager;

import com.google.common.base.Supplier;
import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.impl.AugmentBiomeBlend;
import dev.oribuin.fishing.model.augment.impl.AugmentEnlightened;
import dev.oribuin.fishing.model.augment.impl.AugmentFineSlicing;
import dev.oribuin.fishing.model.augment.impl.AugmentGenius;
import dev.oribuin.fishing.model.augment.impl.AugmentHotspot;
import dev.oribuin.fishing.model.augment.impl.AugmentIndulge;
import dev.oribuin.fishing.model.augment.impl.AugmentIntuition;
import dev.oribuin.fishing.model.augment.impl.AugmentRainDance;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.oribuin.fishing.util.math.RomanNumber;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AugmentManager implements Manager {

    private static final File AUGMENTS_FOLDER = new File(FishingPlugin.get().getDataFolder(), "augments");
    private static final Map<String, Augment> augments = new HashMap<>();
    private static final ConfigLoader loader = new ConfigLoader(AUGMENTS_FOLDER.toPath());
    private final FishingPlugin plugin;

    public AugmentManager(FishingPlugin plugin) {
        this.plugin = plugin;
        this.reload(this.plugin);
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        register(AugmentBiomeBlend::new);
        register(AugmentEnlightened::new);
        register(AugmentFineSlicing::new);
        register(AugmentGenius::new);
        register(AugmentHotspot::new);
        register(AugmentIndulge::new);
        register(AugmentIntuition::new);
        //        register(AugmentMakeItRain::new); // TODO: Redo
        register(AugmentRainDance::new);
        
        this.plugin.getLogger().info("Loaded a total of [" + augments.size() + "] augments into the plugin");
    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
        loader.close();
        augments.clear();
    }

    /**
     * Loads an augment into the registry to be used in the plugin and caches it.
     *
     * @param supplier The {@link Augment} to register
     */
    public static void register(Supplier<Augment> supplier) {
        Augment augment = supplier.get();
        augments.put(augment.getName().toLowerCase(), augment); // Register the augment

        loader.loadConfig(augment.getClass(), augment.getName());
    }

    /**
     * Get an augment from the registry by its name
     *
     * @param name The name of the augment
     *
     * @return The augment
     */
    public Augment from(String name) {
        return augments.get(name);
    }

    /**
     * Obtain all the augments in the registry as a map
     *
     * @return The map of all augments in the registry
     */
    public Map<String, Augment> getAugments() {
        return augments;
    }

    /**
     * Save a map of augments to an itemstack and update the lore of the itemstack
     *
     * @param itemStack The {@link ItemStack} to save the augments to
     * @param augments  The augments and their levels
     */
    public void save(ItemStack itemStack, Map<Augment, Integer> augments) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        augments.forEach((augment, level) -> {
            int previousLevel = container.getOrDefault(augment.key(), PersistentDataType.INTEGER, 0);
            int newLevel = Math.min(level, augment.getMaxLevel());
            container.set(augment.key(), PersistentDataType.INTEGER, newLevel);

            Placeholders placeholders = Placeholders.of(
                    "level", previousLevel,
                    "level_roman", RomanNumber.toRoman(newLevel)
            );

            // Modify the lore of the item
            List<Component> lore = new ArrayList<>();
            List<Component> itemLore = meta.lore();
            if (itemLore != null) {
                lore.addAll(itemLore);
            }
            
            Component text = FishUtils.kyorify(augment.getDisplayLine(), placeholders);
            Integer currentIndex = container.get(augment.loreKey(), PersistentDataType.INTEGER);
            if (currentIndex != null) {
                lore.set(currentIndex, text);
            } else {
                lore.add(text);
                container.set(augment.loreKey(), DataType.INTEGER, lore.size() - 1);
            }

            meta.lore(lore);
        });

        itemStack.setItemMeta(meta);
    }

    /**
     * Get all the active augments on a fishing rod
     *
     * @param itemStack The {@link ItemStack} to load the augments from
     *
     * @return The augments and what level they are at
     */
    public Map<Augment, Integer> from(ItemStack itemStack) {
        Map<Augment, Integer> result = new HashMap<>();
        // Implementation here
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return result;

        // Load the augments from the item meta
        PersistentDataContainer container = meta.getPersistentDataContainer();
        this.augments.forEach((name, augment) -> {
            Integer level = container.get(augment.key(), PersistentDataType.INTEGER);
            if (level == null || level <= 0) return;

            result.put(augment, Math.min(level, augment.getMaxLevel())); // Use the maximum level of the augment
        });

        return result;
    }
}
