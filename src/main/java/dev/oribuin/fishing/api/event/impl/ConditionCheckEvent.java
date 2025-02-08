package dev.oribuin.fishing.api.event.impl;

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
    private final ItemStack rod;
    private final FishHook hook;
    private final CatchCondition condition;
    private boolean result;
    private boolean cancelled;

    /**
     * Define a new Condition Check Event, This is called when a fish condition is checked, Use this to modify the result of the condition.
     *
     * @param who       The {@link Player} who is checking the condition
     * @param rod       The {@link ItemStack} fishing rod the player is using
     * @param hook      The {@link FishHook} the hook the fish was caught on
     * @param condition The {@link CatchCondition} that is being checked
     * @param result    The result of the condition check, True if the player meets the condition
     */
    public ConditionCheckEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook, @NotNull CatchCondition condition, boolean result) {
        super(who, !Bukkit.isPrimaryThread());

        this.rod = rod;
        this.hook = hook;
        this.condition = condition;
        this.result = result;
    }

    /**
     * The fishing rod the player is using
     *
     * @return The itemstack of the fishing rod
     */
    public ItemStack rod() {
        return rod;
    }

    /**
     * The fishhook the player is using
     *
     * @return The fishhook entity
     */
    public FishHook hook() {
        return hook;
    }

    /**
     * The condition that is being checked
     *
     * @return The condition being checked
     */
    public CatchCondition condition() {
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
