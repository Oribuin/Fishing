package dev.oribuin.fishing.storage.util;

import dev.oribuin.fishing.FishingPlugin;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all the {@link NamespacedKey} used in the plugin, for easy access.
 * <p>
 * This is quite unnecessary, but it's a good practice to keep all the keys in one place.
 * Especially when another plugin wants to establish their own keys
 */
public class KeyRegistry {
    
    private static final Map<String, NamespacedKey> keys = new HashMap<>();
    
    public static NamespacedKey FISH_TYPE = register("fish_type");
    public static final NamespacedKey AUGMENT_TYPE = register("augment_type");
    public static final NamespacedKey TOTEM_OWNER = register("totem_owner");
    public static final NamespacedKey TOTEM_OWNER_NAME = register("totem_owner_name");
    public static final NamespacedKey TOTEM_ACTIVE = register("totem_active");
    public static final NamespacedKey TOTEM_LAST_ACTIVE = register("totem_last_active");

    /**
     * Get a {@link NamespacedKey} from the registry
     *
     * @param key The key to get
     *
     * @return The {@link NamespacedKey} from the registry
     */
    public static NamespacedKey register(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(FishingPlugin.get(), key);
        keys.put(key, namespacedKey);
        return namespacedKey;
    }

    /**
     * Get a {@link NamespacedKey} from the registry
     *
     * @param key The key to get
     *
     * @return The {@link NamespacedKey} from the registry
     */
    public static NamespacedKey get(String key) {
        return keys.get(key);
    }

    /**
     * Get all the keys in the registry
     *
     * @return All the keys in the registry
     */
    public static Map<String, NamespacedKey> getKeys() {
        return keys;
    }

}

