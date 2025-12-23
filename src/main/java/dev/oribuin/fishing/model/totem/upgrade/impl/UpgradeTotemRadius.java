package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * A totem upgrade that increases the effective range of the totem
 */
@ConfigSerializable
@SuppressWarnings({ "UnstableApiUsage", "FieldMayBeFinal" })
public class UpgradeTotemRadius extends TotemUpgrade {

    private int baseRadius = 5;
    private String radiusFormula = "<base_radius> + (<level> * 5)"; // The formula to calculate the radius of the totem (5 blocks per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public UpgradeTotemRadius() {
        super("radius", "Increases the effective range of the totem");

        this.defaultLevel(1);
        this.maxLevel(5);
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
                .add("value", this.calculateRadius(totem))
                .build();
    }

    /**
     * Calculate the radius of the totem based on the level of the upgrade.
     * <p>
     * Radius is divided by 2 so it acts as a radius instead of a diameter.
     *
     * @param totem The totem to calculate the radius for
     *
     * @return The radius of the totem
     */
    public int calculateRadius(Totem totem) {
        Integer level = totem.getProperty(this.key(), this.defaultLevel());
        Placeholders plc = Placeholders.of("level", level, "base_radius", this.baseRadius);
        return (int) FishUtils.evaluate(plc.applyString(this.radiusFormula)) / 2;
    }

}
