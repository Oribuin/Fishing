package xyz.oribuin.fishing.api.event;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Fundamental context for all fishing events. This context is just global information for the event that is quite universal
 *
 * @param player    The {@link Player} who caught the fish
 * @param itemStack The {@link ItemStack} of the fishing rod the player is using
 * @param hook      The {@link FishHook} entity that the fish was caught on
 * @param level     The level of the ability that the player has used while fishing
 */
public record FishContext(Player player, ItemStack itemStack, FishHook hook, int level) {}
