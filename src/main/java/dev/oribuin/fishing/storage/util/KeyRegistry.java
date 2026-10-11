package dev.oribuin.fishing.storage.util;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.jeff_media.morepersistentdatatypes.datatypes.collections.MapDataType;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.totem.TotemPrivacy;
import io.papermc.paper.persistence.PersistentDataKey;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.jeff_media.morepersistentdatatypes.DataType.BOOLEAN;
import static com.jeff_media.morepersistentdatatypes.DataType.INTEGER;
import static com.jeff_media.morepersistentdatatypes.DataType.ITEM_STACK;
import static com.jeff_media.morepersistentdatatypes.DataType.LOCATION;
import static com.jeff_media.morepersistentdatatypes.DataType.LONG;
import static com.jeff_media.morepersistentdatatypes.DataType.STRING;
import static com.jeff_media.morepersistentdatatypes.DataType.TAG_CONTAINER;
import static com.jeff_media.morepersistentdatatypes.DataType.asEnum;
import static com.jeff_media.morepersistentdatatypes.DataType.asMap;
import static com.jeff_media.morepersistentdatatypes.DataType.asSet;

/**
 * Registry for all the {@link NamespacedKey} used in the plugin, for easy access.
 * <p>
 * This is quite unnecessary, but it's a good practice to keep all the keys in one place.
 * Especially when another plugin wants to establish their own keys
 */
public class KeyRegistry {

    public static final Set<PersistentDataKey<Double>> STATISTICS = new HashSet<>();

    // region Long Data Types
    public static final MapDataType<Map<Integer, ItemStack>, Integer, ItemStack> INVENTORY = asMap(
            INTEGER,
            ITEM_STACK
    );

    public static MapDataType<Map<String, Integer>, String, Integer> LEVEL_MAPPING = asMap(
            STRING,
            INTEGER
    );

    // endregion

    // region Fishing Rod Values
    public static @NotNull PersistentDataKey<String> ROD_RARITY = register("rod_rarity", STRING);
    public static @NotNull PersistentDataKey<Integer> ROD_BASE_CAPACITY = register("rod_base_capacity", INTEGER);
    public static @NotNull PersistentDataKey<PersistentDataContainer> ROD_AUGMENTS = register(
            "rod_augments",
            TAG_CONTAINER
    );
    // endregion
    // region Rod Statistics
    public static PersistentDataKey<Double> STAT_ROD_CAUGHT = registerStat("rod_caught");
    public static PersistentDataKey<Double> STAT_ROD_GUTTED = registerStat("rod_gutted");
    public static PersistentDataKey<Double> STAT_ROD_SOLD = registerStat("rod_sold");
    // endregion

    // region Fish Data Types
    public static @NotNull PersistentDataKey<String> FISH_TYPE = register("fish_type", STRING);
    public static @NotNull PersistentDataKey<String> FISH_NAME = register("fish_name", STRING);
    // endregion

    // region Augment Data Types
    public static @NotNull PersistentDataKey<Integer> AUGMENT_HEADER = register("augment_header", INTEGER);
    public static @NotNull PersistentDataKey<Integer> AUGMENT_FOOTER = register("augment_footer", INTEGER);
    public static @NotNull PersistentDataKey<String> AUGMENT_TYPE = register("augment_type", STRING);
    public static @NotNull PersistentDataKey<Integer> AUGMENT_LEVEL = register("augment_level", INTEGER);
    // endregion

    // region Totem Data Types
    public static @NotNull PersistentDataKey<Location> TOTEM_POSITION = register("totem_position", LOCATION);
    public static @NotNull PersistentDataKey<UUID> TOTEM_OWNER = register("totem_owner", DataType.UUID);
    public static @NotNull PersistentDataKey<Boolean> TOTEM_ACTIVE = register("totem_active", BOOLEAN);
    public static @NotNull PersistentDataKey<Long> TOTEM_LAST_ACTIVE = register("totem_last_active", LONG);
    public static @NotNull PersistentDataKey<String> TOTEM_OWNER_NAME = register("totem_owner_name", STRING);
    public static @NotNull PersistentDataKey<String> TOTEM_DISPLAY_NAME = register("totem_display_name", STRING);
    public static @NotNull PersistentDataKey<String> TOTEM_SKIN = register("totem_skin", STRING);
    public static @NotNull PersistentDataKey<Integer> TOTEM_LEVEL = register("totem_level", INTEGER);
    public static @NotNull PersistentDataKey<TotemPrivacy> TOTEM_PRIVACY = register("totem_privacy", asEnum(TotemPrivacy.class));
    public static @NotNull PersistentDataKey<PersistentDataContainer> TOTEM_UPGRADES = register("totem_upgrades", TAG_CONTAINER);
    public static @NotNull PersistentDataKey<Set<UUID>> TOTEM_USERS = register("totem_users", asSet(DataType.UUID));
    public static @NotNull PersistentDataKey<Map<Integer, ItemStack>> TOTEM_BAG = register("totem_bag", INVENTORY);
    public static @NotNull PersistentDataKey<Integer> TOTEM_BAG_CAPACITY = register("totem_bag_capacity", INTEGER);
    // endregion

    // region Totem Upgrade Keys
    public static @NotNull PersistentDataKey<Integer> TOTEM_UPGRADE_LEVEL = register("upgrade_level", INTEGER);
    public static @NotNull PersistentDataKey<Boolean> TOTEM_UPGRADE_ACTIVATED = register("upgrade_activated", BOOLEAN);
    // endregion

    // region Fishing Hook Keys
    public static @NotNull PersistentDataKey<Boolean> RAIN_INFLUENCED = register("rain_influenced", BOOLEAN);
    // endregion

    /**
     * A utility function to shorten the namespace key
     *
     * @param name The namespace to register
     *
     * @return The resulting namespace
     */
    @NotNull
    public static <C> PersistentDataKey<C> register(String name, PersistentDataType<?, C> dataType) {
        return PersistentDataKey.of(new NamespacedKey(FishingPlugin.get(), name.toLowerCase()), dataType);
    }

    /**
     * Creates and registers a statistic within the plugin for later use.
     * <p>
     * All statistics will begin with "statistic_" and return Doubles
     *
     * @param name The namespace to register
     *
     * @return The resulting namespace
     *
     * @see dev.oribuin.fishing.manager.RodManager#incrementStatistic(ItemStack, PersistentDataKey, double) )
     * @see dev.oribuin.fishing.manager.RodManager#getStatistic(ItemStack, FishDataType)
     */
    public static PersistentDataKey<Double> registerStat(String name) {
        PersistentDataKey<Double> result = register("statistic_" + name, PersistentDataType.DOUBLE);
        STATISTICS.add(result);
        return result;
    }

}

