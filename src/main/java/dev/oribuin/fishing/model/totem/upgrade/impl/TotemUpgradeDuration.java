package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * A totem upgrade that increases the duration of the totem when activated
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TotemUpgradeDuration extends TotemUpgrade {

    private String durationFormula = "180 + (<level> * 30)"; // The formula to calculate the duration of the totem (60 seconds + 30 seconds per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TotemUpgradeDuration() {
        super();
        this.description = List.of("Increases the duration of the totem when activated");
        this.defaultLevel = 0;
        this.maxLevel = 10;
    }

    /**
     * Calculate the radius of the
     *
     * @return The radius of the totem
     */
    public Duration getDuration() {
        Placeholders plc = Placeholders.of("level", this.level);
        return Duration.ofMillis((long) FishUtils.evaluate(plc.applyString(this.durationFormula)) * 1000);
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
        return () -> "duration";
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
        long duration = this.getDuration().toMillis();
        String totalDuration = FishUtils.formatTime(duration);

        return Placeholders.of(
                "total", totalDuration,
                "remaining", totem.isActive() ? FishUtils.formatTime(totem.getCurrentDuration()) : totalDuration
        );
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
    //                .add("value", FishUtils.formatTime(this.calculateDuration(totem).toMillis()))
    //                .add("timer", FishUtils.formatTime(totem.getCurrentDuration()))
    //                .build();
    //    }

}
