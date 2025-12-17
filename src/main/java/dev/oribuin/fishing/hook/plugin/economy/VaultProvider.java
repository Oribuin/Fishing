package dev.oribuin.fishing.hook.plugin.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultProvider implements EconomyPlugin {

    private static VaultProvider instance;
    private Economy api;

    public VaultProvider() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (provider == null) return;

            this.api = provider.getProvider();
        }
    }

    /**
     * Check if the economy plugin is enabled
     *
     * @return If the economy plugin is enabled
     */
    @Override
    public boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault") && this.api != null;
    }

    /**
     * Take money from a player
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the transaction was successful
     */
    @Override
    public boolean take(OfflinePlayer player, double amount) {
        if (!this.enabled()) return false;

        return this.api.withdrawPlayer(player, amount).transactionSuccess();
    }

    /**
     * Give a player money
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the transaction was successful
     */
    @Override
    public boolean give(OfflinePlayer player, double amount) {
        if (!this.enabled()) return false;

        return this.api.depositPlayer(player, amount).transactionSuccess();
    }

    /**
     * Get the balance of a player
     *
     * @param player The player
     *
     * @return The balance
     */
    @Override
    public double balance(OfflinePlayer player) {
        if (!this.enabled()) return 0;

        return this.api.getBalance(player);
    }

    /**
     * Check if a player has a certain amount of money
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the player has the amount
     */
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (!this.enabled()) return false;

        return this.api.has(player, amount);
    }

    /**
     * Get the instance of the economy plugin
     */
    public static VaultProvider get() {
        if (instance == null)
            instance = new VaultProvider();

        return instance;
    }

}