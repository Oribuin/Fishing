package xyz.oribuin.fishing.api.economy;

/**
 * Represents a cost for an item
 *
 * @param currency The currency type
 * @param amount   The amount of currency
 * @param symbol   The symbol of the currency
 */
public record Cost(Currency currency, int amount, String symbol) {

    /**
     * Create a new cost object
     *
     * @param currency The currency type
     * @param amount   The amount of currency
     */
    public Cost(Currency currency, int amount) {
        this(currency, amount, "");
    }

    /**
     * Check if the player has enough currency
     *
     * @param amount The amount to check
     *
     * @return If the player has enough currency
     */
    public boolean has(int amount) {
        return this.amount >= amount;
    }

}
