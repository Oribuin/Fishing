package xyz.oribuin.fishing.api.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.FishingPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Marks the config as a configurable class that will need to be loaded and unloaded from
 * their own config file
 * TODO: Allow for more dynamic config settings, not very important though
 */
public interface Configurable {

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    default void loadSettings(@NotNull CommentedConfigurationSection config) {
        // Empty function
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    default void saveSettings(@NotNull CommentedConfigurationSection config) {
        // Empty function
    }

    /**
     * The comments to be generated at the top of the file when it is created
     *
     * @return The comments
     */
    default List<String> comments() {
        return new ArrayList<>();
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
                this.createFile(targetFile);
                addDefaults = true;

                Bukkit.getLogger().info("[Fishing]: Created a new file at path " + this.configPath());
            }

            // Load the configuration file
            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(targetFile);
            if (addDefaults) {
                this.saveSettings(config);
                config.save(targetFile);
            }

            this.loadSettings(config);
        } catch (Exception ex) {
            plugin.getLogger().warning("Configurable: There was an error loading the config file at path " + this.configPath() + ": " + ex.getMessage());
        }
    }

    /**
     * Create a file in the designated path
     *
     * @param target The file to create
     *
     * @throws IOException If the file could not be created
     */
    private void createFile(File target) throws IOException {

        // Add all the parent folders if they don't exist
        for (File parent = target.getParentFile(); parent != null; parent = parent.getParentFile()) {
            if (!parent.exists()) {
                parent.mkdirs();
            }
        }

        // Create the file
        target.createNewFile();
    }

}
