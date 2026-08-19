package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.config.impl.Config;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.rod.RodRarity;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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
                .mapToInt(Integer::intValue)
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
        return Config.get().getRodUpgrades().get(identifier);
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
