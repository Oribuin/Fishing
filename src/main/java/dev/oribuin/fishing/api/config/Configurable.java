package dev.oribuin.fishing.api.config;

import dev.oribuin.fishing.FishingPlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public interface Configurable {

    /**
     * Load all the config options inside the class dynamically into the plugin
     *
     * @param file The file that the configs are being loaded into.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void reload(File file, CommentedFileConfiguration config) {
        try {
            if (!file.exists()) file.createNewFile();

            // Load the config
            for (ConfigOptionType<?> option : this.options()) {
                if (option.path() == null) continue; // Ignore anything with no path 

                // If the config contains the path, cache the value
                if (config.contains(option.path())) {
                    option.read(config);
                    continue;
                }

                // Set the default value, this will also write any available 
                option.write(config);
            }

            config.save(file);
        } catch (Exception ex) {
            FishingPlugin.get().getLogger().severe("Failed to create config file for class [" + this.getClass().getSimpleName() + "]: " + ex.getMessage());
        }
    }

    /**
     * Required list of all the config options available in the class
     *
     * @return The provided config options
     */
    List<ConfigOptionType<?>> options();

    /**
     * Register a config option into the list of all config options
     *
     * @param option The config option to load into the plugin
     */
    default void register(ConfigOptionType<?> option) {
        this.register(option, option.path());
    }

    /**
     * Register a config option into the list of all config options
     *
     * @param option The config option to load into the plugin
     */
    default void register(ConfigOptionType<?> option, String path) {
        if (path == null) return;

        option.path(path);
        this.options().add(option);
    }

    /**
     * Register a class of config options into the plugin
     */
    default void registerClass() {
        if (this.options() == null) {
            FishingPlugin.get().getLogger().info("Option list is not provided in class [" + this.getClass().getSimpleName() + "]");
            return;
        }

        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (!field.canAccess(null)) continue;

                Object type = field.get(null);
                String path = field.getName().toLowerCase().replace("_", ".");

                if (type instanceof ConfigOptionType<?> option) {
                    this.register(option, path);
                }
            }
        } catch (Exception ex) {
            FishingPlugin.get().getLogger().severe("Failed to register ConfigOption in class [" + this.getClass().getSimpleName() + "] - " + ex);
        }
    }

}