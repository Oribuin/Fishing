package dev.oribuin.fishing.model.totem.upgrade;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.impl.UpgradeTotemCooldown;
import dev.oribuin.fishing.model.totem.upgrade.impl.UpgradeTotemDuration;
import dev.oribuin.fishing.model.totem.upgrade.impl.UpgradeTotemRadius;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UpgradeRegistry {

    private static final Map<String, TotemUpgrade> upgrades = new HashMap<>();
    
    public static final UpgradeTotemCooldown COOLDOWN_UPGRADE = register(UpgradeTotemCooldown::new);
    public static final UpgradeTotemDuration DURATION_UPGRADE = register(UpgradeTotemDuration::new);
    public static final UpgradeTotemRadius RADIUS_UPGRADE = register(UpgradeTotemRadius::new);

    /**
     * A private constructor to prevent instantiation of this class
     *
     * @throws IllegalStateException If the class is instantiated
     */
    private UpgradeRegistry() {
        throw new IllegalStateException("Registry class, all methods are static");
    }

    /**
     * Register a new upgrade to the registry and return the upgrade
     *
     * @param supplier The supplier to create the upgrade
     * @param <T>      The type of the upgrade
     *
     * @return The upgrade
     */
    public static <T extends TotemUpgrade> T register(Supplier<T> supplier) {
        T upgrade = supplier.get();
        upgrade.reload(); // Load the upgrade
        upgrades.put(upgrade.name(), upgrade);
        return upgrade;
    }
    
    /**
     * Load all the upgrades from a {@link Totem} and initialize them to the totem
     * <p>
     * This method will initialize the upgrades to the totem
     *
     * @param totem The totem to load the upgrades from
     *
     * @return The upgrades loaded from the totem
     */
    public static Map<TotemUpgrade, Integer> from(Totem totem) {
        Map<TotemUpgrade, Integer> result = new HashMap<>();

        upgrades.values().forEach(upgrade -> {
            int level = totem.getProperty(upgrade.key(), upgrade.defaultLevel());
            upgrade.initialize(totem, level);
            result.put(upgrade, level);
        });

        return result;
    }

    /**
     * Reload all the upgrades in the registry
     */
    public static void reload() {
        upgrades.values().forEach(TotemUpgrade::reload);
    }

    /**
     * Get an upgrade from the registry
     *
     * @param name The name of the upgrade
     *
     * @return The upgrade
     */
    public static TotemUpgrade get(String name) {
        return upgrades.get(name);
    }

    /**
     * Get all the upgrades in the registry
     *
     * @return All the upgrades in the registry
     */
    public static Map<String, TotemUpgrade> all() {
        return upgrades;
    }

}
