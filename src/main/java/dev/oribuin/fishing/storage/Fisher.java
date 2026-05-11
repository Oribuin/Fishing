package dev.oribuin.fishing.storage;

import dev.oribuin.fishing.config.impl.Config;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fisher implements ForwardingAudience.Single {

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
        int requiredExp = this.getRequiredExp(); // Get the required experience to level up
        this.experience -= requiredExp; // Remove the required experience from the player
        this.level++;
        this.skillPoints++;
        return true;
    }

    @Override
    public @NotNull Audience audience() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) return player;
        return Audience.empty();
    }

    /**
     * Check if the player can level up based on the current experience
     *
     * @return If the player can level up
     */
    public boolean canLevelUp() {
        return this.experience >= this.getRequiredExp();
    }

    /**
     * Check the required experience to level up based on the current level
     *
     * @return The required experience to level up
     */
    public int getRequiredExp() {
        Placeholders placeholders = Placeholders.of("level", this.level);
        return (int) FishUtils.evaluate(placeholders.applyString(Config.get().getExperienceFormula()));
    }

    public Placeholders getPlaceholders() {
        return Placeholders.of(
                "entropy", this.entropy,
                "level", this.level,
                "experience", this.experience,
                "skill_points", this.skillPoints,
                "required_exp", this.getRequiredExp()
        );
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public int getEntropy() {
        return this.entropy;
    }

    public void setEntropy(int entropy) {
        this.entropy = entropy;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getSkillPoints() {
        return this.skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public Map<String, Integer> getSkills() {
        return this.skills;
    }

    public void setSkills(Map<String, Integer> skills) {
        this.skills = skills;
    }

}
