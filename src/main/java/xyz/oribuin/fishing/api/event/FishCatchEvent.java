package xyz.oribuin.fishing.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.fish.Fish;

import java.util.Map;

public class FishCatchEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final @NotNull ItemStack rod;
    private final @NotNull FishHook hook;
    private @Nullable Fish fish;
    private boolean cancelled;
    private int entropy;
    private int fishExp;
    private float naturalExp;

    /**
     * This event is fired whenever a player has sufficiently caught a fish.
     * Use this to modify the fish properties or change the values provided to the player
     * This method will also provide all the augments the rod has and other stuff.
     *
     * @param who  The player who caught the fish
     * @param rod  The fishing rod used
     * @param hook The hook where the fish will spawn
     */
    public FishCatchEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook, @NotNull Fish fish) {
        super(who, !Bukkit.isPrimaryThread());

        this.rod = rod;
        this.hook = hook;
        this.fish = fish;
        this.entropy = fish.tier().entropy();
        this.fishExp = fish.tier().fishExp();
        this.naturalExp = fish.tier().naturalExp();
    }

    /**
     * Pulls the list of augments that a player has equipped on their fishing rod.
     *
     * @return The list of augments used
     */
    public Map<Augment, Integer> augments() {
        return AugmentRegistry.from(this.rod);
    }

    public @NotNull ItemStack getRod() {
        return rod;
    }

    public @NotNull FishHook getHook() {
        return hook;
    }

    public @Nullable Fish getFish() {
        return fish;
    }

    public void setFish(@Nullable Fish fish) {
        this.fish = fish;
    }

    public int getEntropy() {
        return entropy;
    }

    public void setEntropy(int entropy) {
        this.entropy = entropy;
    }

    public int getFishExp() {
        return fishExp;
    }

    public void setFishExp(int fishExp) {
        this.fishExp = fishExp;
    }

    public float getNaturalExp() {
        return naturalExp;
    }

    public void setNaturalExp(float naturalExp) {
        this.naturalExp = naturalExp;
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
