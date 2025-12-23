package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;

import static com.jeff_media.morepersistentdatatypes.DataType.INTEGER;

/**
 * A totem upgrade that increases the duration of the totem when activated
 */
@ConfigSerializable

public class UpgradeTotemDuration extends TotemUpgrade {
    private final String durationFormula = "60 + (<level> * 30)"; // The formula to calculate the duration of the totem (60 seconds + 30 seconds per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public UpgradeTotemDuration() {
        super("duration", "Increases the duration of the totem when activated");

        this.defaultLevel(0);
        this.maxLevel(10);
    }

    /**
     * Initialize the upgrade to the totem at the specified level
     *
     * @param totem The totem to apply the upgrade to
     * @param level The level of the upgrade
     */
    @Override
    public void initialize(Totem totem, int level) {
        totem.applyProperty(INTEGER, this.key(), level);
    }

    /**
     * Calculate the radius of the totem based on the level of the upgrade
     *
     * @param totem The totem to calculate the radius for
     *
     * @return The radius of the totem
     */
    public Duration calculateDuration(Totem totem) {
        Integer level = totem.getProperty(this.key(), this.defaultLevel());
        Placeholders plc = Placeholders.of("level", level);
        return Duration.ofMillis((long) FishUtils.evaluate(plc.applyString(this.durationFormula)) * 1000);
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
                .add("value", FishUtils.formatTime(this.calculateDuration(totem).toMillis()))
                .add("timer", FishUtils.formatTime(totem.getCurrentDuration()))
                .build();
    }

}
