package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.ProcessedFish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FishSellEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final @Nullable ItemStack rod;
    private final @NotNull Map<String, Augment> augments;
    private final @NotNull List<ProcessedFish> sold;
    private final double baseMoney;
    private double money;
    private boolean cancelled;

    public FishSellEvent(@NotNull Player who, @Nullable ItemStack rod, @NotNull Map<String, Augment> augments, @NotNull List<ProcessedFish> sold) {
        super(who, false);

        this.rod = rod;
        this.augments = augments;
        this.sold = sold;
        this.baseMoney = this.sold.stream().mapToDouble(value -> value.tier().getSellMoney() * value.amount()).sum();
        this.money = baseMoney;
    }

    public @NotNull Map<String, Augment> getAugments() {
        return augments;
    }

    public @Nullable ItemStack getRod() {
        return rod;
    }

    public @NotNull List<ProcessedFish> getSold() {
        return sold;
    }

    public double getBaseMoney() {
        return baseMoney;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
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
