package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a currency type in the plugin economy
 */
public interface Currency {

    /**
     * The name of the currency type (e.g. "Dollars")
     *
     * @return The name
     */
    String name();

    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
     *
     * @return The amount of currency the player has
     */
    @NotNull
    Number amount(@NotNull OfflinePlayer player);

    /**
     * Check if the player has enough currency to purchase an item
     *
     * @param player The player who is purchasing the item
     * @param amount The amount to check
     *
     * @return If the player has enough currency
     */
    boolean has(@NotNull OfflinePlayer player, @NotNull Number amount);

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     * @param amount The amount to give
     */
    void give(@NotNull OfflinePlayer player, @NotNull Number amount);

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     * @param amount The amount to take
     */
    void take(@NotNull OfflinePlayer player, @NotNull Number amount);

    /**
     * Get the Fisher object for the player
     *
     * @param player The player to get the Fisher object for
     *
     * @return The Fisher object for the player
     */
    default Fisher fisher(@NotNull OfflinePlayer player) {
        return FishingPlugin.get().getManager(DataManager.class).get(player.getUniqueId());
    }

}
