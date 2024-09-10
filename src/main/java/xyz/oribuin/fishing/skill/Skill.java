package xyz.oribuin.fishing.skill;


import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.FishEventHandler;

import java.util.Objects;

public abstract class Skill extends FishEventHandler {

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
     * @return The skill name
     */
    public String name() {
        return this.name;
    }

}
