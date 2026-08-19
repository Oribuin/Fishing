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
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FishListener implements Listener {

    private final FishingPlugin plugin;

    public FishListener(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getHand() == null) return;

        ItemStack hand = event.getPlayer().getInventory().getItem(event.getHand()).clone();
        Map<Augment, Integer> augments = this.plugin.getAugmentManager().getAugments(hand);
        Totem nearby = this.plugin.getTotemManager().getClosestActive(event.getHook().getLocation());

        FishEventWrapper eventWrapper = new FishEventWrapper(
                event.getPlayer(),
                event.getHook(),
                hand,
                augments, nearby
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
            case FAILED_ATTEMPT -> this.handleCustomEvent(
                    () -> new FailCatchEvent(event.getPlayer(), eventWrapper),
                    eventWrapper,
                    event
            );
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
            if (!wrapper.augments().isEmpty()) wrapper.augments().keySet().forEach(augment -> augment.handleEvent(fishCatchEvent));
            if (wrapper.totem() != null && wrapper.totem().isActive()) wrapper.totem().handleEvent(fishCatchEvent);

            if (fishCatchEvent.isCancelled()) continue; // If the event is cancelled, do nothing

            // Use the event values because they could have been modified
            naturalExp += fishCatchEvent.getNaturalExp();
            newFishExp += fishCatchEvent.getCatchExp();
            newEntropy += fishCatchEvent.getCatchEntropy();

            // Tell the player they caught a fish

            ItemStack resultItem = fish.buildItem();
            PluginMessages.get().getCaughtFish().send(event.getPlayer(), "item", resultItem.displayName());

            // Give the fish to the player
            PlayerInventory inv = event.getPlayer().getInventory();
            if (inv.firstEmpty() == -1) {
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), resultItem);
                continue;
            }

            inv.addItem(resultItem);
        }

        Fisher fisher = this.plugin.getDataManager().get(event.getPlayer().getUniqueId());
        if (fisher == null) return;

        // Append the new exp and entropy to the player
        event.setExpToDrop((int) naturalExp);
        fisher.setExperience(fisher.getExperience() + newFishExp);
        fisher.setEntropy(fisher.getEntropy() + newEntropy);

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
