package xyz.oribuin.fishing.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.fish.Fish;

import java.util.Map;

public class FishGutEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ItemStack rod;
    private final Map<Fish, Integer> gutted;
    private final int baseEntropy;
    private int entropy;
    private boolean cancelled;

    public FishGutEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull Map<Fish, Integer> gutted) {
        super(who, false);

        this.rod = rod;
        this.gutted = gutted;
        this.baseEntropy = this.calculateEntropy();
        this.entropy = this.baseEntropy;
    }

    public int calculateEntropy() {
        return this.gutted.entrySet()
                .stream()
                .mapToInt(e -> e.getKey().tier().entropy() * e.getValue()).
                sum();
    }

    public ItemStack getRod() {
        return rod;
    }

    public Map<Fish, Integer> getGutted() {
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
