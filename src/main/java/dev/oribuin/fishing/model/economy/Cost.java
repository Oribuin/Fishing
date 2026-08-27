package dev.oribuin.fishing.model.economy;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The cost of an item or upgrade in the fishing plugin
 */
@ConfigSerializable
@SuppressWarnings({ "FieldsMaybeFinal", "FieldCanBeLocal" })
public class Cost<T> {

    private final T price;
    private final transient Currency<T> currency;

    /**
     * Create a new cost object with a currency and amount
     */
    @SuppressWarnings("unchecked")
    public Cost() {
        this.currency = (Currency<T>) CurrencyRegistry.ENTROPY;
        this.price = this.currency.getEmpty().get();
    }

    /**
     * Create a new cost object with a currency and amount
     *
     * @param currency The currency to use
     * @param price    The amount of currency to use
     */
    public Cost(@NotNull Currency<T> currency, @NotNull T price) {
        this.currency = currency;
        this.price = price;
    }

    /**
     * Create a new cost object with a currency and amount
     *
     * @param currency The currency to use
     * @param price    The amount of currency to use
     * @param <T>      The type of currency
     *
     * @return The cost object
     */
    public static <T> Cost<T> of(@NotNull Currency<T> currency, @NotNull T price) {
        return new Cost<>(currency, price);
    }

    public String getFormatted() {
        return "x" + this.price + " " + this.currency.name(); // todo: Currency.format(price)
    }

    /**
     * Get the currency of the cost
     *
     * @return The currency of the cost
     */
    public Currency<T> getCurrency() {
        return currency;
    }

    public T getPrice() {
        return price;
    }


    /**
     * Get the amount of currency the player has
     *
     * @param player The player to check
     *
     * @return The amount of currency the player has
     */
    public @NotNull Number amount(@NotNull OfflinePlayer player) {
        return this.currency.amount(player, this.price);
    }

    /**
     * Check if the player has enough currency to purchase an item
     *
     * @param player The player who is purchasing the item
     *
     * @return If the player has enough currency
     */
    public boolean has(@NotNull OfflinePlayer player) {
        return this.currency.has(player, this.price);
    }

    /**
     * Give the player an amount of currency
     *
     * @param player The player to give the currency to
     */
    public void give(@NotNull OfflinePlayer player) {
        this.currency.give(player, this.price);
    }

    /**
     * Take an amount of currency from the player
     *
     * @param player The player to take the currency from
     */
    public void take(@NotNull OfflinePlayer player) {
        this.currency.take(player, this.price);
    }

    @Override
    public String toString() {
        return "Cost{" +
               "price=" + price +
               ", currency=" + currency.name() +
               '}';
    }
}
