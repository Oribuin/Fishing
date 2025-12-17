package dev.oribuin.fishing.model.economy;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The cost of an item or upgrade in the fishing plugin
 */
@ConfigSerializable
@SuppressWarnings({ "FieldsMaybeFinal", "FieldCanBeLocal" })
public class Cost {

    private String currencyName;
    private Number price;
    private final transient Currency<?> currency;

    /**
     * Create a new cost object with a currency and amount
     */
    public Cost() {
        this.currencyName = "entropy";
        this.currency = CurrencyRegistry.ENTROPY;
        this.price = 0;
    }

    /**
     * Create a new cost object with a currency and amount
     *
     * @param currency The currency to use
     * @param price    The amount of currency to use
     */
    public Cost(@NotNull Currency<?> currency, @NotNull Number price) {
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
    public static <T> Cost of(@NotNull Currency<T> currency, @NotNull Number price) {
        return new Cost(currency, price);
    }

    /**
     * Get the currency of the cost
     *
     * @return The currency of the cost
     */
    public Currency<?> currency() {
        return currency;
    }

    /**
     * The price of the item to purchase
     *
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
