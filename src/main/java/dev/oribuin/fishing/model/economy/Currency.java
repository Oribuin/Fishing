package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.manager.DataManager;
import dev.oribuin.fishing.storage.Fisher;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a currency type in the plugin economy
 *
 * @param <T> The type of currency
 */
public interface Currency<T> {

    /**
     * The name of the currency type (e.g. "Dollars")
     *
     * @return The name
     */
    String name();

    /**
     * Get the amount of currency the player has
     *
     * @param player  The player to check
     * @param content The currency type to check
     *
     * @return The amount of currency the player has
     */
    @NotNull
    Number amount(@NotNull OfflinePlayer player, @NotNull T content);

    /**
     * Check if the player has enough currency to purchase an item
     *
     * @param player  The player who is purchasing the item
     * @param content The amount to check
     *
     * @return If the player has enough currency
     */
    boolean has(@NotNull OfflinePlayer player, @NotNull T content);

    /**
     * Give the player an amount of currency
     *
     * @param player  The player to give the currency to
     * @param content The amount to give
     */
    void give(@NotNull OfflinePlayer player, @NotNull T content);

    /**
     * Take an amount of currency from the player
     *
     * @param player  The player to take the currency from
     * @param content The amount to take
     */
    void take(@NotNull OfflinePlayer player, @NotNull T content);
    
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
