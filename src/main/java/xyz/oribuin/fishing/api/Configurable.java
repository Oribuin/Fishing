package xyz.oribuin.fishing.api;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Marks the config as a configurable class that will need to be loaded and unloaded from
 * their own config file
 * TODO: Allow for more dynamic config settings, not very important though
 */
public interface Configurable {

    /**
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    default void loadSettings(@NotNull CommentedFileConfiguration config) {
        // Empty function
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    default void saveSettings(@NotNull CommentedFileConfiguration config) {
        // Empty function
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @NotNull
    Path configPath();

    /**
     * The parent folder of the configuration file, this should be the starting point for every config path
     *
     * @return The parent folder, /plugins/fishing/
     */
    @NotNull
    default File parentFolder() {
        return FishingPlugin.get().getDataFolder();
    }

    /**
     * Load the configuration file for the configurable class
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void reload() {
        FishingPlugin plugin = FishingPlugin.get();
        File targetFile = new File(this.parentFolder(), this.configPath().toString());

        try {
            boolean addDefaults = false; // Should we add the defaults?

            // Create the file if it doesn't exist, set the defaults
            if (!targetFile.exists()) {
                targetFile.mkdir();
                targetFile.createNewFile();
                addDefaults = true;
            }

            // Load the configuration file
            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(targetFile);
            if (addDefaults) {
                this.saveSettings(config);
                config.save(targetFile);
            }

            this.loadSettings(config);
        } catch (IOException ex) {
            plugin.getLogger().warning("Configurable: There was an error loading the config file at path " + this.configPath());
        }
    }

}