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
        this.currency = FishUtils.getEnum(Currencies.class, config.getString("type"), Currencies.VAULT).get();
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
