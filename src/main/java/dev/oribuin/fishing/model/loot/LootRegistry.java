package dev.oribuin.fishing.model.loot;

import dev.oribuin.fishing.config.impl.LootConfig;
import dev.oribuin.fishing.config.impl.TotemConfig;
import dev.oribuin.fishing.config.item.ItemConstruct;
import dev.oribuin.fishing.util.Placeholders;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootRegistry {

    public static final Map<String, FishLoot> REGISTRY = new ConcurrentHashMap<>();
    
    static {
        // Register fish loot into the plugin
        LootConfig.get().getItems().forEach((s, construct) -> LootRegistry.register(s, () -> construct));
    }

    /**
     * Register a new item into the plugin to get
     *
     * @param identifier The identifier for the item
     * @param construct  The construct to supply
     * @param additional Any additional functionality to apply
     */
    public static void register(@NotNull String identifier, Supplier<@NotNull ItemConstruct> construct, Supplier<@NotNull Placeholders> placeholders, Consumer<@NotNull ItemStack> additional) {
        REGISTRY.put(identifier.toLowerCase(), new FishLoot(identifier, construct, placeholders, additional));
    }
    
    /**
     * Register a new item into the plugin to get
     *
     * @param identifier The identifier for the item
     * @param construct  The construct to supply
     * @param additional Any additional functionality to apply
     */
    public static void register(@NotNull String identifier, Supplier<@NotNull ItemConstruct> construct, Consumer<@NotNull ItemStack> additional) {
        REGISTRY.put(identifier.toLowerCase(), new FishLoot(identifier, construct, Placeholders::empty, additional));
    }

    /**
     * Register a new item into the plugin to get
     *
     * @param identifier The identifier for the item
     * @param construct  The construct to supply
     */
    public static void register(@NotNull String identifier, Supplier<@NotNull ItemConstruct> construct) {
        REGISTRY.put(identifier.toLowerCase(), new FishLoot(identifier, construct));
    }

    /**
     * Get an itemstack from the plugin
     *
     * @param identifier   The identifier for the plugin
     * @param placeholders Any placeholders that may need to be applied
     *
     * @return The itemstack to create
     */
    @Nullable
    public static ItemStack from(String identifier, Placeholders placeholders) {
        FishLoot loot = REGISTRY.get(identifier);
        if (loot == null) return null;

        return loot.create(placeholders);
    } 
    
    /**
     * Get an itemstack from the plugin
     *
     * @param identifier   The identifier for the plugin
     *
     * @return The itemstack to create
     */
    @Nullable
    public static ItemStack from(String identifier) {
        FishLoot loot = REGISTRY.get(identifier);
        if (loot == null) return null;

        return loot.create();
    }

}
