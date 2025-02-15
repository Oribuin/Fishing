package dev.oribuin.fishing.manager;

import dev.oribuin.fishing.api.event.FishEventHandler;
import dev.oribuin.fishing.api.event.impl.FishGenerateEvent;
import dev.oribuin.fishing.api.event.impl.InitialFishCatchEvent;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.augment.AugmentRegistry;
import dev.oribuin.fishing.model.fish.Fish;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FishManager extends Manager {

    public FishManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
    }

    /**
     * Try to catch a fish from the tier based on the player's fishing rod and fishhook
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return The fish the player caught
     */
    public List<Fish> tryCatch(Player player, ItemStack rod, FishHook hook) {
        List<Fish> result = new ArrayList<>();
        Map<Augment, Integer> augments = AugmentRegistry.from(rod);

        InitialFishCatchEvent event = new InitialFishCatchEvent(player, rod, hook);

        // Run the augments onInitialCatch method
        FishEventHandler.callEvents(augments, event);

        // Cancel the event if it is cancelled
        if (event.isCancelled()) return result;

        for (int i = 0; i < event.getAmountToCatch(); i++) {
            result.add(this.generateFish(augments, player, rod, hook));
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

    @Override
    public void disable() {

    }

}
