package xyz.oribuin.fishing.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.fishing.api.event.FishGenerateEvent;
import xyz.oribuin.fishing.api.event.InitialFishCatchEvent;
import xyz.oribuin.fishing.augment.Augment;
import xyz.oribuin.fishing.augment.AugmentRegistry;
import xyz.oribuin.fishing.fish.Fish;

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
        event.callEvent();

        // Run the augments onInitialCatch method
        augments.forEach((augment, integer) -> augment.onInitialCatch(event, integer));

        // Cancel the event if it is cancelled
        if (event.isCancelled()) return result;

        for (int i = 0; i < event.getAmountToCatch(); i++) {
            result.add(this.generateFish(player, rod, hook));
        }

        return result;
    }

    /**
     * Fires the {@link FishGenerateEvent} and returns the fish
     * This generates its own fish that can be overridden by augments or other plugins.
     *
     * @param player The player to check
     * @param rod    The fishing rod the player is using
     * @param hook   The fishhook the player is using
     *
     * @return The fish the player caught
     */
    public Fish generateFish(Player player, ItemStack rod, FishHook hook) {
        FishGenerateEvent event = new FishGenerateEvent(player, rod, hook);
        Bukkit.getPluginManager().callEvent(event);

        event.generate();
        if (event.isCancelled()) return null;
        if (event.fish() == null) return null;

        return event.fish();
    }

    @Override
    public void disable() {

    }

}
