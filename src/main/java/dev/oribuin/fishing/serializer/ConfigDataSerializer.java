package dev.oribuin.fishing.serializer;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigDataSerializer<T> {

    /**
     * Write a data type into the config file
     *
     * @param section The config section to write to
     * @param path    The path to the value to write
     * @param value   The value to write into the config
     */
    default void write(@NotNull ConfigurationSection section, @NotNull String path, @NotNull T value) {
        section.set(path, value);
    }

    /**
     * Read the data type from the config file
     *
     * @param section The section to read from
     * @param path    The path to the object to read
     *
     * @return The returning value
     */
    @Nullable
    T read(@NotNull ConfigurationSection section, @NotNull String path);

}