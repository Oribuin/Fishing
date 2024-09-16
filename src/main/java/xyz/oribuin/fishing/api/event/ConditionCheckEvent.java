package xyz.oribuin.fishing.api.event;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.condition.CatchCondition;

public class ConditionCheckEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final ItemStack rod;
    private final FishHook hook;
    private String condition;
    private boolean result;

    public ConditionCheckEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook, @NotNull String condition, boolean result) {
        super(who, false);

        this.rod = rod;
        this.hook = hook;
        this.condition = condition;
        this.result = result;
    }

    public ItemStack getRod() {
        return rod;
    }

    public FishHook getHook() {
        return hook;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
