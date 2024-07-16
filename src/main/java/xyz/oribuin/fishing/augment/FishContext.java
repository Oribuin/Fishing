package xyz.oribuin.fishing.augment;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The context of any fish event for the augments to use
 *
 * @param player    The player who caught the fish
 * @param itemStack The item stack of the fish
 * @param hook      The fishhook entity
 */
public record FishContext(Player player, ItemStack itemStack, FishHook hook) {

    // Unused

}
