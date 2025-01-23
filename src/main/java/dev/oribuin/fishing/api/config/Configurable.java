package dev.oribuin.fishing.api.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.oribuin.fishing.FishingPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Marks the config as a configurable class that will need to be loaded and unloaded from
 * their own config file
 * <p>
 * TODO: Allow for more dynamic config settings, this will require a more advanced system with predefined objects.
 */
public interface Configurable {

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    default void loadSettings(@NotNull CommentedConfigurationSection config) {}

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    default void saveSettings(@NotNull CommentedConfigurationSection config) {}

    /**
     * A predefined list of comments that will be generated at the top of the file
     * when the configuration loaded using {@link #reload()} method.
     * <p>
     * This method is optional and will not generate any comments if the load method is overwritten and are not generated
     *
     * @return The list of comments to be generated
     */
    default List<String> comments() {
        return new ArrayList<>();
    }

    /**
     * The file path to a {@link CommentedFileConfiguration} file, This path by default will be relative {@link #parentFolder()}.
     * <p>
     * This by default is only used in the {@link #reload()} method to load the configuration file
     * <p>
     * This an optional method and should only be used if the Configurable class is its own file (E.g. {@link dev.oribuin.fishing.augment.Augment} class)
     *
     * @return The path to the configuration file
     */
    @Nullable
    default Path configPath() {
        return null;
    }

    /**
     * The parent folder of a configuration file, by default this will be the plugin's main directory, this will generate comments and the default starting options
     * <p>
     * This is only used in the {@link #reload()} method to load the configuration file and is the base directory for the {@link #configPath()} method
     * <p>
     * I would not recommend overriding this method unless since all configuration files should be in the plugin's main directory
     *
     * @return A @{@link File} representing the parent folder of the configuration file
     */
    @NotNull
    default File parentFolder() {
        return FishingPlugin.get().getDataFolder();
    }

    /**
     * Creates and loads a configuration file from the {@link #configPath()} method and loads the settings
     * <p>
     * If the file does not exist, it will create the file and save the settings
     * <p>
     * If the file does exist, it will load the settings from the file, however, {@link #saveSettings(CommentedConfigurationSection)} will not be called
     *
     * @see #loadSettings(CommentedConfigurationSection)  The method that will load the settings from the configuration file
     */
    default void reload() {
        FishingPlugin plugin = FishingPlugin.get();
        Path path = this.configPath();
        if (path == null) return;

        File targetFile = new File(this.parentFolder(), path.toString());

        try {
            boolean addDefaults = false; // Should we add the defaults?

            // Create the file if it doesn't exist, set the defaults
            if (!targetFile.exists()) {
                this.createFile(targetFile);
                addDefaults = true;

                plugin.getLogger().info("Created a new file at path " + this.configPath()); // TODO: Remove... perhaps
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
     * Establish a new {@link CommentedConfigurationSection} from a base section, if the section does not exist, it will create a new section.
     * <p>
     * Example: {@code this.loadSettings(this.pullSection(config, "section-name"))}
     *
     * @param base The primary section to pull the section from if it exists
     * @param name The name of the section to pull, if it doesn't exist, it will create a new section under this name
     *
     * @return The section that was pulled or created
     */
    @NotNull
    default CommentedConfigurationSection pullSection(@NotNull CommentedConfigurationSection base, String name) {
        CommentedConfigurationSection section = base.getConfigurationSection(name);
        if (section == null) section = base.createSection(name);

        return section;
    }

    /**
     * Create a new {@link File} at the targeted path if it doesn't exist
     *
     * @param target The file to create
     *
     * @throws IOException If the file could not be created
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFile(@NotNull File target) throws IOException {

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
