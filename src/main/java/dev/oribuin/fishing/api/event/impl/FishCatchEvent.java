package dev.oribuin.fishing.api.event.impl;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventWrapper;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.fish.Tier;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The event that is fired once a player has caught a fish. This event will be used to modify the rewards and the fish itself.
 */
public class FishCatchEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final FishEventWrapper wrapper;
    private Fish fish;
    private int entropy;
    private int fishExp;
    private float naturalExp;
    private boolean cancelled;

    /**
     * Create a new Fish Catch Event to be called when a player catches a fish. This event is used to change the rewards when a player catches a fish.
     * Use this to change how much entropy / xp is earned from the fish.
     *
     * @param who  The {@link Player} who caught the fish
     * @param wrapper  The fishing event stuff
     * @param fish The {@link Fish} that was caught
     *
     * @see dev.oribuin.fishing.listener.FishListener#onFish(PlayerFishEvent) Where the event is called
     */
    public FishCatchEvent(@NotNull Player who, @NotNull FishEventWrapper wrapper, @NotNull Fish fish) {
        super(who, !Bukkit.isPrimaryThread());
        this.wrapper = wrapper;
        this.fish = fish;
        this.cancelled = false;

        // Set the base values for the fish
        Tier tier = FishingPlugin.get().getTierManager().get(this.fish.getTier());
        this.entropy = tier.getCatchEntropy();
        this.fishExp = tier.getCatchExperience();
        this.naturalExp = tier.getNaturalExperience();
    }

    /**
     * The list of every {@link Augment} used on the fishing rod used to catch the fish.
     *
     * @return The list of augments and the level of the augment
     */
    public Map<Augment, Integer> getAugments() {
        return this.wrapper.augments();
    }

    /**
     * The fishing rod the player is using to catch the fish
     * -
     *
     * @return The fishing rod {@link ItemStack}
     */
    public @NotNull ItemStack getRod() {
        return this.wrapper.rod();
    }

    /**
     * The fishhook entity the player is using to catch the fish
     *
     * @return The {@link FishHook} entity
     */
    public @NotNull FishHook getHook() {
        return this.wrapper.hook();
    }

    /**
     * The fish that was caught
     *
     * @return The {@link Fish} that was caught
     */
    public @Nullable Fish getFish() {
        return this.fish;
    }

    /**
     * Set the fish that was caught
     *
     * @param fish The fish that was caught
     */
    public void setFish(@Nullable Fish fish) {
        this.fish = fish;
    }

    /**
     * The base amount of entropy the fish gives
     *
     * @return The base amount of entropy the fish gives
     */
    public int getBaseCatchEntropy() {
        return this.fish.getTierInstance().getCatchEntropy();
    }

    /**
     * The amount of entropy the fish gives
     *
     * @return The amount of entropy the fish gives
     */
    public int getCatchEntropy() {
        return entropy;
    }

    /**
     * Set the amount of entropy the fish gives
     *
     * @param entropy The amount of entropy the fish gives
     */
    public void setCatchEntropy(int entropy) {
        this.entropy = entropy;
    }

    /**
     * The base amount of plugin experience the fish gives
     *
     * @return The base amount of experience the fish gives
     */
    public int getBaseCatchExp() {
        return this.fish.getTierInstance().getCatchExperience();
    }

    /**
     * The amount of plugin experience the fish gives
     *
     * @return The amount of experience the fish gives
     */
    public int getCatchExp() {
        return fishExp;
    }

    /**
     * Set the amount of plugin experience the fish gives
     *
     * @param fishExp The amount of experience the fish gives
     */
    public void setCatchExp(int fishExp) {
        this.fishExp = fishExp;
    }

    /**
     * The base minecraft experience the fish gives
     *
     * @return The base minecraft experience the fish gives
     */
    public float getBaseNaturalExp() {
        return this.fish.getTierInstance().getNaturalExperience();
    }

    /**
     * The base minecraft experience the fish gives
     *
     * @return The base minecraft experience the fish gives
     */
    public float getNaturalExp() {
        return naturalExp;
    }

    /**
     * Set the base minecraft experience the fish gives
     *
     * @param naturalExp The base minecraft experience the fish gives
     */
    public void setNaturalExp(float naturalExp) {
        this.naturalExp = naturalExp;
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
