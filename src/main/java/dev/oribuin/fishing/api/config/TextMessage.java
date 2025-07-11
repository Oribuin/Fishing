package dev.oribuin.fishing.api.config;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.SettingField;
import dev.rosewood.rosegarden.config.SettingSerializer;
import dev.rosewood.rosegarden.config.SettingSerializers;
import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TextMessage {

    public static final SettingSerializer<TextMessage> SERIALIZER = SettingSerializers.ofRecord(TextMessage.class, instance -> instance.group(
            SettingField.of("message", SettingSerializers.STRING, TextMessage::message),
            SettingField.of("actionbar", SettingSerializers.STRING, TextMessage::actionbar),
            SettingField.of("sound", SettingSerializers.STRING, TextMessage::sound),
            SettingField.of("source", SettingSerializers.STRING, TextMessage::sourceName),
            SettingField.of("title_header", SettingSerializers.STRING, TextMessage::titleHeader),
            SettingField.of("title_subtitle", SettingSerializers.STRING, TextMessage::titleSubtitle),
            SettingField.ofOptional("placeholders", SettingSerializers.BOOLEAN, TextMessage::placeholderapi, () -> false)
    ).apply(instance, TextMessage::new));

    private @Nullable String message;
    private @Nullable String actionbar;
    private @Nullable String sound;
    private @Nullable String titleHeader;
    private @Nullable String titleSubtitle;
    private @Nullable Sound.Source source;
    private Boolean placeholderapi;

    /**
     * Create a new config defined message for the plugin
     *
     * @param message  The message being defined
     * @param comments The comments for the message
     *
     * @return The resulting config option
     */
    public static Message ofConfig(String message, List<String> comments) {
        return new Message(new TextMessage(message), comments);
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param message  The message being defined
     * @param comments The comments for the message
     *
     * @return The resulting config option
     */
    public static Message ofPapiConfig(String message, List<String> comments) {
        return new Message(new TextMessage(message).placeholderapi(true), comments);
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param message The message being defined
     *
     * @return The resulting config option
     */
    public static Message ofConfig(String message) {
        return new Message(new TextMessage(message));
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param message The message being defined
     *
     * @return The resulting config option
     */
    public static Message ofPapiConfig(String message) {
        return new Message(new TextMessage(message).placeholderapi(true));
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param message  The message being defined
     * @param comments The comments for the message
     *
     * @return The resulting config option
     */
    public static Message ofConfig(TextMessage message, String... comments) {
        return new Message(message, comments);
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param message  The message being defined
     * @param comments The comments for the message
     *
     * @return The resulting config option
     */
    public static Message ofPapiConfig(TextMessage message, String... comments) {
        return new Message(message.placeholderapi(true), comments);
    }

    /**
     * Create a new message that can be sent to an audience with specified params
     *
     * @param message        The content that is sent to the player's chat
     * @param actionbar      The action bar message
     * @param sound          The sound that is sent
     * @param titleHeader    The title header
     * @param titleSubtitle  The title subtitle
     * @param placeholderapi Whether placeholderapi should be used when sending the message
     */
    public TextMessage(
            @Nullable String message,
            @Nullable String actionbar,
            @Nullable String sound,
            @Nullable String titleHeader,
            @Nullable String titleSubtitle,
            @Nullable String source,
            Boolean placeholderapi
    ) {
        this.message = message;
        this.actionbar = actionbar;
        this.sound = sound;
        this.titleHeader = titleHeader;
        this.titleSubtitle = titleSubtitle;
        this.source = FishUtils.getEnum(Sound.Source.class, source, null);
        this.placeholderapi = placeholderapi;
    }

    public TextMessage(@Nullable String message) {
        this(message, null, null, null, null, null, null);
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
        this.send(audience, null, placeholders);
    }

    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, Object... placeholders) {
        StringPlaceholders.Builder builder = StringPlaceholders.builder();
        for (int i = 0; i < placeholders.length; i += 2) {
            if (placeholders[i] instanceof String placeholder) {
                Object value = placeholders[i + 1];
                builder.add(placeholder, value);
            }
        }

        this.send(audience, target, builder.build());
    }


    /**
     * Send a message to players with
     *
     * @param audience     The audience to send the message to
     * @param target       The placeholderapi target
     * @param placeholders The plugin defined placeholders
     */
    public void send(Audience audience, Player target, StringPlaceholders placeholders) {
        if (this.message != null && !this.message.isEmpty()) {
            audience.sendMessage(this.parse(this.message, target, placeholders));
        }

        if (this.titleHeader != null) {
            Component header = this.parse(this.titleHeader, target, placeholders);
            Component subtitle = this.parse(this.titleSubtitle, target, placeholders);
            audience.showTitle(Title.title(header, subtitle));
        }

        if (this.actionbar != null) {
            audience.sendActionBar(this.parse(this.actionbar, target, placeholders));
        }

        if (sound != null && source != null) {
            Sound kyoriSound = Sound.sound(Key.key(this.sound), source, 1f, 1f);
            FishingPlugin.get().getScheduler().runTask(() -> audience.playSound(kyoriSound));
        }
    }

    /**
     * Create a new config defined message for the plugin
     *
     * @param comments The comments for the message
     *
     * @return The resulting config option
     */
    public Message asConfig(String... comments) {
        return new Message(this, comments);
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
        if (message == null) return Component.empty();
        if (placeholders == null) placeholders = StringPlaceholders.empty();

        boolean usePapi = this.placeholderapi != null ? this.placeholderapi : false;

        return usePapi
                ? FishUtils.kyorify(placeholders.apply(PlaceholderAPIHook.applyPlaceholders(target, message)))
                : FishUtils.kyorify(message, placeholders);
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
        return this.parse(message, target, StringPlaceholders.empty());
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
        return this.parse(message, null, placeholders);
    }

    /**
     * Parse a message through the plugin
     *
     * @param message The message being sent
     *
     * @return The formatted message
     */
    public Component parse(String message) {
        return this.parse(message, null, StringPlaceholders.empty());
    }

    public @Nullable String message() {
        return message;
    }

    public TextMessage message(@Nullable String message) {
        this.message = message;
        return this;
    }

    public @Nullable String actionbar() {
        return actionbar;
    }

    public TextMessage actionbar(@Nullable String actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    public @Nullable String sound() {
        return sound;
    }

    public TextMessage sound(@Nullable String sound) {
        this.sound = sound;
        return this;
    }

    public Sound.Source source() {
        return source;
    }

    public String sourceName() {
        return source != null ? source.name() : null;
    }

    public TextMessage source(Sound.Source source) {
        this.source = source;
        return this;
    }

    public @Nullable String titleHeader() {
        return titleHeader;
    }

    public TextMessage titleHeader(@Nullable String titleHeader) {
        this.titleHeader = titleHeader;
        return this;
    }

    public @Nullable String titleSubtitle() {
        return titleSubtitle;
    }

    public TextMessage titleSubtitle(@Nullable String titleSubtitle) {
        this.titleSubtitle = titleSubtitle;
        return this;
    }

    public Boolean placeholderapi() {
        return placeholderapi;
    }

    public TextMessage placeholderapi(boolean placeholderapi) {
        this.placeholderapi = placeholderapi;
        return this;
    }
}
