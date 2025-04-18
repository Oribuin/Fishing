package dev.oribuin.fishing.api.event.impl;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InitialFishCatchEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final ItemStack rod;
    private final FishHook hook;
    private int amountToCatch;

    public InitialFishCatchEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook) {
        super(who, false);

        this.rod = rod;
        this.hook = hook;
        this.amountToCatch = 1;
    }

    public ItemStack getRod() {
        return rod;
    }

    public FishHook getHook() {
        return hook;
    }

    public int getAmountToCatch() {
        return amountToCatch;
    }

    public void setAmountToCatch(int amountToCatch) {
        this.amountToCatch = amountToCatch;
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
