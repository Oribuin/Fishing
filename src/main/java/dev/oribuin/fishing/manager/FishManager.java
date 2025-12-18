package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.fish.Fish;
import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishManager implements Manager {
    
    private final FishingPlugin plugin;

    public FishManager(FishingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The task that runs when the plugin is loaded/reloaded
     *
     * @param plugin The plugin reloading
     */
    @Override
    public void reload(FishingPlugin plugin) {

    }

    /**
     * The task that runs when the plugin is disabled, usually takes priority over {@link Manager#reload(FishingPlugin)}
     *
     * @param plugin The plugin being disabled
     */
    @Override
    public void disable(FishingPlugin plugin) {

    }

    /**
     * Try to catch a fish from the tier based on the player's fishing rod and fishhook
     *
     * @param event The initial player fish event to catch the fish
     *
     * @return The fish the player caught
     */
    public List<Fish> tryCatch(PlayerFishEvent event) {
        if (event.getHand() == null) return new ArrayList<>();

        List<Fish> result = new ArrayList<>();
        ItemStack rod = event.getPlayer().getInventory().getItem(event.getHand());
        Map<Augment, Integer> augments = this.plugin.getAugmentManager().from(rod);
        Totem nearest = this.plugin.getTotemManager().getClosestActive(event.getHook().getLocation());
        if (event.isCancelled()) return result; // Cancel the event if it is cancelled

        InitialFishCatchEvent catchEvent = new InitialFishCatchEvent(event.getPlayer(), rod, event.getHook());

        // Run the augments onInitialCatch method
        FishEventHandler.callEvents(augments, catchEvent);

        // Run Totem Stuff
        if (nearest != null) {
            FishEventHandler.callEvents(nearest.upgrades(), catchEvent);
        }

        // Cancel the event if it is cancelled
        if (catchEvent.isCancelled()) return result;

        for (int i = 0; i < catchEvent.getAmountToCatch(); i++) {
            result.add(this.generateFish(augments, event.getPlayer().getPlayer(), rod, event.getHook()));
        }
        return result;
    }

    /**
     * Fires the {@link FishGenerateEvent} and returns the fish
     * This generates its own fish that can be overridden by augments or other plugins.
     *
     * @param augments The augments on the fishing rod
     * @param player   The player to check
     * @param rod      The fishing rod the player is using
     * @param hook     The fishhook the player is using
     *
     * @return The fish the player caught
     */
    public Fish generateFish(Map<Augment, Integer> augments, Player player, ItemStack rod, FishHook hook) {
        FishGenerateEvent event = new FishGenerateEvent(player, rod, hook);
        event.generate(); // Generate the fish

        FishEventHandler.callEvents(augments, event); // Call the augments
        if (event.isCancelled()) return null;

        return event.fish();
    }
    
}
