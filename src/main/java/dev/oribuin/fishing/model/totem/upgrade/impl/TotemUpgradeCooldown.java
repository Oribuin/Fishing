package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * A totem upgrade that decreases the cooldown of the totem once deactivated
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TotemUpgradeCooldown extends TotemUpgrade {

    private String cooldownFormula = "(60 * 60) - (<level> * 120)"; // The formula to calculate the cooldown of the totem (1 hour - 2 minute per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TotemUpgradeCooldown() {
        super();
        this.description = List.of("Decreases the cooldown of the totem when it has expired");
        this.defaultLevel = 0;
        this.maxLevel = 25;
    }

    /**
     * Get the cooldown of the totem when it's finished
     *
     * @return The radius of the totem
     */
    public Duration getCooldown() {
        Placeholders plc = Placeholders.of("level", this.level);
        return Duration.ofMillis((long) FishUtils.evaluate(plc.applyString(this.cooldownFormula)) * 1000);
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
        return () -> "cooldown";
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
    //                .add("value", FishUtils.formatTime(this.getCooldown(totem).toMillis()))
    //                .add("timer", FishUtils.formatTime(totem.getCurrentCooldown()))
    //                .build();
    //    }

}
