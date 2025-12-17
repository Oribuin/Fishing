package dev.oribuin.fishing.config;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.serializer.ComponentSerializer;
import dev.oribuin.fishing.config.serializer.DurationSerializer;
import dev.oribuin.fishing.config.serializer.EnchantSerializer;
import dev.oribuin.fishing.config.serializer.SoundSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class ConfigHandler<T> {

    private final File configFile;
    private final Class<T> configClass;
    private ConfigurationReference<@NotNull CommentedConfigurationNode> base;
    private ValueReference<T, @NotNull CommentedConfigurationNode> config;

    public ConfigHandler(Class<T> configClass, String name) {
        this(configClass, Paths.get("plugins", FishingPlugin.get().getName()), name);
    }

    public ConfigHandler(Class<T> configClass, Path parent, String name) {
        this(configClass, parent.resolve(name).toFile());
    }

    /**
     * Create a new config loader for the plugin
     *
     * @param configFile  The file the config will be saved in
     * @param configClass The config class
     */
    public ConfigHandler(Class<T> configClass, File configFile) {
        this.configClass = configClass;
        this.configFile = configFile;

        try {
            this.base = ConfigurationReference.fixed(YamlConfigurationLoader.builder()
                    .defaultOptions(options -> options
                            .serializers(builder -> builder
                                    .register(Component.class, ComponentSerializer.getInstance())
                                    .register(Sound.class, SoundSerializer.getInstance())
                                    .register(Enchantment.class, EnchantSerializer.getInstance())
                                    .register(Duration.class, DurationSerializer.getInstance())
                                    .build()
                            ).shouldCopyDefaults(true)
                    )
                    .nodeStyle(NodeStyle.BLOCK)
                    .indent(2)
                    .path(this.configFile.toPath())
                    .build());

            this.config = this.base.referenceTo(configClass);
            this.base.save();
        } catch (IOException ex) {
            FishingPlugin.get().getLogger().severe("Failed to load configuration [" + this.configFile.getPath() + "] due to: " + ex.getMessage());
        }
    }

    /**
     * Save the configuration file
     */
    public void save() {
        try {
            this.base.node().set(this.configClass, this.configClass.cast(this.config.get()));
            this.base.loader().save(this.base.node());
        } catch (ConfigurateException ex) {
            FishingPlugin.get().getLogger().severe("Failed to save configuration [" + this.configFile.getPath() + "] due to: " + ex.getMessage());
        }
    }

    /**
     * Unload the config file
     */
    public void unload() {
        this.base.close();
    }

    public T getConfig() {
        return this.config.get();
    }
}
