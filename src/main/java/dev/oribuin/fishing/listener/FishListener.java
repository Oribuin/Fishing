package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventWrapper;
import dev.oribuin.fishing.api.event.impl.FailCatchEvent;
import dev.oribuin.fishing.api.event.impl.FishBiteEvent;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.api.event.impl.RodCastEvent;
import dev.oribuin.fishing.config.impl.PluginMessages;
import dev.oribuin.fishing.config.impl.Settings;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dev.oribuin.fishing.storage.util.KeyRegistry.STAT_ROD_CAUGHT;

public class FishListener implements Listener {

    private final FishingPlugin plugin;

    public FishListener(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getHand() == null) return;

        ItemStack hand = event.getPlayer().getInventory().getItem(event.getHand());
        Map<String, Augment> augments = this.plugin.getAugmentManager().getAugments(hand);
        Totem nearby = this.plugin.getTotemManager().getClosestActive(event.getHook().getLocation());

        FishEventWrapper eventWrapper = new FishEventWrapper(
                event.getPlayer(),
                event.getHook(),
                hand,
                event.getHand(),
                augments,
                nearby
        );

        // TODO: Have rod rarity impact bites hm
        switch (event.getState()) {
            case FISHING -> this.handleCustomEvent(
                    () -> new RodCastEvent(event.getPlayer(), eventWrapper),
                    eventWrapper,
                    event
            );
            case BITE -> this.handleCustomEvent(
                    () -> new FishBiteEvent(event.getPlayer(), eventWrapper),
                    eventWrapper,
                    event
            );
            case FAILED_ATTEMPT, REEL_IN -> { // failed_attempt is so inconsistent, reel in means they didnt catch anything
                this.handleCustomEvent(
                        () -> new FailCatchEvent(event.getPlayer(), eventWrapper),
                        eventWrapper,
                        event
                );
            }
            case CAUGHT_FISH -> this.catchNewFish(event, eventWrapper);
        }

    }

    /**
     * Handle a custom fishing event by passing it through the fish event wrapper & bukkit
     *
     * @param supplier  The supplier for the event
     * @param wrapper   The event wrapper
     * @param fishEvent The fishing event it probably stems from
     * @param <T>       THe type of event
     */
    private <T extends Event> void handleCustomEvent(@NotNull Supplier<@NotNull T> supplier, @NotNull FishEventWrapper wrapper, @NotNull PlayerFishEvent fishEvent) {
        T event = supplier.get();
        event.callEvent();
        wrapper.handleEvent(event);

        // If the called event is cancelled
        if (event instanceof Cancellable cancellable) {
            fishEvent.setCancelled(cancellable.isCancelled());
        }
    }

    /**
     * Catch a new type of fish
     *
     * @param event The catching event
     */
    private void catchNewFish(PlayerFishEvent event, FishEventWrapper wrapper) {
        // If caught no fish, do nothing
        List<Fish> caught = new ArrayList<>();
        InitialFishCatchEvent catchEvent = new InitialFishCatchEvent(event.getPlayer(), wrapper);

        // Run the augments onInitialCatch method
        wrapper.handleEvent(catchEvent);

        // Cancel the event if it is cancelled
        if (catchEvent.isCancelled()) return;

        for (int i = 0; i < catchEvent.getAmountToCatch(); i++) {
            caught.add(this.generateFish(wrapper));
        }

        // Add the fish into the player inventory
        float naturalExp = event.getExpToDrop();
        int newFishExp = 0;
        int newEntropy = 0;

        for (Fish fish : caught) {
            if (fish == null) continue;

            FishCatchEvent fishCatchEvent = new FishCatchEvent(event.getPlayer(), wrapper, fish);
            fishCatchEvent.setNaturalExp(naturalExp); // Set the base experience gained
            fishCatchEvent.callEvent(); // call through bukkit

            // Run the augments onInitialCatch method
            wrapper.handleEvent(fishCatchEvent);
            if (fishCatchEvent.isCancelled()) continue; // If the event is cancelled, do nothing

            // Use the event values because they could have been modified
            naturalExp += fishCatchEvent.getNaturalExp();
            newFishExp += fishCatchEvent.getCatchExp();
            newEntropy += fishCatchEvent.getCatchEntropy();

            // Tell the player they caught a fish

            ItemStack resultItem = fish.buildItem();
            PluginMessages.get().getCaughtFish().send(event.getPlayer(), "item", resultItem.displayName());

            // Give the fish to the player
            if (Settings.get().isInstantLootPickup()) {
                PlayerInventory inv = event.getPlayer().getInventory();
                if (inv.firstEmpty() == -1) {
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), resultItem);
                    continue;
                }

                inv.addItem(resultItem);
            } else {
                Item item = event.getPlayer().getWorld().dropItem(event.getHook().getLocation(), resultItem);
                Location playerLoc = event.getPlayer().getLocation().toCenterLocation();
                double x = playerLoc.getX() - item.getLocation().getX();
                double y = playerLoc.getY() - item.getLocation().getY();
                double z = playerLoc.getZ() - item.getLocation().getZ();
                Vector motion = new Vector(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
                item.setPickupDelay(10); // totems might want to intercept this :)
                item.setVelocity(motion);
            }
        }

        Fisher fisher = this.plugin.getDataManager().get(event.getPlayer().getUniqueId());
        if (fisher == null) return;

        // Append the new exp and entropy to the player
        event.setExpToDrop((int) naturalExp);
        fisher.setExperience(fisher.getExperience() + newFishExp);
        fisher.setEntropy(fisher.getEntropy() + newEntropy);
        // TODO: increase fisher's total statistics

        // Increase fishing rod statistics
        if (Settings.get().isRodStatistics()) {
            this.plugin.getRodManager().incrementStatistic(
                    wrapper.rod(),
                    STAT_ROD_CAUGHT,
                    caught.size()
            );
        }

        // Level up the player if they have enough experience
        if (fisher.canLevelUp()) {
            fisher.levelUp(); // Level up the player

            this.plugin.getDataManager().saveUser(fisher); // Save the player data on levelup
            PluginMessages.get().getLevelUp().send(fisher, "level", fisher.getLevel()); // Tell the player they leveled up
        }
    }

    /**
     * Fires the {@link FishGenerateEvent} and returns the fish
     * This generates its own fish that can be overridden by augments or other plugins.
     *
     * @param wrapper The fish event wrapper
     *
     * @return The fish the player caught
     */
    private Fish generateFish(FishEventWrapper wrapper) {
        FishGenerateEvent event = new FishGenerateEvent(wrapper.player(), wrapper);
        event.callEvent(); // Call the fish generation event

        wrapper.handleEvent(event);
        if (event.isCancelled()) return null;

        event.generate();
        return event.fish();
    }

}
