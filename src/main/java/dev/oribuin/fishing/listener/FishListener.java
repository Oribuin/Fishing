package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.manager.FishManager;
import dev.oribuin.fishing.manager.TotemManager;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.totem.Totem;
import dev.oribuin.fishing.storage.Fisher;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishListener implements Listener {

    private final FishingPlugin plugin;

    public FishListener(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getHand() == null) return;

        ItemStack hand = event.getPlayer().getInventory().getItem(event.getHand()).clone();
        Map<Augment, Integer> augments = AugmentRegistry.from(hand);

        switch (event.getState()) {
            case CAUGHT_FISH -> this.catchNewFish(event, hand, augments);
            // todo: allow bite actual modification
        }

    }

    /**
     * Catch a new type of fish 
     *
     * @param event The catching event
     */
    private void catchNewFish(PlayerFishEvent event, ItemStack rod, Map<Augment, Integer> augments) {
        FishManager manager = this.plugin.getFishManager();
        TotemManager totemProvider = this.plugin.getTotemManager();

        // If caught no fish, do nothing
        List<Fish> caught = new ArrayList<>();
        InitialFishCatchEvent catchEvent = new InitialFishCatchEvent(event.getPlayer(), rod, event.getHook());
        Totem nearest = totemProvider.getClosestActive(event.getHook().getLocation());

        // Run the augments onInitialCatch method
        FishEventHandler.callEvents(augments, catchEvent);

        // Run Totem Stuff
        if (nearest != null) {
            FishEventHandler.callEvents(nearest.upgrades(), catchEvent);
        }

        // Cancel the event if it is cancelled
        if (catchEvent.isCancelled()) return;

        for (int i = 0; i < catchEvent.getAmountToCatch(); i++) {
            caught.add(manager.generateFish(augments, event.getPlayer().getPlayer(), rod, event.getHook()));
        }
        // Add the fish into the player inventory
        float naturalExp = event.getExpToDrop();
        int newFishExp = 0;
        int newEntropy = 0;

        for (Fish fish : caught) {
            if (fish == null) continue;

            FishCatchEvent fishCatchEvent = new FishCatchEvent(event.getPlayer(), rod, event.getHook(), fish);
            fishCatchEvent.naturalExp(naturalExp); // Set the base experience gained
            fishCatchEvent.callEvent(); // call through bukkit

            FishEventHandler.callEvents(augments, fishCatchEvent);
            if (fishCatchEvent.isCancelled()) continue; // If the event is cancelled, do nothing

            // Use the event values because they could have been modified
            naturalExp += fishCatchEvent.naturalExp();
            newFishExp += fishCatchEvent.fishExp();
            newEntropy += fishCatchEvent.entropy();

            // Tell the player they caught a fish
            //            locale.sendMessage(event.getPlayer(), "fish-caught", Placeholders.of("fish", fish.displayName()));

            ItemStack resultItem = fish.buildItem();
            Component message = Component.text("You have caught a ").append(resultItem.displayName()); // TODO: Replace with locale message
            event.getPlayer().sendMessage(message);

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
        fisher.experience(fisher.experience() + newFishExp);
        fisher.entropy(fisher.entropy() + newEntropy);

        // Level up the player if they have enough experience
        if (fisher.canLevelUp()) {
            fisher.levelUp(); // Level up the player

            this.plugin.getDataManager().saveUser(fisher); // Save the player data on levelup

            // Tell the player they leveled up
            event.getPlayer().sendMessage("You leveled up! You are now level " + fisher.level() + "!"); // TODO: Replace with locale message
        }
    }

}
