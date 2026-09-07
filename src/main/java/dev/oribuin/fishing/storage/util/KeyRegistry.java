package dev.oribuin.fishing.storage.util;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.jeff_media.morepersistentdatatypes.datatypes.collections.MapDataType;
import dev.oribuin.fishing.model.totem.TotemPrivacy;
import dev.oribuin.fishing.storage.persistent.FishDataType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.jeff_media.morepersistentdatatypes.DataType.*;

/**
 * Registry for all the {@link NamespacedKey} used in the plugin, for easy access.
 * <p>
 * This is quite unnecessary, but it's a good practice to keep all the keys in one place.
 * Especially when another plugin wants to establish their own keys
 */
public class KeyRegistry {

    public static final Set<FishDataType<Double, Double>> STATISTICS = new HashSet<>();

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
    public static FishDataType<String, String> ROD_RARITY = register("rod_rarity", STRING);
    public static FishDataType<Integer, Integer> ROD_BASE_CAPACITY = register("rod_base_capacity", INTEGER);
    public static FishDataType<PersistentDataContainer, PersistentDataContainer> ROD_AUGMENTS = register(
            "rod_augments",
            TAG_CONTAINER
    );
    // endregion
    // region Rod Statistics
    public static FishDataType<Double, Double> STAT_ROD_CAUGHT = registerStat("rod_caught");
    public static FishDataType<Double, Double> STAT_ROD_GUTTED = registerStat("rod_gutted");
    public static FishDataType<Double, Double> STAT_ROD_SOLD = registerStat("rod_sold");
    // endregion

    // region Fish Data Types
    public static FishDataType<String, String> FISH_TYPE = register("fish_type", STRING);
    public static FishDataType<String, String> FISH_NAME = register("fish_name", STRING);
    // endregion

    // region Augment Data Types
    public static FishDataType<Integer, Integer> AUGMENT_HEADER = register("augment_header", INTEGER);
    public static FishDataType<Integer, Integer> AUGMENT_FOOTER = register("augment_footer", INTEGER);
    public static FishDataType<String, String> AUGMENT_TYPE = register("augment_type", STRING);
    public static FishDataType<Integer, Integer> AUGMENT_LEVEL = register("augment_level", INTEGER);
    // endregion

    // region Totem Data Types
    public static FishDataType<byte[], Location> TOTEM_POSITION = register("totem_position", LOCATION);
    public static FishDataType<byte[], UUID> TOTEM_OWNER = register("totem_owner", DataType.UUID);
    public static FishDataType<Byte, Boolean> TOTEM_ACTIVE = register("totem_active", BOOLEAN);
    public static FishDataType<Long, Long> TOTEM_LAST_ACTIVE = register("totem_last_active", LONG);
    public static FishDataType<String, String> TOTEM_OWNER_NAME = register("totem_owner_name", STRING);
    public static FishDataType<String, String> TOTEM_DISPLAY_NAME = register("totem_display_name", STRING);
    public static FishDataType<String, String> TOTEM_SKIN = register("totem_skin", STRING);
    public static FishDataType<Integer, Integer> TOTEM_LEVEL = register("totem_level", INTEGER);
    public static FishDataType<String, TotemPrivacy> TOTEM_PRIVACY = register("totem_privacy", asEnum(TotemPrivacy.class));
    public static FishDataType<PersistentDataContainer, PersistentDataContainer> TOTEM_UPGRADES = register("totem_upgrades", TAG_CONTAINER);
    public static FishDataType<PersistentDataContainer, Set<UUID>> TOTEM_USERS = register("totem_users", asSet(DataType.UUID));
    public static FishDataType<PersistentDataContainer, Map<Integer, ItemStack>> TOTEM_BAG = register("totem_bag", INVENTORY);
    public static FishDataType<Integer, Integer> TOTEM_BAG_CAPACITY = register("totem_bag_capacity", INTEGER);
    // endregion

    // region Totem Upgrade Keys
    public static FishDataType<Integer, Integer> TOTEM_UPGRADE_LEVEL = register("upgrade_level", INTEGER);
    public static FishDataType<Byte, Boolean> TOTEM_UPGRADE_ACTIVATED = register("upgrade_activated", BOOLEAN);
    // endregion

    /**
     * A utility function to shorten the namespace key
     *
     * @param name The namespace to register
     *
     * @return The resulting namespace
     */
    public static <P, C> FishDataType<P, C> register(String name, PersistentDataType<P, C> dataType) {
        return new FishDataType<>(name, dataType);
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
     * @see dev.oribuin.fishing.manager.RodManager#incrementStatistic(ItemStack, FishDataType, double) )
     * @see dev.oribuin.fishing.manager.RodManager#getStatistic(ItemStack, FishDataType)
     */
    public static FishDataType<Double, Double> registerStat(String name) {
        FishDataType<Double, Double> result = new FishDataType<>("statistic_" + name, DOUBLE);
        STATISTICS.add(result);
        return result;
    }

}

