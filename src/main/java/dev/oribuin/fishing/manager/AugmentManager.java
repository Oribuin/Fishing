package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.ConfigLoader;
import dev.oribuin.fishing.config.impl.Config;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.impl.AugmentBiomeBlend;
import dev.oribuin.fishing.model.augment.impl.AugmentEnlightened;
import dev.oribuin.fishing.model.augment.impl.AugmentFailure;import dev.oribuin.fishing.model.augment.impl.AugmentFineSlicing;
import dev.oribuin.fishing.model.augment.impl.AugmentGenius;
import dev.oribuin.fishing.model.augment.impl.AugmentHotspot;
import dev.oribuin.fishing.model.augment.impl.AugmentIndulge;
import dev.oribuin.fishing.model.augment.impl.AugmentIntuition;
import dev.oribuin.fishing.model.augment.impl.AugmentRainDance;
import dev.oribuin.fishing.model.loot.LootRegistry;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import dev.oribuin.fishing.util.math.RomanNumber;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jeff_media.morepersistentdatatypes.DataType.TAG_CONTAINER;
import static dev.oribuin.fishing.storage.util.KeyRegistry.*;

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
        loader.reload();
        register("failure", AugmentFailure.class);
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
        T augment = loader.loadConfig(augmentClass, identifier);

        augments.put(identifier.toLowerCase(), () -> loader.getClone(augmentClass));
        LootRegistry.register("augment_" + identifier.toLowerCase(), augment::getDisplayItem, augment::getPlaceholders, stack -> stack.editPersistentDataContainer(container -> {
            container.set(AUGMENT_TYPE.key(), AUGMENT_TYPE, augment.getName());
            container.set(AUGMENT_LEVEL.key(), AUGMENT_LEVEL, Math.min(augment.getLevel(), augment.getMaxLevel()));
        }));
    }

    /**
     * Get an augment from the registry by its name
     *
     * @param identifier The name of the augment
     *
     * @return The augment
     */
    @SuppressWarnings("unchecked")
    public <T extends Augment> T getAugment(String identifier) {
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
        return augments.entrySet().stream().map(x -> Map.entry(x.getKey(), x.getValue())).collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    /**
     * Get an augment from an itemstack
     *
     * @param itemStack The stack to get it from
     *
     * @return The augment item
     */

    @Nullable
    public Augment getAugmentStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        String identifier = container.get(AUGMENT_TYPE.key(), AUGMENT_TYPE);
        int level = container.getOrDefault(AUGMENT_LEVEL.key(), AUGMENT_LEVEL, 1);

        Augment augment = this.plugin.getAugmentManager().getAugment(identifier);
        if (augment != null) augment.setLevel(level);
        return augment;
    }

    /**
     * Save a map of augments to an itemstack and update the lore of the itemstack
     *
     * @param itemStack The {@link ItemStack} to save the augments to
     * @param augments  The augments and their levels
     */
    public void applyAugments(ItemStack itemStack, Map<String, Augment> augments) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        // Modify the lore of the item
        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<Component> lore = new ArrayList<>();
        List<Component> itemLore = meta.lore();
        if (itemLore != null) {
            lore.addAll(itemLore);
        }

        // region Add the header for the description
        int augmentsStart = lore.isEmpty() ? 0 : lore.size() - 1;
        Integer headerIndex = container.get(AUGMENT_HEADER.key(), AUGMENT_HEADER);
        int footerIndex = container.getOrDefault(AUGMENT_FOOTER.key(), AUGMENT_FOOTER, 0);
        if (headerIndex == null) headerIndex = augmentsStart;

        if (footerIndex > headerIndex) {
            lore.subList(headerIndex, footerIndex + 1).clear();
        }

        container.set(AUGMENT_HEADER.key(), AUGMENT_HEADER, headerIndex);
        for (String headerText : Config.get().getAugmentsHeader()) {
            lore.add(FishUtils.kyorify(headerText));
        }
        // endregion

        AtomicInteger furthestIndex = new AtomicInteger(headerIndex);

        // region Add the augments to the description
        Comparator<Augment> augmentCompare = Comparator.comparing(Augment::getLevel)
                .reversed()
                .thenComparing(Augment::getName);
        PersistentDataAdapterContext context = container.getAdapterContext();
        PersistentDataContainer augmentsContainer = context.newPersistentDataContainer();
        List<Augment> target = augments.values().stream().sorted(augmentCompare).toList();
        for (Augment augment : target) {
            // Write the augment to the container
            PersistentDataContainer augmentContainer = context.newPersistentDataContainer();
            augment.writeContainer(augmentContainer);
            augmentsContainer.set(augment.getNamespace(), TAG_CONTAINER, augmentContainer);

            Placeholders placeholders = Placeholders.of("level", augment.getLevel(), "level_roman", RomanNumber.toRoman(augment.getLevel()));

            // region Add the augment to the description
            lore.add(FishUtils.kyorify(augment.getDisplayLine(), placeholders));
            furthestIndex.incrementAndGet();
        }

        container.set(ROD_AUGMENTS.key(), ROD_AUGMENTS, augmentsContainer);

        // endregion
        // region Add the footer for the description
        List<String> footer = Config.get().getAugmentsFooter();
        if (!footer.isEmpty()) {
            for (String footerText : footer) {
                lore.add(FishUtils.kyorify(footerText));
            }

            furthestIndex.set(lore.size() - 1);
        }

        // endregion
        container.set(AUGMENT_FOOTER.key(), AUGMENT_FOOTER, furthestIndex.get());
        meta.lore(lore);
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
    public Map<String, Augment> getAugments(@Nullable ItemStack itemStack) {
        if (itemStack == null) return new HashMap<>();

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return new HashMap<>();

        PersistentDataContainer rodContainer = meta.getPersistentDataContainer();
        PersistentDataContainer augmentsContainer = rodContainer.get(ROD_AUGMENTS.key(), ROD_AUGMENTS);
        if (augmentsContainer == null) return new HashMap<>();

        // Load the augments from the item meta
        return augmentsContainer.getKeys().stream().map(namespacedKey -> {
                    Supplier<? extends Augment> augmentSupplier = augments.get(namespacedKey.value());
                    if (augmentSupplier == null) return null;

                    Augment augment = augmentSupplier.get();

                    PersistentDataContainer augmentContainer = augmentsContainer.get(namespacedKey, TAG_CONTAINER);
                    if (augmentContainer == null) return null;

                    augment.readContainer(augmentContainer);
                    return augment;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Augment::getName, augment -> augment));
    }

    /**
     * Get the sum strength of all the augments
     *
     * @param augments The equipped augments
     *
     * @return The strength of the augments
     */
    public int getStrength(Map<String, Augment> augments) {
        return augments.values().stream().mapToInt(Augment::getLevel).sum();
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
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType().isAir()) continue; // Ignore null/air
            if (stack.getType() != Material.FISHING_ROD) continue; // Make sure it's actually a fishing rod

            Map<String, Augment> available = getAugments(stack);
            if (available.isEmpty()) continue;

            int currentStr = getStrength(available);
            if (currentStr <= strength) continue;

            contender = stack;
            strength = currentStr;
        }

        return contender;
    }

}
