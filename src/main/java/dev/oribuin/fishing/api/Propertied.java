package dev.oribuin.fishing.api;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that can have properties saved to it using {@link PersistentDataContainer}
 */
@SuppressWarnings("unchecked")
public abstract class Propertied {

    private final Map<NamespacedKey, PersistentDataType<?, ?>> properties = new HashMap<>();
    private final Map<NamespacedKey, Object> container = new HashMap<>();

    /**
     * Save the properties to a {@link PersistentDataContainer}
     *
     * @param container The container to save the properties to
     */
    public void saveProperties(PersistentDataContainer container) {
        this.container.forEach((key, value) -> {
            PersistentDataType<Object, Object> type = (PersistentDataType<Object, Object>) this.properties.get(key);
            if (type != null) container.set(key, type, value);
        });
    }

    /**
     * Load the properties from a {@link PersistentDataContainer}
     *
     * @param container The container to load the properties from
     */
    public void loadProperties(PersistentDataContainer container) {
        this.properties.forEach((key, type) -> {
            Object value = container.get(key, type);
            if (value != null) this.container.put(key, value);
        });
    }

    /*
     * Register a new {@link DataType} to the class so it can be loaded and saved
     *
     * @param type  The type to register
     * @param key   The key to register the type with
     * @param value The value to register the type with
     * @param <T>   The type of the value
     */
    public final <T> T applyProperty(PersistentDataType<?, T> type, NamespacedKey key, T value) {
        this.properties.put(key, type);
        
        if (value != null) {
            this.container.put(key, value);
        }
        return value;
    }

    /**
     * Register a new {@link DataType} to the class so it can be loaded and saved
     *
     * @param type The type to register
     * @param key  The key to register the type with
     * @param <T>  The type of the value
     */
    public final <T> void applyProperty(PersistentDataType<?, T> type, NamespacedKey key) {
        this.applyProperty(type, key, null);
    }

    /**
     * Set a property to the class with a key, this can be a temporary property that is not saved to the {@link PersistentDataContainer}
     *
     * @param key   The key of the property
     * @param value The value of the property
     * @param <T>   The type of the property
     *
     * @return The value of the property
     */
    public final <T> T setProperty(NamespacedKey key, T value) {
        this.container.put(key, value);
        return value;
    }

    /**
     * Get a property from the class
     *
     * @param key The key of the property
     * @param <T> The type of the property
     *
     * @return The property
     */
    public final <T> T getProperty(NamespacedKey key) {
        return (T) this.container.get(key);
    }

    /**
     * Get a property from the class
     *
     * @param key The key of the property
     * @param def The default value of the property
     * @param <T> The type of the property
     *
     * @return The property
     */
    public final <T> T getProperty(NamespacedKey key, T def) {
        return (T) this.container.getOrDefault(key, def);
    }

    /**
     * Check if the class has a property
     *
     * @param key The key of the property
     * @param <T> The type of the property
     *
     * @return If the class has the property
     */
    public final <T> boolean hasProperty(NamespacedKey key) {
        return this.container.containsKey(key);
    }

    /**
     * Remove a property from the class
     *
     * @param key The key of the property
     */
    public final void removeProperty(NamespacedKey key) {
        this.container.remove(key);
        this.properties.remove(key);
    }

    /**
     * Clear all the properties from the class
     */
    public final void clearProperties() {
        this.properties.clear();
        this.container.clear();
    }

}
