package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.Settings;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.rod.RodRarity;
import dev.oribuin.fishing.storage.persistent.FishDataType;
import dev.oribuin.fishing.storage.util.KeyRegistry;
import dev.oribuin.fishing.util.Placeholders;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static dev.oribuin.fishing.storage.util.KeyRegistry.ROD_BASE_CAPACITY;
import static dev.oribuin.fishing.storage.util.KeyRegistry.ROD_RARITY;

public class RodManager implements Manager {

    private final FishingPlugin plugin;

    public RodManager(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {

    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {
    }

    /**
     * Increment a statistic on the rod by 1
     *
     * @param rod       The rod to increase the statistic of
     * @param statistic The statistic to increase
     */
    public void incrementStatistic(@NotNull ItemStack rod, @NotNull FishDataType<Double, Double> statistic) {
        this.incrementStatistic(rod, statistic, 1);
    }

    /**
     * Increment a statistic on the rod by a specified amount
     *
     * @param rod       The rod to increase the statistic of
     * @param statistic The statistic to increase
     * @param amount    The amount to increase the statistic by
     */
    public void incrementStatistic(@NotNull ItemStack rod, @NotNull FishDataType<Double, Double> statistic, double amount) {
        rod.editPersistentDataContainer(container -> {
            double current = container.getOrDefault(statistic.key(), statistic, 0.0);
            container.set(statistic.key(), statistic, current + amount);
        });
    }

    /**
     * Get the current statistic value for the plugin
     *
     * @param rod       The rod to get the statistics for
     * @param statistic The statistic to get
     *
     * @return The resulting statistic
     */
    public double getStatistic(@NotNull ItemStack rod, @NotNull FishDataType<Double, Double> statistic) {
        PersistentDataContainerView container = rod.getPersistentDataContainer();
        return container.getOrDefault(statistic.key(), statistic, 0.0);
    }

    /**
     * Get all the statistics of the fishing rod as a placeholder
     *
     * @param container The container to get the rod from
     *
     * @return The resulting statistic placeholders
     */
    @NotNull
    public Map<String, Double> getStatistics(@NotNull PersistentDataContainerView container) {
        return new HashMap<>() {{
            KeyRegistry.STATISTICS.forEach(stat -> {
                String key = stat.key().value().replace("statistic_", "");
                double value = container.getOrDefault(stat.key(), stat, 0.0);
                this.put(key, value);
            });
        }};
    }

    /**
     * Get all the statistics of the fishing rod as a placeholder
     *
     * @param container The container to get the rod from
     *
     * @return The resulting statistic placeholders
     */
    @NotNull
    public Placeholders getPlaceholderStats(@NotNull PersistentDataContainerView container) {
        Placeholders.Builder placeholders = Placeholders.builder();
        this.getStatistics(container).forEach(placeholders::add);
        return placeholders.build();
    }

    /**
     * Check whether a fishing rod can accept an augment
     *
     * @param stack   The fishing rod
     * @param augment The augment to apply
     *
     * @return Whether the rod can accept the upgrade
     */
    public boolean canAccept(ItemStack stack, Augment augment) {
        int maximum = this.getMaximumCapacity(stack);
        int current = this.getConsumedCapacity(stack);
        if (current >= maximum) return false;

        return current + augment.getLevel() <= maximum;
    }

    /**
     * Get the remaining amount of augment levels that can be applied to the rod
     *
     * @param stack The fishing rod
     *
     * @return The remaining augment capacity
     */
    public int getRemainingCapacity(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != Material.FISHING_ROD) return 0;

        int maximum = this.getMaximumCapacity(stack);
        int current = this.getConsumedCapacity(stack);
        return Math.min(0, maximum - current);
    }

    /**
     * Get the current capacity of augments consumed by the fishing rod
     *
     * @param stack The fishing rod
     *
     * @return The combined level of all augments
     */
    public int getConsumedCapacity(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != Material.FISHING_ROD) return 0;

        return this.plugin.getAugmentManager().getAugments(stack).values()
                .stream()
                .mapToInt(Augment::getLevel)
                .sum();
    }

    /**
     * Get the maximum augment slots available on the rod
     *
     * @param stack The stack to get the capacity from
     *
     * @return The rod
     */
    public int getMaximumCapacity(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != Material.FISHING_ROD) return 0;

        PersistentDataContainerView container = stack.getPersistentDataContainer();
        RodRarity rarity = this.getRarity(container.get(ROD_RARITY.key(), ROD_RARITY));
        if (rarity == null) return 0;

        Integer baseCapacity = container.getOrDefault(ROD_BASE_CAPACITY.key(), ROD_BASE_CAPACITY, 0);
        return baseCapacity + rarity.getCapacity();
    }

    /**
     * Find the rarity type from the fishing rod
     *
     * @param identifier The rarity identifier
     *
     * @return The rod rarity
     */
    @Nullable
    public RodRarity getRarity(@Nullable String identifier) {
        if (identifier == null) return null;
        return Settings.get().getRodUpgrades().get(identifier);
    }

    /**
     * Find the rarity type from the fishing rod
     *
     * @param stack The upgraded fishing rod
     *
     * @return The rod rarity
     */
    @Nullable
    public RodRarity getRarity(@Nullable ItemStack stack) {
        if (stack == null || stack.getType() != Material.FISHING_ROD) return null;

        PersistentDataContainerView container = stack.getPersistentDataContainer();
        return this.getRarity(container.get(ROD_RARITY.key(), ROD_RARITY));
    }


}
