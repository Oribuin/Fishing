
package dev.oribuin.fishing.model.fish;

import org.bukkit.inventory.ItemStack;

public record GuttedFish(Fish fish, Tier tier, int amount, ItemStack stack) {

}