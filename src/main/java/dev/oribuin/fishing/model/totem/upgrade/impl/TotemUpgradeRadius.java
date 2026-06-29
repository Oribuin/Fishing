package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.function.Supplier;

/**
 * A totem upgrade that increases the effective range of the totem
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TotemUpgradeRadius extends TotemUpgrade {

    private int baseRadius = 5;
    private String radiusFormula = "<base_radius> + (<level> * 5)"; // The formula to calculate the radius of the totem (5 blocks per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TotemUpgradeRadius() {
        super();
        this.description = List.of("Increases the effective range of the totem");
        this.defaultLevel = 1;
        this.maxLevel = 5;
    }

    /**
     * Calculate the radius of the totem based on the level of the upgrade.
     * <p>
     * Radius is divided by 2 so it acts as a radius instead of a diameter.
     *
     * @return The radius of the totem
     */
    public double getRadius() {
        Placeholders plc = Placeholders.of("level", this.level, "base_radius", this.baseRadius);
        return FishUtils.evaluate(plc.applyString(this.radiusFormula)) / 2;
    }

    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    @Override
    public Supplier<String> getIdentifier() {
        return getStaticIdentifier();
    }

    /**
     * Get the identifier for the totem upgrade
     *
     * @return The upgrade supplier
     */
    public static Supplier<String> getStaticIdentifier() {
        return () -> "radius";
    }

    //    /**
    //     * The totem upgrade placeholders for the upgrade.
    //     * All upgrades are added to the totems placeholders as "upgrade_<name>_<placeholder>"
    //     * <p>
    //     * Example: upgrade_radius_value
    //     *
    //     * @param totem The totem to apply the upgrade to
    //     *
    //     * @return The value of the upgrade
    //     */
    //    @Override
    //    public Placeholders getPlaceholders(Totem totem) {
    //        return Placeholders.builder()
    //                .addAll(super.getPlaceholders(totem))
    //                .add("value", this.getRadius(totem))
    //                .build();
    //    }

}
