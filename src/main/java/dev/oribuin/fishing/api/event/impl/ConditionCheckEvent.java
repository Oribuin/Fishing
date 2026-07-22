package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.api.event.FishEventWrapper;
import dev.oribuin.fishing.model.condition.CatchCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This event determines the result of a condition check, which can be modified by other conditions.
 */
public class ConditionCheckEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FishEventWrapper wrapper;
    private final CatchCondition condition;
    private boolean result;
    private boolean cancelled;

    /**
     * Define a new Condition Check Event, This is called when a fish condition is checked, Use this to modify the result of the condition.
     *
     * @param who       The {@link Player} who is checking the condition
     * @param condition The {@link CatchCondition} that is being checked
     * @param result    The result of the condition check, True if the player meets the condition
     */
    public ConditionCheckEvent(@NotNull Player who, @NotNull FishEventWrapper wrapper, @NotNull CatchCondition condition, boolean result) {
        super(who, !Bukkit.isPrimaryThread());
        this.wrapper = wrapper;
        this.condition = condition;
        this.result = result;
    }

    /**
     * The fishing rod the player is using
     *
     * @return The itemstack of the fishing rod
     */
    public ItemStack getRod() {
        return this.wrapper.rod();
    }

    /**
     * The fishhook the player is using
     *
     * @return The fishhook entity
     */
    public FishHook getHook() {
        return this.wrapper.hook();
    }

    /**
     * The condition that is being checked
     *
     * @return The condition being checked
     */
    public CatchCondition getCondition() {
        return condition;
    }

    /**
     * The result of the condition check
     *
     * @return The result of the condition check
     */
    public boolean result() {
        return result;
    }

    /**
     * Set the result of the condition check
     *
     * @param result The result of the condition check
     */
    public void result(boolean result) {
        this.result = result;
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
