package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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
        this.description = "Increases the totem's range";
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
     * Calculate the radius of the totem based on the level of the upgrade.
     * <p>
     * Radius is divided by 2 so it acts as a radius instead of a diameter.
     *
     * @return The radius of the totem
     */
    public double getTotalRadius() {
        Placeholders plc = Placeholders.of("level", this.level, "base_radius", this.baseRadius);
        return FishUtils.evaluate(plc.applyString(this.radiusFormula));
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

    /**
     * Get the additional placeholders for a totem upgrade
     *
     * @param totem The totem with the upgrade
     *
     * @return The resulting placeholders
     */
    @Override
    public @NotNull Placeholders getPlaceholders(@NotNull Totem totem) {
        return Placeholders.builder().addAll(super.getPlaceholders(totem))
                .add("effective", this.getRadius())
                .add("total", this.getTotalRadius())
                .build();

    }
}
