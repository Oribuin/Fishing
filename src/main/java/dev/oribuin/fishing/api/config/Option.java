package dev.oribuin.fishing.api.config;

import dev.rosewood.rosegarden.config.SettingSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Option<T> extends ConfigOptionType<T> {

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Option(@Nullable String path, @NotNull SettingSerializer<T> serializer, @NotNull T defaultValue, @NotNull List<String> comments) {
        super(path, serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Option(@Nullable String path, @NotNull SettingSerializer<T> serializer, @NotNull T defaultValue, @NotNull String... comments) {
        super(path, serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     */
    public Option(@Nullable String path, @NotNull SettingSerializer<T> serializer, @NotNull T defaultValue) {
        super(path, serializer, defaultValue);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Option(@NotNull SettingSerializer<T> serializer, @NotNull T defaultValue, @NotNull List<String> comments) {
        super(serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Option(@NotNull SettingSerializer<T> serializer, @NotNull T defaultValue, @NotNull String... comments) {
        super(serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     */
    public Option(@NotNull SettingSerializer<T> serializer, @NotNull T defaultValue) {
        super(serializer, defaultValue);
    }

    @Override
    public String toString() {
        return (this.value != null ? this.value : this.defaultValue).toString();
    }
}
