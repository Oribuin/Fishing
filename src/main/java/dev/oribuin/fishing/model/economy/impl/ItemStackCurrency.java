package dev.oribuin.fishing.model.economy.impl;

import dev.oribuin.fishing.model.economy.Currency;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStackCurrency implements Currency<ItemStack> {
    
    /**
     * Get the amount of currency the player has
     *
     * @param player  The player to check
     * @param item The currency type to check
     *
     * @return The amount of currency the player has
     */
    @Override
    public @NotNull Number amount(@NotNull OfflinePlayer player, @NotNull ItemStack item) {
        PlayerInventory inventory = this.getInventory(player);
        if (inventory == null) return 0;

        int amount = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || !itemStack.isSimilar(item)) continue;
            amount += itemStack.getAmount();
        }

        return amount;
    }

    /**
     * Check if the player has enough currency to purchase an item
     *
     * @param player The player who is purchasing the item
     * @param item   The item to check
     *
     * @return If the player has enough currency
     */
    @Override
    public boolean has(@NotNull OfflinePlayer player, @NotNull ItemStack item) {
        PlayerInventory inventory = this.getInventory(player);
        if (inventory == null) return false;

        // Use a different check to avoid calculating when unnecessary
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.isSimilar(item)) return true;
        }

        return false;
    }

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     * @param item   The item to give
     */
    @Override
    public void give(@NotNull OfflinePlayer player, @NotNull ItemStack item) {
        PlayerInventory inventory = this.getInventory(player);
        if (inventory == null) return;


        int remaining = item.getAmount();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || !itemStack.isSimilar(item)) continue;

            int max = item.getMaxStackSize();
            int give = Math.min(remaining, max - itemStack.getAmount());
            itemStack.setAmount(itemStack.getAmount() + give);
            remaining -= give;

            if (remaining <= 0) break;
        }

        while (remaining > 0) {
            int give = Math.min(remaining, item.getMaxStackSize());
            ItemStack clone = item.clone();
            clone.setAmount(give);
            inventory.addItem(clone);
            remaining -= give;
        }
    }

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     * @param item   The item to take
     */
    @Override
    public void take(@NotNull OfflinePlayer player, @NotNull ItemStack item) {
        PlayerInventory inventory = this.getInventory(player);
        if (inventory == null) return;

        int remaining = item.getAmount();
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || !itemStack.isSimilar(item)) continue;

            int take = Math.min(remaining, itemStack.getAmount());
            itemStack.setAmount(itemStack.getAmount() - take);
            remaining -= take;

            if (remaining <= 0) break;
        }
    }

    /**
     * Get the offline player's current inventory of the item
     *
     * @return The player's inventory
     */
    @Nullable
    public PlayerInventory getInventory(@NotNull OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) return null;

        return player.getInventory();
    }

    /**
     * The name of the currency type (e.g. "Dollars")
     *
     * @return The name
     */
    @Override
    public String name() {
        return "item";
    }

}
