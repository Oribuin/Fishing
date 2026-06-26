package dev.oribuin.fishing.storage.persistent;

import dev.oribuin.fishing.FishingPlugin;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record FishDataType<P, C>(NamespacedKey key, @NotNull PersistentDataType<P, C> type) implements PersistentDataType<P, C>, Namespaced {

    /**
     * Create a new persistent data type
     *
     * @param key  The key for the data type
     * @param type The
     */
    public FishDataType(String key, @NotNull PersistentDataType<P, C> type) {
        this(NamespacedKey.fromString(key.toLowerCase(), FishingPlugin.get()), type);
    }

    /**
     * Returns the primitive data type of this tag.
     *
     * @return the class
     */
    @Override
    public @NotNull Class<P> getPrimitiveType() {
        return this.type.getPrimitiveType();
    }

    /**
     * Returns the complex object type the primitive value resembles.
     *
     * @return the class type
     */
    @Override
    public @NotNull Class<C> getComplexType() {
        return this.type.getComplexType();
    }

    /**
     * Returns the primitive data that resembles the complex object passed to
     * this method.
     *
     * @param complex the complex object instance
     * @param context the context this operation is running in
     * @return the primitive value
     */
    @Override
    public @NonNull P toPrimitive(@NonNull C complex, @NotNull PersistentDataAdapterContext context) {
        return this.type.toPrimitive(complex, context);
    }

    /**
     * Creates a complex object based of the passed primitive value
     *
     * @param primitive the primitive value
     * @param context   the context this operation is running in
     * @return the complex object instance
     */
    @Override
    public @NonNull C fromPrimitive(@NonNull P primitive, @NotNull PersistentDataAdapterContext context) {
        return this.type.fromPrimitive(primitive, context);
    }

    @KeyPattern.Namespace
    @Override
    public @NotNull String namespace() {
        return this.key.namespace();
    }

}
