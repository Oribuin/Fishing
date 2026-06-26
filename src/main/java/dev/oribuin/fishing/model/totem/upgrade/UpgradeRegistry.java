package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.totem.upgrade.impl.UpgradeTotemRadius;
import dev.oribuin.fishing.storage.persistent.SerializeType;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class UpgradeRegistry {
    
    private static final Map<String, SerializeType<?>> REGISTRY = new HashMap<>();

    public static final UpgradeTotemRadius RADIUS = register(UpgradeTotemRadius.class, "totem_radius", UpgradeTotemRadius::deserialize);

    /**
     * A private constructor to prevent instantiation of this class
     *
     * @throws IllegalStateException If the class is instantiated
     */
    private UpgradeRegistry() {
        throw new IllegalStateException("Registry class, all methods are static");
    }
    
    public static void init() {
        // java does not load a static class until it's referenced at least once
    }

    /**
     * Register a {@link NodeType} into the plugin to be used to load a {@link Node}
     *
     * @param identifier The identifier of the node
     * @param <T>        The node that is associated with this type
     * @return The resulting node if available
     */
    public static <T extends TotemUpgrade> SerializeType<T> register(
            Class<T> nodeClass,
            String identifier,
            BiFunction<Block, PersistentDataContainer, T> deserializer
    ) {
        return register(
                identifier, new SerializeType<>(nodeClass,
                        identifier,
                        (data, node) -> node.serialize(data),
                        deserializer
                ));
    }

    /**
     * Register a {@link SerializeType} into the plugin to be used to load
     *
     * @param identifier The identifier of the node
     * @param type       The node serialize/deserialize type
     * @param <T>        The node that is associated with this type
     * @return The resulting node if available
     */
    public static <T extends TotemUpgrade> SerializeType<T> register(String identifier, SerializeType<T> type) {
        REGISTRY.put(identifier.toLowerCase(), type);

        if (type.typeClass().isAnnotationPresent(ConfigSerializable.class)) {
            TotemManager.getLoader().loadConfig(type.typeClass());
        }

        return type;
    }


}
