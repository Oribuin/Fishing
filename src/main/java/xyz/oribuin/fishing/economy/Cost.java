package xyz.oribuin.fishing.economy;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.fishing.api.config.Configurable;
import xyz.oribuin.fishing.util.FishUtils;

public class Cost implements Currency, Configurable {

    private Currency currency;
    private Number price;

    /**
     * Create a new cost object with a currency and amount
     *
     * @param currency The currency to use
     * @param price    The amount of currency to use
     */
    public Cost(@NotNull Currency currency, @NotNull Number price) {
        this.currency = currency;
        this.price = price;
    }

    /**
     * Create a new cost object with a currency and amount
     *
     * @param currency The currency to use
     * @param price    The amount of currency to use
     *
     * @return The cost object
     */
    public static Cost of(Currency currency, Number price) {
        return new Cost(currency, price);
    }

    /**
     * Load the settings from the configuration file
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to load
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.currency = FishUtils.getEnum(Currencies.class, config.getString("type"), Currencies.VAULT).get();
        this.price = config.getDouble("price", this.price.doubleValue());
    }

    /**
     * Save the configuration file for the configurable class
     * I would recommend always super calling this method to save any settings that could be implemented
     *
     * @param config The configuration file to save
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("type", this.currency.name());
        config.set("price", this.price.doubleValue());
    }

    /**
     * @return The name of the currency
     */
    @Override
    public String name() {
        return this.currency.name();
    }

    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
     */
    @Override
    public @NotNull Number amount(@NotNull OfflinePlayer player) {
        return this.currency.amount(player);
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
        return this.currency.has(player, amount);
    }

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     * @param amount The amount to give
     */
    @Override
    public void give(@NotNull OfflinePlayer player, @NotNull Number amount) {
        this.currency.give(player, amount);
    }

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     * @param amount The amount to take
     */
    @Override
    public void take(@NotNull OfflinePlayer player, @NotNull Number amount) {
        this.currency.take(player, amount);
    }

    /**
     * @return The price of the item
     */
    public Number price() {
        return this.price;
    }

    /**
     * Add a price to the item cost
     *
     * @param price The price to add
     */
    public void price(Number price) {
        this.price = price;
    }

}
