package dev.oribuin.fishing.hook.plugin.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class PointsProvider implements EconomyPlugin {

    private static PointsProvider instance;
    private PlayerPointsAPI api;

    public PointsProvider() {
        if (this.enabled()) {
            this.api = PlayerPoints.getInstance().getAPI();
        }
    }

    /**
     * Check if the economy plugin is enabled
     *
     * @return If the economy plugin is enabled
     */
    @Override
    public boolean enabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlayerPoints");
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

        return this.api.take(player.getUniqueId(), (int) amount);
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

        return this.api.give(player.getUniqueId(), (int) amount);
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

        return this.api.look(player.getUniqueId());
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

        return this.api.look(player.getUniqueId()) >= amount;
    }

    /**
     * Get the instance of the economy plugin
     */
    public static PointsProvider get() {
        if (instance == null)
            instance = new PointsProvider();

        return instance;
    }

}