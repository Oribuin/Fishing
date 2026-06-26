package dev.oribuin.fishing.storage.persistent;

import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Create a new {@link SerializeType} for the plugin to load
 *
 * @param identifier   The identifier for the type
 * @param serializer   The serializer method for the type
 * @param deserializer The deserializer method for the type
 * @param <T>          The node that is being serialized
 */
public record SerializeType<T>(
        Class<T> typeClass,
        String identifier,
        BiConsumer<? extends PersistentDataContainer, T> serializer,
        Function<PersistentDataContainer, T> deserializer
) {
    
    public interface Serializer {

        /**
         * Get the identifier of a node type
         *
         * @return The node type identifier
         */
        Supplier<String> getIdentifier();

        /**
         * Store a {@link Serializer} into a {@link PersistentDataContainer}
         *
         * @param container The container to store the serializer in
         */
        <T extends PersistentDataContainer> void serialize(T container);
    }

}
