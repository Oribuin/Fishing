package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.config.Configurable;
import dev.oribuin.fishing.util.FishUtils;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The cost of an item or upgrade in the fishing plugin
 */
public class Cost implements Configurable {

    private Currency<?> currency;
    private Number price;

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
     * Create a new cost object with a currency and amount
     *
     * @param config The configuration section to load the settings from
     *
     * @return The cost object
     */
    public static Cost of(CommentedConfigurationSection config) {
        Cost cost = new Cost(CurrencyRegistry.ENTROPY, Integer.MAX_VALUE);
        cost.loadSettings(config);
        return cost;
    }

    /**
     * Initialize a {@link CommentedConfigurationSection} from a configuration file to establish the settings
     * for the configurable class, will be automatically called when the configuration file is loaded using {@link #reload()}
     * <p>
     * If your class inherits from another configurable class, make sure to call super.loadSettings(config)
     * to save the settings from the parent class
     * <p>
     * A class must be initialized before settings are loaded, If you wish to have a configurable data class style, its best to create a
     * static method that will create a new instance and call this method on the new instance
     * <p>
     * The {@link CommentedConfigurationSection} should never be null, when creating a new section,
     * use {@link #pullSection(CommentedConfigurationSection, String)} to establish new section if it doesn't exist
     *
     * @param config The {@link CommentedConfigurationSection} to load the settings from, this cannot be null.
     */
    @Override
    public void loadSettings(@NotNull CommentedConfigurationSection config) {
        this.currency = CurrencyRegistry.get(config.getString("type"));
        this.price = config.getDouble("price", this.price.doubleValue());
    }

    /**
     * Serialize the settings of the configurable class into a {@link CommentedConfigurationSection} to be saved later
     * <p>
     * This functionality will not update the configuration file, it will only save the settings into the section to be saved later.
     * <p>
     * The function {@link #reload()} will save the settings on first load, please override this method if you wish to save the settings regularly
     * New sections should be created using {@link #pullSection(CommentedConfigurationSection, String)}
     *
     * @param config The {@link CommentedConfigurationSection} to save the settings to, this cannot be null.
     */
    @Override
    public void saveSettings(@NotNull CommentedConfigurationSection config) {
        config.set("type", this.currency.name());
        config.set("price", this.price.doubleValue());
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
