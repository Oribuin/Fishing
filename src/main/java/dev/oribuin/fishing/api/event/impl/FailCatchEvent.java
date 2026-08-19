package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.api.event.FishEventWrapper;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FailCatchEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FishEventWrapper wrapper;
    private boolean cancelled;

    public FailCatchEvent(@NotNull Player who, @NotNull FishEventWrapper wrapper) {
        super(who, false);
        this.wrapper = wrapper;
    }

    public ItemStack getRod() {
        return this.wrapper.rod();
    }

    public FishHook getHook() {
        return this.wrapper.hook();
    }

    public Map<Augment, Integer> getAugments() {
        return this.wrapper.augments();
    }

    public Totem getTotem() {
        return this.wrapper.totem();
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