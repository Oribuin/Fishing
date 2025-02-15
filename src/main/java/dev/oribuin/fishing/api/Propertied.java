package dev.oribuin.fishing.api;

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
     * Register all the default properties to the class so they can be loaded and saved
     *
     * @param container The container to register the properties to
     *
     * @see #saveProperties(PersistentDataContainer)
     */
    public abstract void defineProperties(PersistentDataContainer container);

    /*
     * Register a new {@link DataType} to the class so it can be loaded and saved
     *
     * @param type  The type to register
     * @param key   The key to register the type with
     * @param value The value to register the type with
     * @param <T>   The type of the value
     */
    public final <T> T registerProperty(PersistentDataType<?, T> type, NamespacedKey key, T value) {
        this.properties.put(key, type);
        this.container.put(key, value);
        return value;
    }

    /**
     * Apply a property to the class
     *
     * @param key   The key of the property
     * @param value The value of the property
     * @param <T>   The type of the property
     */
    public final <T> void setProperty(NamespacedKey key, T value) {
        if (!this.properties.containsKey(key)) {
            throw new IllegalArgumentException("Property with key " + key + " is not registered");
        }

        this.container.put(key, value);
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
