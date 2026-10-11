package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.api.event.FishEventWrapper;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.ProcessedFish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class FishGutEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final @Nullable ItemStack rod;
    private final @NotNull Map<String, Augment> augments;
    private final @NotNull List<ProcessedFish> gutted;
    private final int baseEntropy;
    private int entropy;
    private boolean cancelled;

    public FishGutEvent(@NotNull Player who, @Nullable ItemStack rod, @NotNull Map<String, Augment> augments, @NotNull List<ProcessedFish> gutted) {
        super(who, false);

        this.rod = rod;
        this.augments = augments;
        this.gutted = gutted;
        this.baseEntropy = this.gutted.stream()
                .mapToInt(value -> value.tier().getGutEntropy() * value.amount())
                .sum();
        this.entropy = this.baseEntropy;
    }

    public @NonNull Map<String, Augment> getAugments() {
        return augments;
    }

    public @NonNull List<ProcessedFish> getGutted() {
        return gutted;
    }

    public int getBaseEntropy() {
        return baseEntropy;
    }

    public int getEntropy() {
        return entropy;
    }

    public void setEntropy(int entropy) {
        this.entropy = entropy;
    }

    public @Nullable ItemStack getRod() {
        return rod;
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
