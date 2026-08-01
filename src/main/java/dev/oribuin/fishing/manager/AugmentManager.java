package dev.oribuin.fishing.manager;

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
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.oribuin.fishing.storage.util.KeyRegistry.AUGMENT_TYPE;

public class AugmentManager implements Manager {

    private static final File AUGMENTS_FOLDER = new File(FishingPlugin.get().getDataFolder(), "augments");
    private static final ConfigLoader loader = new ConfigLoader(AUGMENTS_FOLDER.toPath());
    private static final Map<String, Supplier<? extends Augment>> augments = new HashMap<>();
    private final FishingPlugin plugin;

    public AugmentManager(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {
        register("biome_blend", AugmentBiomeBlend.class);
        register("enlightened", AugmentEnlightened.class);
        register("fine_slicing", AugmentFineSlicing.class);
        register("genius", AugmentGenius.class);
        register("hotspot", AugmentHotspot.class);
        register("indulge", AugmentIndulge.class);
        register("intuition", AugmentIntuition.class);
        //        register(AugmentMakeItRain::new); // TODO: Redo
        register("rain_dance", AugmentRainDance.class);

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
     * @param identifier   The identifier for the augment
     * @param augmentClass The augment to register
     * @param <T>          The type of augment to register
     */
    public static <T extends Augment> void register(String identifier, Class<T> augmentClass) {
        loader.loadConfig(augmentClass, identifier);

        augments.put(identifier.toLowerCase(), () -> loader.getClone(augmentClass));
    }

    /**
     * Get an augment from the registry by its name
     *
     * @param identifier The name of the augment
     *
     * @return The augment
     */
    @SuppressWarnings("unchecked")
    public <T extends Augment> T from(String identifier) {
        if (identifier == null) return null;
        
        Supplier<? extends Augment> supplier = augments.get(identifier);
        if (supplier == null) return null;

        return (T) supplier.get();
    }

    /**
     * Obtain all the augments in the registry as a map
     *
     * @return The map of all augments in the registry
     */
    public Map<String, Augment> getAugments() {
        return augments.entrySet().stream()
                .map(x -> Map.entry(x.getKey(), x.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }
    
    @Nullable
    public Augment getAugment(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String identifier = container.get(AUGMENT_TYPE.key(), AUGMENT_TYPE);
        
        return this.plugin.getAugmentManager().from(identifier);
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
            int previousLevel = container.getOrDefault(augment.getNamespace(), PersistentDataType.INTEGER, 0);
            int newLevel = Math.min(level, augment.getMaxLevel());
            container.set(augment.getNamespace(), PersistentDataType.INTEGER, newLevel);

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
            Integer currentIndex = container.get(augment.getLoreNamespace(), PersistentDataType.INTEGER);
            if (currentIndex != null) {
                lore.set(currentIndex, text);
            } else {
                lore.add(text);
                container.set(augment.getLoreNamespace(), DataType.INTEGER, lore.size() - 1);
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
    @NotNull
    public Map<Augment, Integer> from(@Nullable ItemStack itemStack) {
        if (itemStack == null) return new HashMap<>();

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return new HashMap<>();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Load the augments from the item meta
        Map<Augment, Integer> result = new HashMap<>();
        augments.forEach((name, supplier) -> {
            Augment augment = supplier.get();
            if (augment == null) return;

            Integer level = container.get(augment.getNamespace(), PersistentDataType.INTEGER);
            if (level == null || level <= 0) return;

            augment.setLevel(level);

            result.put(augment, Math.min(level, augment.getMaxLevel())); // Use the maximum level of the augment
        });

        return result;
    }

    /**
     * Get the sum strength of all the augments
     *
     * @param augments The equipped augments
     *
     * @return The strength of the augments
     */
    public int getStrength(Map<Augment, Integer> augments) {
        return augments.values().stream().mapToInt(i -> i).sum();
    }

    /**
     * Get the strongest equipped rod inside an inventory
     *
     * @param inventory The inventory to checkl
     *
     * @return The strongest rod
     */
    @Nullable
    public ItemStack getStrongestRod(@NotNull Inventory inventory) {
        ItemStack contender = null;
        int strength = 0;
        Map<Augment, Integer> contenderAugments = null;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType().isAir()) continue; // Ignore null/air
            if (stack.getType() != Material.FISHING_ROD) continue; // Make sure its actually a fishing rod

            Map<Augment, Integer> available = from(stack);
            if (available.isEmpty()) continue;

            int currentStr = getStrength(available);
            if (currentStr <= strength) continue;

            contender = stack;
            strength = currentStr;
        }

        return contender;
    }

}
