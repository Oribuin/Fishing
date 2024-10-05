package xyz.oribuin.fishing.skill;

import xyz.oribuin.fishing.skill.impl.SkillComboCatcher;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {

    private static final Map<Class<? extends Skill>, Skill> skills = new HashMap<>();

    /**
     * The plugin initializes all the skills and registers them
     */
    public static void init() {
        skills.clear();

        register(new SkillComboCatcher());
    }

    /**
     * Register a skill into the registry to be loaded
     *
     * @param skill The skill to register
     */
    public static void register(Skill skill) {
        skill.reload(); // Load the skill
        skills.put(skill.getClass(), skill); // Register the skill
    }

    /**
     * Get a skill from the registry
     *
     * @param clazz The class of the skill
     *
     * @return The skill
     */
    public static Skill get(Class<? extends Skill> clazz) {
        return skills.get(clazz);
    }

    /**
     * Get all the skills from the registry
     *
     * @return The skills
     */
    public static Map<Class<? extends Skill>, Skill> all() {
        return skills;
    }

}
