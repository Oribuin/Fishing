package xyz.oribuin.fishing.storage;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.fishing.manager.ConfigurationManager.Setting;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.UUID;

public class FishingUser {

    private final UUID uuid;
    private int entropy;
    private int level;
    private int experience;
    private int skillPoints;

    public FishingUser(UUID uuid) {
        this.uuid = uuid;
        this.entropy = 0;
        this.level = 1;
        this.experience = 0;
        this.skillPoints = 0;
    }

    /**
     * Check the required experience to level up based on the current level
     *
     * @return The required experience to level up
     */
    public int getXpToNextLevel() {
        StringPlaceholders placeholders = StringPlaceholders.of("level", this.level);
        return (int) FishUtils.evaluate(placeholders.apply(Setting.REQUIRED_XP_FORMULA.getString()));
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

    public int skillCaught() {
        return this.skillPoints;
    }

    public void skillCaught(int skillCaught) {
        this.skillPoints = skillCaught;
    }

}
