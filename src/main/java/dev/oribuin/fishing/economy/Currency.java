package dev.oribuin.fishing.economy;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import dev.oribuin.fishing.FishingPlugin;
import dev.oribuin.fishing.manager.base.DataManager;
import dev.oribuin.fishing.storage.Fisher;

public interface Currency {

    /**
     * @return The name of the currency
     */
    String name();

    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
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
     */
    default Fisher fisher(@NotNull OfflinePlayer player) {
        return FishingPlugin.get().getManager(DataManager.class).get(player.getUniqueId());
    }

}
