package dev.oribuin.fishing.listener;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.impl.FishCatchEvent;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.manager.FishManager;
import dev.oribuin.fishing.manager.LocaleManager;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class FishListener implements Listener {

    private final FishingPlugin plugin;
    private final LocaleManager locale;

    public FishListener(FishingPlugin plugin) {
        this.plugin = plugin;
        this.locale = plugin.getManager(LocaleManager.class);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (event.getHand() == null) return;

        ItemStack hand = event.getPlayer().getInventory().getItem(event.getHand()).clone();
        FishManager manager = this.plugin.getManager(FishManager.class);

        // If caught no fish, do nothing
        List<Fish> caught = manager.tryCatch(event.getPlayer(), hand, event.getHook());
        if (caught.isEmpty()) return;

        // Add the fish into the player inventory
        float naturalExp = event.getExpToDrop();
        int newFishExp = 0;
        int newEntropy = 0;

        for (Fish fish : caught) {
            if (fish == null) continue;

            FishCatchEvent fishCatchEvent = new FishCatchEvent(event.getPlayer(), hand, event.getHook(), fish);
            fishCatchEvent.naturalExp(naturalExp); // Set the base experience gained
            AugmentRegistry.callEvent(AugmentRegistry.from(hand), fishCatchEvent);

            if (fishCatchEvent.isCancelled()) continue; // If the event is cancelled, do nothing

            // Use the event values because they could have been modified
            naturalExp += fishCatchEvent.naturalExp();
            newFishExp += fishCatchEvent.fishExp();
            newEntropy += fishCatchEvent.entropy();

            // Tell the player they caught a fish
            event.getPlayer().sendMessage("You caught a " + fish.displayName() + "!"); // TODO: Replace with locale message
            //            locale.sendMessage(event.getPlayer(), "fish-caught", StringPlaceholders.of("fish", fish.displayName()));

            ItemStack resultItem = fish.createItemStack();

            // Give the fish to the player
            PlayerInventory inv = event.getPlayer().getInventory();
            if (inv.firstEmpty() == -1) {
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), resultItem);
                continue;
            }

            inv.addItem(resultItem);
        }

        Fisher fisher = this.plugin.getManager(DataManager.class).get(event.getPlayer().getUniqueId());
        if (fisher == null) return;

        // Append the new exp and entropy to the player
        event.setExpToDrop((int) naturalExp);
        fisher.experience(fisher.experience() + newFishExp);
        fisher.entropy(fisher.entropy() + newEntropy);

        // Level up the player if they have enough experience
        if (fisher.canLevelUp()) {
            fisher.levelUp(); // Level up the player

            this.plugin.getManager(DataManager.class).saveUser(fisher); // Save the player data on levelup

            // Tell the player they leveled up
            event.getPlayer().sendMessage("You leveled up! You are now level " + fisher.level() + "!"); // TODO: Replace with locale message
        }
    }

}
