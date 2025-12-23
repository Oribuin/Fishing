package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;

/**
 * A totem upgrade that decreases the cooldown of the totem once deactivated
 */
@ConfigSerializable
public class UpgradeTotemCooldown extends TotemUpgrade {

    private final String cooldownFormula = "(60 * 60) - (<level> * 120)"; // The formula to calculate the cooldown of the totem (1 hour - 2 minute per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public UpgradeTotemCooldown() {
        super("cooldown", "Decreases the cooldown of the totem once deactivated");

        this.defaultLevel(0);
        this.maxLevel(15);
    }

    /**
     * Calculate the radius of the totem based on the level of the upgrade
     *
     * @param totem The totem to calculate the radius for
     *
     * @return The radius of the totem
     */
    public Duration calculateCooldown(Totem totem) {
        Integer level = totem.getProperty(this.key(), this.defaultLevel());
        Placeholders plc = Placeholders.of("level", level);
        return Duration.ofMillis((long) FishUtils.evaluate(plc.applyString(this.cooldownFormula)) * 1000);
    }

    /**
     * The totem upgrade placeholders for the upgrade.
     * All upgrades are added to the totems placeholders as "upgrade_<name>_<placeholder>"
     * <p>
     * Example: upgrade_radius_value
     *
     * @param totem The totem to apply the upgrade to
     *
     * @return The value of the upgrade
     */
    @Override
    public Placeholders placeholders(Totem totem) {
        return Placeholders.builder()
                .addAll(super.placeholders(totem))
                .add("value", FishUtils.formatTime(this.calculateCooldown(totem).toMillis()))
                .add("timer", FishUtils.formatTime(totem.getCurrentCooldown()))
                .build();
    }

}
