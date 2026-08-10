package dev.oribuin.fishing.model.totem.upgrade;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.totem.upgrade.impl.TotemUpgradeCooldown;
import dev.oribuin.fishing.model.totem.upgrade.impl.TotemUpgradeDuration;
import dev.oribuin.fishing.model.totem.upgrade.impl.TotemUpgradeRadius;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TotemUpgradeRegistry {


    private static final Map<String, RegisteredUpgrade<?>> UPGRADES = new HashMap<>();

    public static void register() {
        register("cooldown", TotemUpgradeCooldown.class);
        register("duration", TotemUpgradeDuration.class);
        register("radius", TotemUpgradeRadius.class);
    }
    
    /**
     * Register a new upgrade into the plugin to be used by totems
     *
     * @param identifier   The totem identifier
     * @param upgradeClass THe upgrade class
     * @param <T>          The upgrade being registered
     */
    public static <T extends TotemUpgrade> void register(String identifier, Class<T> upgradeClass) {
        RegisteredUpgrade<?> existing = UPGRADES.get(identifier);
        if (existing != null) {
            FishingPlugin.get().getLogger().warning(
                    "Could not register totem" +
                    " upgrade[" + identifier + "] from" +
                    " class[" + upgradeClass.getSimpleName() + "] " +
                    "as one exists with this name already."
            );
            return;
        }

        TotemManager.getLoader().loadConfig(upgradeClass, identifier);
        UPGRADES.put(identifier, new RegisteredUpgrade<>(identifier, upgradeClass, () -> TotemManager.getLoader().getClone(upgradeClass)));
    }

    /**
     * Get an upgrade id from the class it's stored in
     *
     * @param upgradeClass The upgrade class
     * @param <T>          The totem upgrade type
     *
     * @return The id for the totem upgrade if possible
     */
    @Nullable
    public static <T extends TotemUpgrade> String getUpgradeId(@NotNull Class<T> upgradeClass) {
        return UPGRADES.entrySet().stream().
                filter(entry -> entry.getValue().upgrade().equals(upgradeClass))
                .map(Map.Entry::getKey)
                .findAny()
                .orElse(null);
    }

    /**
     * Get default totem upgrades from the plugin
     *
     * @return All totem upgrades
     */
    public static Map<String, TotemUpgrade> getDefault() {
        Map<String, TotemUpgrade> upgrades = new HashMap<>();
        UPGRADES.keySet().forEach(s -> {
            RegisteredUpgrade<?> registeredUpgrade = UPGRADES.get(s);
            if (registeredUpgrade != null) upgrades.put(s, registeredUpgrade.supplier().get());
        });
        return upgrades;
    }

    public record RegisteredUpgrade<T extends TotemUpgrade>(String identifier, Class<T> upgrade, Supplier<T> supplier) {

    }
}
