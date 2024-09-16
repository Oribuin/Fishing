package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.skill.Skill;
import xyz.oribuin.fishing.skill.impl.SkillComboCatcher;

import java.util.HashMap;
import java.util.Map;

public class SkillManager extends Manager {

    private final Map<String, Skill> skills = new HashMap<>();

    public SkillManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.skills.clear();

        this.register(new SkillComboCatcher());
        this.skills.values().forEach(Configurable::reload);
    }

    /**
     * Register a skill for the manager to handle
     *
     * @param skill The skill to handle and register
     */
    public void register(Skill skill) {
        String key = skill.name().toLowerCase();
        if (key.isEmpty() || key.contains(" ")) return;

        this.skills.put(key, skill);
    }

    @Override
    public void disable() {

    }

}
