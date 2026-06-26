package dev.oribuin.fishing.api;

import dev.oribuin.fishing.storage.persistent.FishDataType;
import dev.oribuin.fishing.storage.persistent.FishValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that can have properties saved to it using {@link PersistentDataContainer}
 */
public abstract class Propertied {

    protected Map<FishDataType<?, ?>, FishValue<?>> nodeValues;

    public Propertied() {
        this.nodeValues = new HashMap<>();
    }

    /**
     * Store a {@link FishDataType} into a {@link PersistentDataContainer}
     *
     * @param container The container to store the serializer in
     */
    public <T extends PersistentDataContainer> void serialize(T container) {
        // region Serialize the data into the PDC Container
        nodeValues.values().forEach(nodeValue -> {
            if (nodeValue.isDirty()) nodeValue.serialize(container);
        });
        // endregion
    }

    /**
     * Store a {@link FishDataType} into a {@link PersistentDataContainer}
     *
     * @param container The container to store the serializer in
     */
    public <T extends PersistentDataContainer> void loadFrom(T container) {
        if (container == null) return;

        nodeValues.values().forEach(nodeValue -> nodeValue.deserialize(container));
    }

    /**
     * Get a value from a namespaced key
     *
     * @param gadgetType The key to load the value from
     * @param <T>        The node value type
     *
     * @return The resulting value if available
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getValue(PersistentDataContainer container, FishDataType<?, T> gadgetType) {
        FishValue<T> nodeValue = (FishValue<T>) this.nodeValues.get(gadgetType);

        // Check if the block data exists, if not use default node value
        T storedValue = nodeValue != null ? nodeValue.getValue() : null;
        if (container == null) return storedValue;

        // If the value from the block data does not match what's stored, update the stored value
        T blockDataValue = container.get(gadgetType.key(), gadgetType);
        if (blockDataValue != null && !blockDataValue.equals(storedValue)) {
            this.setValue(gadgetType, blockDataValue);
        }

        // Prefer using the block data value, otherwise use stored
        return blockDataValue != null ? blockDataValue : storedValue;
    }

    /**
     * Get a value from a namespaced gadgetType
     *
     * @param gadgetType The gadgetType to load the value from
     * @param <T>        The node value type
     *
     * @return The resulting value if available
     */
    @NotNull
    public <T> T getValue(PersistentDataContainer container, FishDataType<?, T> gadgetType, @NotNull T other) {
        T result = this.getValue(container, gadgetType);
        return result == null ? other : result;
    }

    /**
     * Set a value in the plugin to a specified value
     *
     * @param gadgetType The gadgetType to set
     * @param value      The value being modified
     * @param <T>        The type of value being set
     */
    @SuppressWarnings("unchecked")
    public <T> T setValue(FishDataType<?, T> gadgetType, @Nullable T value) {
        FishValue<?> nodeValue = this.nodeValues.get(gadgetType);
        if (nodeValue == null) return null;

        FishValue<T> result = (FishValue<T>) nodeValue;
        result.setValue(value);
        this.nodeValues.put(gadgetType, result);
        return value;
    }

    /**
     * Register a stored value into the plugin to load/unload to or from the data container
     *
     * @param gadgetType   The utilized  gadgetType
     * @param defaultValue The default value for it
     * @param <T>          he stored data type
     *
     * @return The resulting stored value
     */
    public <T> FishValue<T> registerType(FishDataType<?, T> gadgetType, @Nullable T defaultValue) {
        FishValue<T> result = new FishValue<>(gadgetType, defaultValue);
        this.nodeValues.put(gadgetType, result);
        return result;
    }

}
