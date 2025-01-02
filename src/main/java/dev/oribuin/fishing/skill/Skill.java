package dev.oribuin.fishing.skill;


import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.api.config.Configurable;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.manager.base.DataManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class Skill extends FishEventHandler implements Configurable {

    protected final String name;
    protected String description;

    /**
     * Skills are player unique upgrades, different to augments because
     * they are universal for the player and will activate regardless of what fishing
     * rod they use. These skills can be bought with fishing levels
     */
    public Skill(String name) {
        Objects.requireNonNull(name, "The skill name must not be null");
        this.name = name;
    }

    /**
     * Create a new skill instance with a name and description
     */
    public Skill(String name, String description) {
        this(name);
        this.description = description;
    }

    /**
     * Create a new skill instance with a name and description
     */
    public Skill(String name, String... description) {
        this(name);
        this.description = String.join("\n", description);
    }

    /**
     * Create a new skill instance with a name and description
     */
    public Skill(String name, List<String> description) {
        this(name);
        this.description = String.join("\n", description);
    }

    /**
     * The placeholders that will be applied in the skill description when it is displayed to the player
     * do not return null for this method, please only return {@link StringPlaceholders}
     *
     * @return The applicable placeholders, if any
     */
    @NotNull
    public StringPlaceholders placeholders(@NotNull Player player) {
        return StringPlaceholders.empty();
    }

    /**
     * The name of the skill, this is used to store what skills player has in {@link DataManager}
     *
     * @return The skill name
     */
    public String name() {
        return this.name;
    }

    /**
     * The path to the configuration file to be loaded. All paths will be relative to the {@link #parentFolder()},
     * If you wish to overwrite this functionality, override the {@link #parentFolder()} method
     *
     * @return The path
     */
    @Override
    public @NotNull Path configPath() {
        return Path.of("skills", this.name + ".yml");
    }

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
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.description = String.join("\n", config.getStringList("description"));

    }

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
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.addComments(this.comments().toArray(new String[0]));
        config.set("description", List.of(this.description.split("\n")));
    }

}
