package dev.oribuin.fishing.economy.impl;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.economy.Currency;

public class PlayerExpCurrency implements Currency {

    /**
     * @return The name of the currency
     */
    @Override
    public String name() {
        return "player_exp";
    }

    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
     */
    @Override
    public @NotNull Number amount(@NotNull OfflinePlayer player) {
        Player online = player.getPlayer();
        if (online == null) return 0;

        return online.getTotalExperience();
    }

    /**
     * Check if the player has enough currency to purchase an item
     *
     * @param player The player who is purchasing the item
     * @param amount The amount to check
     *
     * @return If the player has enough currency
     */
    @Override
    public boolean has(@NotNull OfflinePlayer player, @NotNull Number amount) {
        return this.amount(player).intValue() >= amount.intValue();
    }

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     * @param amount The amount to give
     */
    @Override
    public void give(@NotNull OfflinePlayer player, @NotNull Number amount) {
        Player online = player.getPlayer();
        if (online == null) return;

        online.giveExp(amount.intValue());
    }

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     * @param amount The amount to take
     */
    @Override
    public void take(@NotNull OfflinePlayer player, @NotNull Number amount) {
        Player online = player.getPlayer();
        if (online == null) return;

        online.giveExp(-amount.intValue());
    }

}
