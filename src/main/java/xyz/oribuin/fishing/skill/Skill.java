package xyz.oribuin.fishing.skill;


import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.Configurable;
import xyz.oribuin.fishing.api.FishEventHandler;

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
     * The name of the skill, this is used to store what skills player has in {@link xyz.oribuin.fishing.manager.DataManager}
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
     * Load the settings from the configuration file
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedFileConfiguration config) {
        this.description = config.getString("description", "No Description");
    }

    /**
     * Save the configuration file for the configurable class
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedFileConfiguration config) {
        config.set("description", this.description);
    }

}
