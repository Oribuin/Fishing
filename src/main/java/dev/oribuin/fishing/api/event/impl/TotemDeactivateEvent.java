package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called when a player activates a fishing totem
 */
public class TotemDeactivateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Totem totem;
    private boolean cancelled;

    /**
     * Define a new Totem Deactivate Event, This is called when a totem deactivates by running out of time.
     *
     * @param totem The {@link Totem} that is being deactivated
     */
    public TotemDeactivateEvent(@NotNull Totem totem) {
        super(!Bukkit.isPrimaryThread());

        this.totem = totem;
    }

    /**
     * Get the totem that is being deactivated
     *
     * @return The totem that is being deactivated
     */
    public @NotNull Totem totem() {
        return this.totem;
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
