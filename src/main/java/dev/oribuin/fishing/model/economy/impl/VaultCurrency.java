package dev.oribuin.fishing.model.economy.impl;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.model.economy.Currency;

public class VaultCurrency implements Currency {

    private static final RegisteredServiceProvider<Economy> PROVIDER = Bukkit.getServicesManager().getRegistration(Economy.class);
    private static final Economy API = PROVIDER != null ? PROVIDER.getProvider() : null;

    /**
     * @return The name of the currency
     */
    @Override
    public String name() {
        return "vault";
    }

    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
     */
    @Override
    public @NotNull Number amount(@NotNull OfflinePlayer player) {
        return API != null ? API.getBalance(player) : 0.0;
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
        return API != null && API.has(player, amount.doubleValue());
    }

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     * @param amount The amount to give
     */
    @Override
    public void give(@NotNull OfflinePlayer player, @NotNull Number amount) {
        if (API != null) API.depositPlayer(player, amount.doubleValue());
    }

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     * @param amount The amount to take
     */
    @Override
    public void take(@NotNull OfflinePlayer player, @NotNull Number amount) {
        if (API != null) API.withdrawPlayer(player, amount.doubleValue());
    }

}
