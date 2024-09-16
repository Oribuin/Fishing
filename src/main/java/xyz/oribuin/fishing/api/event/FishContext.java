package xyz.oribuin.fishing.api.event;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The context of any fish event for any Fish Event Handler to use
 *
 * @param player    The player who caught the fish
 * @param itemStack The item stack of the fish
 * @param hook      The fishhook entity
 * @param level     The level of the augment/skill/upgrade that was used
 */
public record FishContext(Player player, ItemStack itemStack, FishHook hook, int level) {

    // Unused

}
