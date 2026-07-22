package dev.oribuin.fishing.api.event;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.model.augment.Augment;
import dev.oribuin.fishing.model.totem.Totem;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Create a fish event wrapper which covers all the events and important information regarding a fishing event
 *
 * @param player   The player who caught the fish
 * @param hook     The hook that was bitten by the fish
 * @param rod      The rod used to catch the fish
 * @param augments The augments equipped on the rod
 * @param totem    The nearby totem to the rod
 */
public record FishEventWrapper(Player player, FishHook hook, ItemStack rod, Map<Augment, Integer> augments, Totem totem) {

    /**
     * Create a fish event wrapper which covers all the events and important information regarding a fishing event
     *
     * @param player The player who caught the fish
     * @param hook   The hook that was bitten by the fish
     * @param rod    The rod used to catch the fish
     */
    public FishEventWrapper(Player player, ItemStack rod, FishHook hook) {
        this(
                player,
                hook,
                rod,
                FishingPlugin.get().getAugmentManager().from(rod),
                FishingPlugin.get().getTotemManager().getClosestActive(hook.getLocation())
        );
    }

    /**
     * Handle the event for a specified fishing event
     *
     * @param event The event to handle
     * @param <T>   The type of event being handled
     */
    public <T extends Event> void handleEvent(T event) {
        if (this.augments != null && !this.augments.isEmpty()) {
            this.augments.keySet().forEach(x -> x.handleEvent(event));
        }

        if (this.totem != null && this.totem.isActive()) {
            this.totem.handleEvent(event);
        }
    }
}
