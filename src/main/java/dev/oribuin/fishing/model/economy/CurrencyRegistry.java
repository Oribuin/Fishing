package dev.oribuin.fishing.model.economy;

import dev.oribuin.fishing.model.economy.impl.EntropyCurrency;
import dev.oribuin.fishing.model.economy.impl.FishExpCurrency;
import dev.oribuin.fishing.model.economy.impl.ItemStackCurrency;
import dev.oribuin.fishing.model.economy.impl.PlayerExpCurrency;
import dev.oribuin.fishing.model.economy.impl.SkillpointCurrency;
import dev.oribuin.fishing.model.economy.impl.VaultCurrency;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * All the registered currency type in the fishing plugin
 *
 * @see Currency
 * @see Cost
 */
public class CurrencyRegistry {

    private static final Map<String, Currency<?>> registered = new HashMap<>();
    public static final Currency<Integer> ENTROPY = register(EntropyCurrency::new);
    public static final Currency<Integer> FISH_EXP = register(FishExpCurrency::new);
    public static final Currency<Integer> PLAYER_EXP = register(PlayerExpCurrency::new);
    public static final Currency<Integer> SKILLPOINTS = register(SkillpointCurrency::new);
    public static final Currency<Double> VAULT = register(VaultCurrency::new);
    public static final Currency<ItemStack> ITEMSTACK = register(ItemStackCurrency::new);

    /**
     * Register a currency type in the fishing plugin
     *
     * @param supplier The supplier to create the currency instance
     * @param <T>      The type of the currency
     *
     * @return The currency instance
     */
    public static <T> Currency<T> register(Supplier<Currency<T>> supplier) {
        Currency<T> currency = supplier.get();
        registered.put(currency.name().toLowerCase(), currency);
        return currency;
    }

    /**
     * Get a currency instance from the currency 'name'
     *
     * @param name The currency name to get
     *
     * @return The currency instance or null if not found
     */
    @Nullable
    public static Currency<?> get(@Nullable String name) {
        if (name == null) return null;

        return registered.get(name.toLowerCase());
    }

    /**
     * Get a currency instance from the currency 'name'
     *
     * @param name            The currency name to get
     * @param defaultCurrency The currency to return if the currency is not found
     * @param <T>            The type of the currency
     *
     * @return The currency instance or the default currency if not found
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Currency<T> get(@Nullable String name, @NotNull Currency<T> defaultCurrency) {
        Currency<T> currency = (Currency<T>) get(name);
        return currency == null ? defaultCurrency : currency;
    }

}
