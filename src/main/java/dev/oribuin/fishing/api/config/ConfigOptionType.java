package dev.oribuin.fishing.api.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.SettingSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigOptionType<T> {

    protected final @NotNull SettingSerializer<T> serializer;
    protected final @NotNull T defaultValue;
    protected final List<String> comments;
    protected @Nullable String path;
    protected @Nullable T value;

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigOptionType(
            @Nullable String path,
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue,
            @NotNull List<String> comments
    ) {
        this.path = path;
        this.serializer = serializer;
        this.defaultValue = defaultValue;
        this.comments = comments;
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigOptionType(
            @Nullable String path,
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue,
            @NotNull String... comments
    ) {
        this.path = path;
        this.serializer = serializer;
        this.defaultValue = defaultValue;
        this.comments = List.of(comments);
    }

    /**
     * Create a new config option with a specified serializer, default value
     *
     * @param path         The path to the config option
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     */
    public ConfigOptionType(
            @Nullable String path,
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue
    ) {
        this(path, serializer, defaultValue, new ArrayList<>());
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigOptionType(
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue,
            @NotNull List<String> comments
    ) {
        this(null, serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public ConfigOptionType(
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue,
            @NotNull String... comments
    ) {
        this(null, serializer, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param serializer   The config serializer
     * @param defaultValue The default values to use
     */
    public ConfigOptionType(
            @NotNull SettingSerializer<T> serializer,
            @NotNull T defaultValue
    ) {
        this(null, serializer, defaultValue, new ArrayList<>());
    }

    /**
     * Load the config option from a config
     *
     * @param section The configuration section to load from
     */
    public void read(CommentedConfigurationSection section) {
        if (this.path == null) return;

        T value = this.serializer.read(section, this.path);
        this.value = value != null ? value : this.defaultValue;
    }

    /**
     * Write a config option to the config path
     *
     * @param section The section to write it to
     */
    public void write(CommentedConfigurationSection section) {
        if (this.path == null) return;

        String[] comments = this.comments.toArray(new String[0]);
        this.serializer.write(section, this.path, this.defaultValue, comments);
    }

    public @NotNull SettingSerializer<T> serializer() {
        return this.serializer;
    }

    public @NotNull T defaultValue() {
        return this.defaultValue;
    }

    public List<String> comments() {
        return this.comments;
    }

    public @Nullable String path() {
        return this.path;
    }

    public void path(@Nullable String path) {
        this.path = path;
    }

    public @NotNull T value() {
        return this.value != null ? this.value : this.defaultValue;
    }

    public void value(@Nullable T value) {
        this.value = value;
    }
}
