package dev.oribuin.fishing.api.config;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Message extends ConfigOptionType<TextMessage> {

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@Nullable String path, @NotNull TextMessage defaultValue, @NotNull List<String> comments) {
        super(path, TextMessage.SERIALIZER, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@Nullable String path, @NotNull TextMessage defaultValue, @NotNull String... comments) {
        super(path, TextMessage.SERIALIZER, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value
     *
     * @param path         The path to the config option
     * @param defaultValue The default values to use
     */
    public Message(@Nullable String path, @NotNull TextMessage defaultValue) {
        super(path, TextMessage.SERIALIZER, defaultValue);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@NotNull TextMessage defaultValue, @NotNull List<String> comments) {
        super(TextMessage.SERIALIZER, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     * @param comments     The comments if available
     */
    public Message(@NotNull TextMessage defaultValue, @NotNull String... comments) {
        super(TextMessage.SERIALIZER, defaultValue, comments);
    }

    /**
     * Create a new config option with a specified serializer, default value and any available comments
     *
     * @param defaultValue The default values to use
     */
    public Message(@NotNull TextMessage defaultValue) {
        super(TextMessage.SERIALIZER, defaultValue);
    }

    /**
     * Send a message to player
     *
     * @param audience The audience to send the message to
     */
    public void send(Audience audience) {
        this.send(audience, null, StringPlaceholders.empty());
    }

    /**
     * Send a message to players with
     *
     * @param audience The audience to send the message to
     * @param target   The placeholderapi target
     */
    public void send(Audience audience, Player target) {
        this.send(audience, target, StringPlaceholders.empty());
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, StringPlaceholders placeholders) {
        this.send(audience, null, placeholders);
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Object... placeholders) {
        (this.value != null ? this.value : this.defaultValue).send(audience, placeholders);
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, Object... placeholders) {
        (this.value != null ? this.value : this.defaultValue).send(audience, target, placeholders);
    }


    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, StringPlaceholders placeholders) {
        (this.value != null ? this.value : this.defaultValue).send(audience, target, placeholders);
    }

    /**
     * Parse a message through the plugin and automatically decide whether placeholderapi should be involved
     *
     * @param message      The message being sent
     * @param target       The target of the placeholderapi values if available
     * @param placeholders Any possible Placeholders
     *
     * @return The formatted message
     */
    public Component parse(String message, Player target, StringPlaceholders placeholders) {
        return (this.value != null ? this.value : this.defaultValue).parse(message, target, placeholders);
    }

    /**
     * Parse a message through the plugin and automatically decide whether placeholderapi should be involved
     *
     * @param message The message being sent
     * @param target  The target of the placeholderapi values if available
     *
     * @return The formatted message
     */
    public Component parse(String message, Player target) {
        return (this.value != null ? this.value : this.defaultValue).parse(message, target);
    }

    /**
     * Parse a message through the plugin
     *
     * @param message      The message being sent
     * @param placeholders Any possible Placeholders
     *
     * @return The formatted message
     */
    public Component parse(String message, StringPlaceholders placeholders) {
        return (this.value != null ? this.value : this.defaultValue).parse(message, placeholders);
    }

    /**
     * Parse a message through the plugin
     *
     * @param message The message being sent
     *
     * @return The formatted message
     */
    public Component parse(String message) {
        return (this.value != null ? this.value : this.defaultValue).parse(message);
    }
}
