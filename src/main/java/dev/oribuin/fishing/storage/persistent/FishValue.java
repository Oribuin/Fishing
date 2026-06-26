package dev.oribuin.fishing.storage.persistent;

import dev.oribuin.fishing.FishingPlugin;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.mangostudios.gadgets.MangoGadgetsPlugin;

import java.util.function.Consumer;

public class FishValue<T> {

    private final FishDataType<?, T> key;
    private final @NotNull PersistentDataType<?, T> type;
    private final @Nullable T defaultValue;
    private @Nullable T value;
    private boolean dirty;

    /**
     * Store a container value into a persistent data container
     *
     * @param key          The key to change
     * @param defaultValue The default value for the node
     */
    public FishValue(FishDataType<?, T> key, @Nullable T defaultValue) {
        this.key = key;
        this.type = key.type();
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.dirty = false;
    }

    /**
     * Serialize a stored value into a persistent data container
     *
     * @param container The container to store the value into
     */
    public void serialize(PersistentDataContainer container) {
        T target = this.value == null ? this.defaultValue : this.value;
        this.dirty = false;

        if (target != null) container.set(this.key.key(), this.type, target);
        else container.remove(this.key.key());
    }

    /**
     * Serialize a stored value into a persistent data container
     *
     * @param container The container to store the value into
     * @param value     The value to store, default value otherwise
     */
    public void serialize(PersistentDataContainer container, T value) {
        this.value = value == null ? defaultValue : value;
        this.dirty = false;
        if (this.value != null) container.set(this.key.key(), this.type, value == null ? defaultValue : value);
        else container.remove(this.key.key());
    }

    /**
     * Deserialize a value from a persistent data container
     *
     * @param container The container to load from
     * @return The resulting value if available, default or null otherwise
     */
    public T deserialize(PersistentDataContainer container) {
        try {
            T result = container.get(this.key.key(), type);
            this.value = result != null ? result : this.defaultValue;
        } catch (IllegalArgumentException ex) {
            FishingPlugin.get().getLogger().info("Stored Value[" + this.key.key().asString() + "] had an issue loading value: " + ex.getMessage());
        }
        return this.value;
    }

    /**
     * Deserialize a value from a persistent data container and apply it to a value
     *
     * @param container The container to load from
     */
    public void deserialize(PersistentDataContainer container, Consumer<T> apply) {
        try {
            T value = container.get(this.key.key(), type);
            apply.accept(value != null ? value : this.defaultValue);
        } catch (IllegalArgumentException ex) {
            apply.accept(this.defaultValue);
        }
    }

    public FishDataType<?, T> getKey() {
        return this.key;
    }

    public @NotNull PersistentDataType<?, T> getType() {
        return type;
    }

    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    public @Nullable T getValue() {
        return value != null ? value : defaultValue;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public void resetDefault() {
        this.value = defaultValue;
        this.dirty = true;
    }
    
}