package dev.oribuin.fishing.model.totem.upgrade.impl;

import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.model.totem.upgrade.TotemUpgrade;
import dev.oribuin.fishing.util.FishUtils;
import dev.oribuin.fishing.util.Placeholders;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * A totem upgrade that decreases the cooldown of the totem once deactivated
 */
@ConfigSerializable
@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
public class TotemUpgradeCooldown extends TotemUpgrade {

    private String cooldownFormula = "(3600+120) - (<level> * 120)"; // The formula to calculate the cooldown of the totem (1 hour - 2 minute per level)

    /**
     * Create a new totem upgrade with the name "radius"
     */
    public TotemUpgradeCooldown() {
        super();
        this.description = "Decreases the activation cooldown";
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

    /**
     * Get the additional placeholders for a totem upgrade
     *
     * @param totem The totem with the upgrade
     *
     * @return The resulting placeholders
     */
    @Override
    public @NotNull Placeholders getPlaceholders(@NotNull Totem totem) {
        long cooldown = this.getCooldown().toMillis();
        String totalCooldown = FishUtils.formatTime(cooldown);

        return Placeholders.builder().addAll(super.getPlaceholders(totem))
                .add("total", totalCooldown)
                .add("remaining", totem.onCooldown() ? FishUtils.formatTime(totem.getCurrentCooldown()) : totalCooldown)
                .build();
    }

}
