package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.manager.TierManager;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import dev.oribuin.fishing.model.fish.condition.ConditionRegistry;
import dev.oribuin.fishing.util.FishUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This event is fired whenever the plugin attempts to generate a fish for a player.
 */
public class FishGenerateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final @NotNull ItemStack rod;
    private final @NotNull FishHook hook;
    private final double baseChance;
    private @Nullable Fish fish;
    private final List<Double> chanceIncreases;
    private boolean cancelled;

    /**
     * Create a new FishGenerateEvent to be called when the plugin attempts to generate a fish for a player.
     * <p>
     * Use this event to modify the tier of fish that can be caught through {@link #addIncrease(double)}.
     * <p>
     * The base chance is a random number between 0 and 100. Use {@link java.util.concurrent.ThreadLocalRandom#nextDouble(double)} to generate new numbers.
     *
     * @param who  The {@link Player} who is catching the fish
     * @param rod  The {@link ItemStack} fishing rod the player is using
     * @param hook The {@link FishHook} the hook the fish was caught on
     *
     * @see dev.oribuin.fishing.manager.FishManager#generateFish(Map, Player, ItemStack, FishHook)   Where the event is called
     */
    public FishGenerateEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook) {
        super(who, !Bukkit.isPrimaryThread());

        this.rod = rod;
        this.hook = hook;
        this.baseChance = FishUtils.RANDOM.nextDouble(100);
        this.chanceIncreases = new ArrayList<>();
    }

    /**
     * Adds an increase to the base chance of the fish
     *
     * @param increase The increase to add to the base chance
     *
     * @return The new chance
     */
    public double addIncrease(double increase) {
        this.chanceIncreases.add(increase);
        return this.baseChance + increase;
    }

    /**
     * Pulls the list of augments that a player has equipped on their fishing rod.
     *
     * @return The list of augments used
     */
    public Map<Augment, Integer> augments() {
        return AugmentRegistry.from(this.rod);
    }

    /**
     * Generates a new {@link Fish} based on the base chance and the chance increases.
     * <p>
     * The formula is: baseChance + sumOf(chanceIncreases) = newChance
     * Tiers are selected from the highest rarity -> the lowest rarity based on the newChance
     */
    public void generate() {
        TierManager tierProvider = FishingPlugin.get().getManager(TierManager.class);

        // Obtain the quality of the
        double newChance = this.baseChance + this.chanceIncreases
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        Tier quality = tierProvider.selectTier(newChance);
        if (quality == null) return;

        // Make sure the quality is not null
        List<Fish> canCatch = quality.fish().values().stream()
                .filter(x -> ConditionRegistry.check(x, player, rod, hook))
                .toList();

        if (canCatch.isEmpty()) return;

        // Pick a random fish from the list
        this.fish = canCatch.get(FishUtils.RANDOM.nextInt(canCatch.size()));
    }

    /**
     * The fishing rod the player is using
     *
     * @return The itemstack of the fishing rod
     */
    public @NotNull ItemStack rod() {
        return rod;
    }

    /**
     * The hook that the fish was caught on
     *
     * @return The fishhook entity
     */
    public @NotNull FishHook hook() {
        return hook;
    }

    /**
     * The base chance of the fish, this was the original rarity chance of the fish
     *
     * @return The base chance of the fish
     */
    public double baseChance() {
        return baseChance;
    }

    /**
     * The chance increases that have been added to the base chance
     *
     * @return The chance increases
     */
    public List<Double> chanceIncreases() {
        return chanceIncreases;
    }

    /**
     * The fish that was generated
     *
     * @return The fish that was generated
     */
    public @Nullable Fish fish() {
        return fish;
    }

    /**
     * Set the fish that was generated
     *
     * @param fish The fish that was generated
     */
    public void fish(@Nullable Fish fish) {
        this.fish = fish;
    }

    /**
     * Get the handlers for this event class
     *
     * @return The handlers for this event class
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Get the handlers for this event class
     *
     * @return The handlers for this event class
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Check if the event is cancelled
     *
     * @return If the event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Set the event to be cancelled
     *
     * @param b If the event should be cancelled
     */
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
