package dev.oribuin.fishing.config;

import dev.oribuin.fishing.FishingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigLoader {

    private final Path directory;
    private final Map<Class<?>, ConfigHandler<?>> configs;

    /**
     * Initialise a new config loader
     *
     * @param directory the directory to load
     */
    public ConfigLoader(Path directory) {
        this.directory = directory;
        this.configs = new ConcurrentHashMap<>();

        if (!this.directory.toFile().exists()) {
            this.directory.toFile().mkdirs();
        }
    }

    /**
     * Initialise a new config loader with the
     */
    public ConfigLoader() {
        this(Paths.get("plugins", FishingPlugin.get().getName()));
    }

    /**
     * Get the config from the config handler
     *
     * @param config The config to load
     * @param <T>    The
     *
     * @return The initialised config
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> config) {
        return (T) this.configs.get(config).getConfig();
    }

    /**
     * Initialize a config class into the plugin
     *
     * @param config The config to load
     * @param parent The parent directory for it to be placed in
     * @param name   The name of the file
     */
    public void loadConfig(@NotNull Class<?> config, @Nullable Path parent, @Nullable String name) {

        Path path = parent != null ? this.directory.resolve(parent) : this.directory;
        String[] split = (name != null ? name : config.getName()).toLowerCase().split("\\.");

        // Make all parent directories 
        if (!path.toFile().exists()) path.toFile().mkdirs();

        configs.put(config, new ConfigHandler<>(config, path, split[0] + ".yml"));
    }

    /**
     * Initialize a config class into the plugin
     *
     * @param config The config to load
     * @param name   The name of the file
     */
    public void loadConfig(Class<?> config, String name) {
        this.loadConfig(config, null, name);
    }

    /**
     * Initialize a config class into the plugin
     *
     * @param config The config to load
     */
    public void loadConfig(Class<?> config) {
        this.loadConfig(config, null, null);
    }

    /**
     * Write and save a config file into a directory
     *
     * @param config The config file to save
     */
    public void saveConfig(Class<?> config) {
        ConfigHandler<?> handler = this.configs.get(config);
        handler.save();
    }

    /**
     * Close the config loader
     */
    public void close() {
        for (ConfigHandler<?> handler : this.configs.values()) {
            handler.unload();
        }
    }

}
