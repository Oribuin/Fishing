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
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.manager.AugmentManager;
import xyz.oribuin.fishing.manager.FishManager;
import xyz.oribuin.fishing.manager.TierManager;
import xyz.oribuin.fishing.util.FishUtils;

import java.util.List;
import java.util.Map;

public class FishGenerateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final @NotNull ItemStack rod;
    private final @NotNull FishHook hook;
    private @Nullable Fish fish;
    private double baseChance;
    private boolean cancelled;

    /**
     * This event is fired whenever the plugin attempts to generate a new fish.
     * This will provide a list of valid fish that it can catch and select a random one
     * This method will also provide all the augments the rod has and other stuff.
     *
     * @param who  The player who caught the fish
     * @param rod  The fishing rod used
     * @param hook The hook where the fish will spawn
     */
    public FishGenerateEvent(@NotNull Player who, @NotNull ItemStack rod, @NotNull FishHook hook) {
        super(who, !Bukkit.isPrimaryThread());

        this.rod = rod;
        this.hook = hook;
        this.baseChance = FishUtils.RANDOM.nextDouble(100);
        this.selectFish();
    }

    /**
     * Pulls the list of augments that a player has equipped on their fishing rod.
     *
     * @return The list of augments used
     */
    public Map<Augment, Integer> augments() {
        return AugmentManager.getAugments(this.rod);
    }

    /**
     * The default method for generating a new fish that is initially provided
     */
    private void selectFish() {
        FishManager fishProvider = FishingPlugin.get().getManager(FishManager.class);
        TierManager tierProvider = FishingPlugin.get().getManager(TierManager.class);

        // Obtain the quality of the
        Tier quality = tierProvider.selectTier(this.baseChance);
        if (quality == null) return;

        // Make sure the quality is not null
        List<Fish> fishList = fishProvider.getFishByTier(quality)
                .stream()
                .filter(f -> f.canCatch(player, rod, hook))
                .toList();

        if (fishList.isEmpty()) return;

        // Pick a random fish from the list
        this.fish = fishList.get(FishUtils.RANDOM.nextInt(fishList.size()));
    }

    public @NotNull ItemStack getRod() {
        return rod;
    }

    public @NotNull FishHook getHook() {
        return hook;
    }

    public double getBaseChance() {
        return baseChance;
    }

    public void setBaseChance(double baseChance) {
        this.baseChance = baseChance;
    }

    public @Nullable Fish getFish() {
        return fish;
    }

    public void setFish(@Nullable Fish fish) {
        this.fish = fish;
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
