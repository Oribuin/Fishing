package xyz.oribuin.fishing.storage;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.manager.base.ConfigurationManager.Setting;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fisher {

    private final UUID uuid;
    private int entropy;
    private int level;
    private int experience;
    private int skillPoints;
    private Map<String, Integer> skills;

    public Fisher(UUID uuid) {
        this.uuid = uuid;
        this.entropy = 0;
        this.level = 1;
        this.experience = 0;
        this.skillPoints = 1;
        this.skills = new HashMap<>();
    }

    /**
     * Level up the player if they have enough experience
     *
     * @return If the player leveled up
     */
    public boolean levelUp() {
        int requiredExp = this.requiredExp(); // Get the required experience to level up
        this.experience -= requiredExp; // Remove the required experience from the player
        this.level++;
        this.skillPoints++;
        return true;
    }

    /**
     * Check if the player can level up based on the current experience
     *
     * @return If the player can level up
     */
    public boolean canLevelUp() {
        return this.experience >= this.requiredExp();
    }

    /**
     * Check the required experience to level up based on the current level
     *
     * @return The required experience to level up
     */
    public int requiredExp() {
        StringPlaceholders placeholders = StringPlaceholders.of("level", this.level);
        return (int) FishUtils.evaluate(placeholders.apply(Setting.REQUIRED_XP_FORMULA.getString()));
    }

    public StringPlaceholders placeholders() {
        return StringPlaceholders.of(
                "level", this.level,
                "experience", this.experience,
                "skill_points", this.skillPoints,
                "required_exp", this.requiredExp()
        );
    }

    public UUID uuid() {
        return this.uuid;
    }

    public int entropy() {
        return this.entropy;
    }

    public void entropy(int entropy) {
        this.entropy = entropy;
    }

    public int level() {
        return this.level;
    }

    public void level(int level) {
        this.level = level;
    }

    public int experience() {
        return this.experience;
    }

    public void experience(int experience) {
        this.experience = experience;
    }

    public int points() {
        return this.skillPoints;
    }

    public void points(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public Map<String, Integer> skills() {
        return this.skills;
    }

    public void skills(Map<String, Integer> skills) {
        this.skills = skills;
    }

}
