package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called when a player activates a fishing totem
 */
public class TotemActivateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Totem totem;
    private final Player activator;
    private boolean cancelled;

    /**
     * Define a new Totem Activate Event, This is called when a player activates a fishing totem to catch fish
     *
     * @param totem     The {@link Totem} that is being activated
     * @param activator The {@link Player} who is activating the totem
     */
    public TotemActivateEvent(@NotNull Totem totem, @NotNull Player activator) {
        super(activator, !Bukkit.isPrimaryThread());

        this.totem = totem;
        this.activator = activator;
    }

    /**
     * Get the totem that is being activated
     *
     * @return The totem that is being activated
     */
    public @NotNull Totem totem() {
        return this.totem;
    }

    /**
     * Get the player who is activating the totem
     *
     * @return The player who is activating the totem
     */
    public @NotNull Player activator() {
        return this.activator;
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
