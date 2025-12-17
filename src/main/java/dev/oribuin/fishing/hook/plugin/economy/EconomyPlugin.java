package dev.oribuin.fishing.hook.plugin.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyPlugin {

    /**
     * Check if the economy plugin is enabled
     *
     * @return If the economy plugin is enabled
     */
    boolean enabled();

    /**
     * Take money from a player
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the transaction was successful
     */
    boolean take(OfflinePlayer player, double amount);

    /**
     * Give a player money
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the transaction was successful
     */
    boolean give(OfflinePlayer player, double amount);


    /**
     * Get the balance of a player
     *
     * @param player The player
     *
     * @return The balance
     */
    double balance(OfflinePlayer player);

    /**
     * Check if a player has a certain amount of money
     *
     * @param player The player
     * @param amount The amount
     *
     * @return If the player has the amount
     */
    boolean has(OfflinePlayer player, double amount);

}
