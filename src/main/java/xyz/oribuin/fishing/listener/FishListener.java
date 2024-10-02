package xyz.oribuin.fishing.listener;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.oribuin.fishing.FishingPlugin;
import xyz.oribuin.fishing.fish.Fish;
import xyz.oribuin.fishing.fish.Tier;
import xyz.oribuin.fishing.manager.DataManager;
import xyz.oribuin.fishing.manager.FishManager;
import xyz.oribuin.fishing.manager.LocaleManager;
import xyz.oribuin.fishing.storage.Fisher;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

        // TODO: Run the augments for the fishing rod
        // TODO: Augment: Increase xp when catching fish
        // TODO: Reward player with entropy on catch, sometimes

        // Add the fish into the player inventory
        double baseExpGained = event.getExpToDrop();
        double naturalExp = 0.0;
        double newFishExp = 0.0;
        int newEntropy = 0;

        for (Fish fish : caught) {
            if (fish == null) continue;

            Tier tier = fish.tier();
            if (fish.tier() == null) continue;

            naturalExp += baseExpGained * tier.naturalExp();
            newFishExp += tier.fishExp();
            newEntropy += tier.entropy();

            // Tell the player they caught a fish
            locale.sendMessage(event.getPlayer(), "fish-caught", StringPlaceholders.of("fish", fish.displayName()));

            // Give the fish to the player
            PlayerInventory inv = event.getPlayer().getInventory();
            if (inv.firstEmpty() == -1) {
                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), fish.createItemStack());
                continue;
            }

            inv.addItem(fish.createItemStack());
        }

        // Apply more exp to the player
        event.setExpToDrop((int) naturalExp);

        // TODO: Give the player entropy
        // TODO: Give the player statistics

//        Fisher fisher = this.plugin.getManager(DataManager.class).get

    }

}
